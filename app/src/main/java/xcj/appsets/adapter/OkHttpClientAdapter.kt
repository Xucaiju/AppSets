package xcj.appsets.adapter

import android.content.Context
import android.util.Log
import com.dragons.aurora.playstoreapiv2.AuthException
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI
import com.dragons.aurora.playstoreapiv2.GooglePlayException
import com.dragons.aurora.playstoreapiv2.HttpClientAdapter
import okhttp3.*
import okhttp3.Headers.Companion.toHeaders
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import xcj.appsets.exception.AppNotFoundException
import xcj.appsets.exception.MalformedRequestException
import xcj.appsets.exception.TooManyRequestsException
import xcj.appsets.exception.UnknownException
import xcj.appsets.util.getNetworkProxy
import xcj.appsets.util.isNetworkProxyEnabled
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.*
import java.util.concurrent.TimeUnit

class OkHttpClientAdapter(context: Context) : HttpClientAdapter() {
    private val client: OkHttpClient
    @Throws(IOException::class)
    override fun get(
        url: String,
        params: Map<String, String>,
        headers: Map<String, String>
    ): ByteArray? {
        val requestBuilder = Request.Builder().url(buildUrl(url, params)).get()
        return request(requestBuilder, headers)
    }

    @Throws(IOException::class)
    override fun getEx(
        url: String,
        params: Map<String, List<String>>,
        headers: Map<String, String>
    ): ByteArray? {
        return request(Request.Builder().url(buildUrlEx(url, params)).get(), headers)
    }

    @Throws(IOException::class)
    override fun postWithoutBody(
        url: String,
        urlParams: Map<String, String>,
        headers: MutableMap<String, String>
    ): ByteArray? {
        return post(
            buildUrl(url, urlParams),
            HashMap<String, String>(),
            headers)
    }

    @Throws(IOException::class)
    override fun post(url: String, params: Map<String, String>, headers: MutableMap<String, String>): ByteArray? {
        headers["Content-Type"] = "application/x-www-form-urlencoded; charset=UTF-8"
        val bodyBuilder: FormBody.Builder = FormBody.Builder()
        if (null != params && params.isNotEmpty()) {
            for (name in params.keys) {
                bodyBuilder.add(name, params[name] ?: error(""))
            }
        }
        val requestBuilder: Request.Builder = Request.Builder().url(url).post(bodyBuilder.build())
        return post(url, requestBuilder, headers)
    }

    @Throws(IOException::class)
    override fun post(url: String, body: ByteArray, headers: MutableMap<String, String>): ByteArray? {
        if (!headers.containsKey("Content-Type")) {
            headers["Content-Type"] = "application/x-protobuf"
        }
        val requestBuilder: Request.Builder = Request.Builder().url(url).post(body.toRequestBody("application/x-protobuf".toMediaTypeOrNull(),0,body.size))
        return post(url, requestBuilder, headers)
    }

    @Throws(IOException::class)
    fun post(url: String, requestBuilder: Request.Builder, headers: Map<String, String>): ByteArray? {
        requestBuilder.url(url)
        return request(requestBuilder, headers)
    }

    @Throws(IOException::class)
    private fun request(requestBuilder: Request.Builder, headers: Map<String, String>): ByteArray? {

        val request: Request = requestBuilder.headers(headers.toHeaders()).build()

        Log.d("请求的URL","request.url==${request.url}")
        var response: Response?=null
        try {
            response = client.newCall(request).execute()
        } catch (e: SocketTimeoutException) {
            println("Socket连接超时")
        }

        val code: Int? = response?.code

        val content:ByteArray? = response?.body?.bytes()
        if (code == 401 || code == 403) {
            val authException = AuthException("Auth error", code)
            val authResponse =
                GooglePlayAPI.parseResponse(String(content!!))
            if (authResponse.containsKey("Error") && authResponse["Error"] == "NeedsBrowser") {
                authException.twoFactorUrl = authResponse["Url"]
            }
            throw authException
        } else if (code == 404) {
            val authResponse =
                GooglePlayAPI.parseResponse(String(content!!))
            if (authResponse.containsKey("Error") && authResponse["Error"] == "UNKNOWN_ERR") {
                throw UnknownException("Unknown error occurred", code)
            } else throw AppNotFoundException("App not found", code)
        } else if (code == 429) {
            throw TooManyRequestsException(
                "Rate-limiting enabled, you are making too many requests",
                code
            )
        } else if (code != null) {
            if (code >= 500) {
                throw GooglePlayException("Server error", code)
            } else if (code >= 400) {
                throw MalformedRequestException("Malformed Request", code)
            }
        }
        Log.d("返回的数据", "${content?.let { String(it) }}")
        return content
    }

    override fun buildUrl(url: String, params: Map<String, String>): String {
        val urlBuilder: HttpUrl.Builder = url.toHttpUrlOrNull()!!.newBuilder()
        if (null != params && params.isNotEmpty()) {
            for (name in params.keys) {
                urlBuilder.addQueryParameter(name, params[name])
            }
        }
        return urlBuilder.build().toString()
    }

    override fun buildUrlEx(url: String, params: Map<String, List<String>>): String {
        val urlBuilder: HttpUrl.Builder = url.toHttpUrlOrNull()!!.newBuilder()
        if (null != params && params.isNotEmpty()) {
            for (name in params.keys) {
                for (value in params[name] ?: error("")) {
                    urlBuilder.addQueryParameter(name, value)
                }
            }
        }
        return urlBuilder.build().toString()
    }

    init {
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()
            .connectTimeout(16, TimeUnit.SECONDS)
            .readTimeout(16, TimeUnit.SECONDS)
            .writeTimeout(16, TimeUnit.SECONDS)
            .cookieJar(object : CookieJar {
                private val cookieStore: HashMap<HttpUrl, List<Cookie>> = HashMap()
                override fun saveFromResponse(
                    url: HttpUrl,
                    cookies: List<Cookie>
                ) {
                    cookieStore[url] = cookies
                }

                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                    val cookies: List<Cookie>? = cookieStore[url]
                    return cookies ?: ArrayList<Cookie>()
                }
            })
        builder.retryOnConnectionFailure(true)
        if (isNetworkProxyEnabled(context)) builder.proxy(getNetworkProxy(context))
        client = builder.build()
    }
}