package xcj.appsets.util

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.tonyodev.fetch2core.Downloader.FileDownloaderType
import xcj.appsets.Constant
import xcj.appsets.service.ApiValidateService
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.ln
import kotlin.math.pow

fun getDefaultTap(context: Context) = getSharedPreferences(context).getString(Constant.PREFERENCE_DEFAULT_TAB,"today")
fun getSharedPreferences(context: Context): SharedPreferences = context.getSharedPreferences(Constant.SHARED_PREFERENCES_KEY,Context.MODE_PRIVATE)
fun getInstallationProfile(context: Context?): String? =
    if (!isRootInstallEnabled(context!!)) "0"
    else getSharedPreferences(context).getString(Constant.PREFERENCE_INSTALLATION_PROFILE, "0")

fun isRootInstallEnabled(context: Context): Boolean {
    val installMethod: String? = getSharedPreferences(context)
        .getString(Constant.PREFERENCE_INSTALLATION_METHOD, "0")
    return installMethod == "1"
}
fun clearOldInstallationSessions(context: Context) {
    val packageInstaller = context.packageManager.packageInstaller
    for (sessionInfo in packageInstaller.mySessions) {
        val sessionId = sessionInfo.sessionId
        try {
            packageInstaller.abandonSession(sessionInfo.sessionId)
            Log.i("Abandoned session id -> %d", sessionId)
        } catch (e: Exception) {
            Log.e(e.message)
        }
    }
}
fun getCustomTokenizerURL(context: Context): String? {
    return getSharedPreferences(context)
        .getString(Constant.PREFERENCE_CUSTOM_TOKENIZER_URL, Constant.DISPENSER_PRIMARY)
}
fun isCustomTokenizerEnabled(context: Context): Boolean {
    return getSharedPreferences(context)
        .getBoolean(Constant.PREFERENCE_ENABLE_CUSTOM_TOKENIZER, false)
}
fun getTokenizerURL(context: Context): String? {
    return getSharedPreferences(context)
        .getString(Constant.PREFERENCE_TOKENIZER_URL, Constant.DISPENSER_PRIMARY)
}
fun isNetworkProxyEnabled(context: Context): Boolean {
    return getSharedPreferences(context)
        .getBoolean(Constant.PREFERENCE_ENABLE_PROXY, false)
}

fun getNetworkProxy(context: Context): Proxy? {
    val proxyHost: String? = getSharedPreferences(context)
        .getString(Constant.PREFERENCE_PROXY_HOST, "127.0.0.1")
    val proxyPort: String? = getSharedPreferences(context)
        .getString(Constant.PREFERENCE_PROXY_PORT, "8118")
    val port: Int = xcj.appsets.util.parseInt(proxyPort!!, 8118)//TODO something will go wrong, SHOULD BE MODIFY TO paseInt()
    return Proxy(
        getProxyType(context),
        InetSocketAddress(proxyHost, port)
    )
}
fun getProxyType(context: Context): Proxy.Type? {
    val proxyType: String? = getSharedPreferences(context)
        .getString(Constant.PREFERENCE_PROXY_TYPE, "HTTP")
    return when (proxyType) {
        "HTTP" -> Proxy.Type.HTTP
        "SOCKS" -> Proxy.Type.SOCKS
        "DIRECT" -> Proxy.Type.DIRECT
        else -> Proxy.Type.HTTP
    }
}
fun parseInt(intAsString: String, defaultValue: Int): Int {
    return try {
        intAsString.toInt()
    } catch (e: NumberFormatException) {
        defaultValue
    }
}

fun humanReadableByteValue(bytes: Long, si: Boolean): String? {
    val unit = if (si) 1000 else 1024
    if (bytes < unit) return "$bytes B"
    val exp =
        (ln(bytes.toDouble()) / ln(unit.toDouble())).toInt()
    val pre =
        (if (si) "kMGTPE" else "KMGTPE")[exp - 1].toString() + if (si) "" else "i"
    return String.format(
        Locale.getDefault(), "%.1f %sB",
        bytes / unit.toDouble().pow(exp.toDouble()), pre
    )
}

fun attachSnapPager(
    context: Context,
    recyclerView: RecyclerView?
) {
    if (snapPagerEnabled(context)) {
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
    }

}
fun snapPagerEnabled(context: Context): Boolean {
    return getSharedPreferences(context)
        .getBoolean(Constant.PREFERENCE_FEATURED_SNAP, false)
}

fun setCacheCreateTime(
    context: Context,
    dateInMillis: Long,
    cachType:String
) {
    when(cachType) {
        "Home"-> {
            PreferenceUtil.putString(context, Constant.PREFERENCE_CACHE_DATE_HOME, dateInMillis.toString())
        }
        "Today"-> {
            PreferenceUtil.putString(context, Constant.PREFERENCE_CACHE_DATE_TODAY, dateInMillis.toString())
        }
        "Favorite"-> {
            PreferenceUtil.putString(context, Constant.PREFERENCE_CACHE_DATE_FAVORITE, dateInMillis.toString())
        }
        "TimelineApps"->{
            PreferenceUtil.putString(context, Constant.PREFERENCE_CACHE_DATE_TIME_LINE_APPS, dateInMillis.toString())
        }
        "TodayAppUserReview"->{
            PreferenceUtil.putString(context, Constant.PREFERENCE_CACHE_DATE_TODAY_APP_USER_REVIEW, dateInMillis.toString())
        }
        "FirebaseTokenGenerateTime"->{
            PreferenceUtil.putString(context, Constant.PREFERENCE_CACHE_DATE_FIRE_BASE_TOKEN_GENERATE_TIME, dateInMillis.toString())
        }
    }

}

