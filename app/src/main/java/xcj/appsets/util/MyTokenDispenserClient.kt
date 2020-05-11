package xcj.appsets.util

import com.dragons.aurora.playstoreapiv2.GooglePlayException
import com.dragons.aurora.playstoreapiv2.HttpClientAdapter
import com.dragons.aurora.playstoreapiv2.TokenDispenserClient
import com.dragons.aurora.playstoreapiv2.TokenDispenserException
import java.io.IOException

class MyTokenDispenserClient(url: String, httpClient: HttpClientAdapter) :TokenDispenserClient(url, httpClient) {
    var mRESOURCE_EMAIL = "email"
    var murl = url
    var mhttpClient = httpClient
    override fun getRandomEmail(): String {
        return request(
            mhttpClient,
            "$murl/$mRESOURCE_EMAIL"
        )
    }
    companion object{
        @JvmStatic
        @Throws(IOException::class)
        private  fun request(
            httpClient: HttpClientAdapter,
            url: String
        ): String {
            return try {
                val response = String(httpClient[url])
                if (response.length > 500) {
                    throw TokenDispenserException("Token is unexpectedly long")
                }
//                if (response.matches(".*[\\r\\n].*")) {
//                    throw TokenDispenserException("Contains unexpected characters")
//                }
                response
            } catch (e: GooglePlayException) {
                when (e.code) {
                    401, 403 -> throw TokenDispenserException("Token dispenser returned an auth error for $url")
                    404 -> throw TokenDispenserException("Token dispenser has no password for $url")
                    429 -> throw TokenDispenserException("You are making too many requests - try later")
                    else -> throw TokenDispenserException("Token dispenser error " + e.code + " " + e.message)
                }
            } catch (e: IOException) {
                throw TokenDispenserException(e)
            }
        }
    }

}

