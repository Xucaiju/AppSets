package xcj.appsets.provider

import android.content.Context
import android.content.pm.PackageManager

class NativeGsfVersionProvider(context: Context) {
    private var gsfVersionCode = 0
    private var vendingVersionCode = 0
    private var vendingVersionString = ""
    fun getGsfVersionCode(defaultIfNotFound: Boolean): Int {
        return if (defaultIfNotFound && gsfVersionCode < GOOGLE_SERVICES_VERSION_CODE) GOOGLE_SERVICES_VERSION_CODE else gsfVersionCode
    }

    fun getVendingVersionCode(defaultIfNotFound: Boolean): Int {
        return if (defaultIfNotFound && vendingVersionCode < GOOGLE_VENDING_VERSION_CODE) GOOGLE_VENDING_VERSION_CODE else vendingVersionCode
    }

    fun getVendingVersionString(defaultIfNotFound: Boolean): String {
        return if (defaultIfNotFound && vendingVersionCode < GOOGLE_VENDING_VERSION_CODE) GOOGLE_VENDING_VERSION_STRING else vendingVersionString
    }

    companion object {
        private const val GOOGLE_SERVICES_PACKAGE_ID = "com.google.android.gms"
        private const val GOOGLE_VENDING_PACKAGE_ID = "com.android.vending"
        private const val GOOGLE_SERVICES_VERSION_CODE = 16089037
        private const val GOOGLE_VENDING_VERSION_CODE = 81431900
        private const val GOOGLE_VENDING_VERSION_STRING = "14.3.19-all [0] [PR] 241809067"
    }

    init {
        try {
            gsfVersionCode = context.packageManager
                .getPackageInfo(GOOGLE_SERVICES_PACKAGE_ID, 0)
                .versionCode
        } catch (e: PackageManager.NameNotFoundException) { // com.google.android.gms not found
        }
        try {
            val packageInfo = context.packageManager
                .getPackageInfo(GOOGLE_VENDING_PACKAGE_ID, 0)
            vendingVersionCode = packageInfo.versionCode
            vendingVersionString = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) { // com.android.vending not found
        }
    }
}