fun parseLong(intAsString: String, defaultValue: Long): Long {
    return try {
        intAsString.toLong()
    } catch (e: java.lang.NumberFormatException) {
        defaultValue
    }
}
fun isHomeAppCacheObsolete(context: Context): Boolean {
    return try {
        val lastSyncDate: Long? = PreferenceUtil.getString(context, Constant.PREFERENCE_CACHE_DATE_HOME)?.toLong()
        val currentSyncDate = Calendar.getInstance().timeInMillis
        val diffDatesInMillis = currentSyncDate - (lastSyncDate?:0)
        val diffInDays = TimeUnit.MILLISECONDS.toDays(diffDatesInMillis)
        diffInDays > 3
    } catch (e: java.lang.Exception) {
        false
    }
}
fun clearHomeAppCache(context: Context) {
    PreferenceUtil.putString(context, Constant.TOP_APPS, "")
    PreferenceUtil.putString(context, Constant.TOP_FAMILY, "")
    PreferenceUtil.putString(context, Constant.TOP_GAME, "")
}
fun validateApi(context: Context) {
    if (!ApiValidateService.isServiceRunning)
        context.startService(Intent(context, ApiValidateService::class.java))
}

fun getDefaultTab(context: Context): Int {
    val value: String? = getSharedPreferences(context).getString(Constant.PREFERENCE_DEFAULT_TAB, "0")
    return  parseInt(value!!, 0)
}

fun isSearchByPackageEnabled(context: Context?): Boolean {
    return context?.let {
        getSharedPreferences(it)
            .getBoolean(Constant.PREFERENCE_SEARCH_PACKAGE, true)
    }!!
}

fun filterGoogleAppsEnabled(context: Context?): Boolean {
    return context?.let {
        getSharedPreferences(it)
            .getBoolean(Constant.PREFERENCE_FILTER_GOOGLE, false)
    }!!
}

fun addSiPrefix(value: Long): String? {
    var tempValue = value
    var order = 0
    while (tempValue >= 1000.0) {
        tempValue /= 1000.0.toLong()
        order += 3
    }
    return "${tempValue}${siPrefixes[order]}"
}

private val diPrefixes: Map<Int, String> = HashMap()
private val siPrefixes: Map<Int, String> = HashMap()

fun filterSearchNonPersistent(context: Context?): Boolean {
    return getSharedPreferences(context!!)
        .getBoolean(Constant.PREFERENCE_FILTER_SEARCH, true)
}
fun shouldAutoInstallApk(context: Context?): Boolean {
    return getSharedPreferences(context!!)
        .getBoolean(Constant.PREFERENCE_INSTALLATION_AUTO, true)
}
fun isNativeInstallerEnforced(context: Context?): Boolean {
    return getSharedPreferences(context!!)
        .getBoolean(Constant.PREFERENCE_INSTALLATION_TYPE, false)
}
fun shouldDeleteApk(context: Context?): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && isRootInstallEnabled(
            context!!
        )
    ) {
        true
    } else getSharedPreferences(context!!)
        .getBoolean(Constant.PREFERENCE_INSTALLATION_DELETE, false)
}
fun getActiveDownloadCount(context: Context?): Int {
    return getSharedPreferences(context!!)
        .getInt(Constant.PREFERENCE_DOWNLOAD_ACTIVE, 3)
}

fun getDownloadStrategy(context: Context?): FileDownloaderType? {
    val prefValue: String? = getSharedPreferences(context!!).getString(Constant.PREFERENCE_DOWNLOAD_STRATEGY, "")
    return when (prefValue) {
        "0" -> FileDownloaderType.SEQUENTIAL
        "1" -> FileDownloaderType.PARALLEL
        else -> FileDownloaderType.PARALLEL
    }
}

fun isFetchDebugEnabled(context: Context?): Boolean {
    return getSharedPreferences(context!!)
        .getBoolean(Constant.PREFERENCE_DOWNLOAD_DEBUG, false)
}

fun humanReadableByteSpeed(bytes: Long, si: Boolean): String? {
    val unit = if (si) 1000 else 1024
    if (bytes < unit) return "$bytes B"
    val exp =
        (ln(bytes.toDouble()) / ln(unit.toDouble())).toInt()
    val pre =
        (if (si) "kMGTPE" else "KMGTPE")[exp - 1].toString() + if (si) "" else "i"
    return String.format(
        Locale.getDefault(), "%.1f %sB/s",
        bytes / unit.toDouble().pow(exp.toDouble()), pre
    )
}

fun isPrivilegedInstall(context: Context?): Boolean {
    val prefValue: String? = PreferenceUtil.getString(context!!, Constant.PREFERENCE_INSTALLATION_METHOD)
    return when (prefValue) {
        "0" -> false
        "1", "2" -> true
        else -> false
    }
}



