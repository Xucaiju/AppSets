package xcj.appsets.util

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import xcj.appsets.model.App

object PackageUtil {
    private const val ACTION_PACKAGE_REPLACED_NON_SYSTEM = "ACTION_PACKAGE_REPLACED_NON_SYSTEM"
    private const val ACTION_PACKAGE_INSTALLATION_FAILED = "ACTION_PACKAGE_INSTALLATION_FAILED"
    private const val ACTION_UNINSTALL_PACKAGE_FAILED = "ACTION_UNINSTALL_PACKAGE_FAILED"
    private const val PSEUDO_PACKAGE_MAP = "PSEUDO_PACKAGE_MAP"
    private const val PSEUDO_URL_MAP = "PSEUDO_URL_MAP"
    fun getDisplayName(
        context: Context,
        packageName: String?
    ): String {
        val pseudoMap: Map<String, String> =
            getPseudoPackageMap(context)
        return TextUtil.emptyIfNull(pseudoMap[packageName])
    }

    fun getIconURL(context: Context, packageName: String?): String {
        val pseudoMap: Map<String, String> =
            getPseudoURLMap(context)
        return TextUtil.emptyIfNull(pseudoMap[packageName])
    }

    private fun getPseudoPackageMap(context: Context): MutableMap<String, String> {
        return PreferenceUtil.getMap(context, PSEUDO_PACKAGE_MAP)
    }

    private fun getPseudoURLMap(context: Context): MutableMap<String, String> {
        return PreferenceUtil.getMap(context, PSEUDO_URL_MAP)
    }

    fun addToPseudoPackageMap(
        context: Context,
        packageName: String,
        displayName: String
    ) {
        val pseudoMap = getPseudoPackageMap(context)
        pseudoMap[packageName] = displayName
        PreferenceUtil.saveMap(context, pseudoMap, PSEUDO_PACKAGE_MAP)
    }

    fun addToPseudoURLMap(
        context: Context,
        packageName: String,
        iconURL: String
    ) {
        val pseudoMap =
            getPseudoURLMap(context)
        pseudoMap[packageName] = iconURL
        PreferenceUtil.saveMap(context, pseudoMap, PSEUDO_URL_MAP)
    }

    fun getAppFromPackageName(packageManager: PackageManager, packageName: String?): App? {
        return try {
            var app = App()
            val packageInfo =
                packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA)
            app.setPackageName(packageName)
            app.setDisplayName(packageManager.getApplicationLabel(packageInfo.applicationInfo).toString())
            app.setVersionName(packageInfo.versionName)
            app.setVersionCode(packageInfo.versionCode)
            app.setSystem(packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0)
            app
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    fun isSystemApp(packageManager: PackageManager, packageName: String?): Boolean {
        return try {
            val packageInfo =
                packageManager.getPackageInfo(packageName, 0)
            packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun isPackageLaunchable(
        context: Context,
        packageName: String?
    ): Boolean {
        val packageManager = context.packageManager
        return packageManager.getLaunchIntentForPackage(packageName!!) != null
    }

    fun isInstalled(context: Context, app: App): Boolean {
        return try {
            context.packageManager.getPackageInfo(app.getPackageName(), 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun isInstalled(context: Context, packageName: String?): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    val filter: IntentFilter
        get() {
            val filter = IntentFilter()
            filter.addDataScheme("package")
            filter.addAction(Intent.ACTION_PACKAGE_REMOVED)
            filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
            filter.addAction(Intent.ACTION_PACKAGE_INSTALL)
            filter.addAction(Intent.ACTION_UNINSTALL_PACKAGE)
            filter.addAction(Intent.ACTION_PACKAGE_ADDED)
            filter.addAction(Intent.ACTION_PACKAGE_REPLACED)
            filter.addAction(ACTION_PACKAGE_REPLACED_NON_SYSTEM)
            filter.addAction(ACTION_PACKAGE_INSTALLATION_FAILED)
            filter.addAction(ACTION_UNINSTALL_PACKAGE_FAILED)
            return filter
        }
}