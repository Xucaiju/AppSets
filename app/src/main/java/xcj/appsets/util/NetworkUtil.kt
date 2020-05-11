package xcj.appsets.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object NetworkUtil {
    fun isConnected(context: Context?): Boolean {
        if (context == null) return false
        val any = context.getSystemService(Context.CONNECTIVITY_SERVICE)
        val manager = any as ConnectivityManager
        if (manager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)
                if (capabilities != null) {
                    return (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_LOWPAN)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE))
                }
            } else {
                try {
                    val activeNetworkInfo = manager.activeNetworkInfo
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                        return true
                    }
                } catch (e: Exception) {
                    return false
                }
            }
        }
        return false
    }
}