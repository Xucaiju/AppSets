package xcj.appsets.manager

import android.content.Context
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2.FetchConfiguration
import com.tonyodev.fetch2okhttp.OkHttpDownloader
import okhttp3.OkHttpClient
import xcj.appsets.Constant
import xcj.appsets.util.*

class DownloadManager {
    companion object {
        @Volatile
        private var instance: DownloadManager? = null
        private var fetch: Fetch? = null
        fun getFetchInstance(context: Context): Fetch? {
            if (instance == null) {
                synchronized(DownloadManager::class.java) {
                    if (instance == null) {
                        instance = DownloadManager()
                        fetch = getFetch(context)
                    }
                }
            }
            return fetch
        }

        private fun getFetch(context: Context): Fetch? {
            val fetchConfiguration: FetchConfiguration.Builder? = getDownloadStrategy(context)?.let {downloader_type->
                OkHttpDownloader(
                    getOkHttpClient(
                        context
                    ), downloader_type
                )
            }?.let {downloader->
                FetchConfiguration.Builder(
                    context
                )
                    .setDownloadConcurrentLimit(getActiveDownloadCount(context))
                    .setHttpDownloader(
                        downloader
                    )
                    .setNamespace(Constant.TAG)
                    .enableLogging(isFetchDebugEnabled(context))
                    .enableHashCheck(true)
                    .enableFileExistChecks(true)
                    .enableRetryOnNetworkGain(true)
                    .enableAutoStart(true)
                    .setAutoRetryMaxAttempts(3)
                    .setProgressReportingInterval(3000)
            }
            return fetchConfiguration?.build()?.let { Fetch.Impl.getInstance(it) }
        }

        private fun getOkHttpClient(context: Context): OkHttpClient {
            val builder = OkHttpClient.Builder()
            if (isNetworkProxyEnabled(context)) builder.proxy(getNetworkProxy(context))
            return builder.build()
        }
    }

    init {
        if (instance != null) {
            throw RuntimeException("Use get() method to get the single instance of RxBus")
        }
    }
}