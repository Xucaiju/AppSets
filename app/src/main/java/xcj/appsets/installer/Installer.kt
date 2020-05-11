package xcj.appsets.installer

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import androidx.core.content.FileProvider
import org.apache.commons.lang3.StringUtils
import xcj.appsets.AppSetsApplication
import xcj.appsets.Constant
import xcj.appsets.R
import xcj.appsets.events.Event
import xcj.appsets.model.App
import xcj.appsets.notification.QuickNotification
import xcj.appsets.ui.AppDetailsActivity
import xcj.appsets.util.*
import java.io.File
import java.util.*

class Installer(var context: Context): AppInstallerAbstract.InstallationStatusListener {
    private val appHashMap: MutableMap<String, App> = HashMap()
    private var packageInstaller:AppInstallerAbstract? = getInstallationMethod(context.applicationContext)
    private val installationQueue: MutableList<App> = ArrayList()
    private var isInstalling = false
    private var isWaiting = false
    fun getPackageInstaller():AppInstallerAbstract? = packageInstaller
    override fun onStatusChanged(status: Int, packageName: String) {}

    private fun getInstallationMethod(context: Context): AppInstallerAbstract? = when (PreferenceUtil.getString(context, Constant.PREFERENCE_INSTALLATION_METHOD)) {
            "1" -> RootedInstaller.getInstance(context)
            //"2" -> AppInstallerPrivileged.getInstance(context)
            else -> AppInstaller.getInstance(context)

    }

    private fun getStatusString(status: Int): String? = when (status) {
            PackageInstaller.STATUS_FAILURE -> context.getString(R.string.installer_status_failure)
            PackageInstaller.STATUS_FAILURE_ABORTED -> context.getString(R.string.installer_status_failure_aborted)
            PackageInstaller.STATUS_FAILURE_BLOCKED -> context.getString(R.string.installer_status_failure_blocked)
            PackageInstaller.STATUS_FAILURE_CONFLICT -> context.getString(R.string.installer_status_failure_conflict)
            PackageInstaller.STATUS_FAILURE_INCOMPATIBLE -> context.getString(R.string.installer_status_failure_incompatible)
            PackageInstaller.STATUS_FAILURE_INVALID -> context.getString(R.string.installer_status_failure_invalid)
            PackageInstaller.STATUS_FAILURE_STORAGE -> context.getString(R.string.installer_status_failure_storage)
            PackageInstaller.STATUS_PENDING_USER_ACTION -> context.getString(R.string.installer_status_user_action)
            PackageInstaller.STATUS_SUCCESS -> context.getString(R.string.installer_status_success)
            else -> context.getString(R.string.installer_status_unknown)
        }
    fun install(app: App) {
        app.getPackageName()?.let {
            appHashMap[it] = app
        }

        installationQueue.add(app)
        if (isInstalling)
            isWaiting =  true
        else processApp(app)
    }
    fun install(packageName: String?, versionCode: Int) {
        Log.i("Native Installer Called")
        val intent: Intent
        val file = File(packageName?.let { PathUtil.getLocalApkPath(context, it, versionCode) })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
            intent.data = FileProvider.getUriForFile(
                context,
                "com.aurora.store.fileProvider",
                file
            )
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        } else {
            intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(
                Uri.fromFile(file),
                "application/vnd.android.package-archive"
            )
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
    fun installSplit(packageName: String?, versionCode: Int) {
        Log.i("Split Installer Called")
        val apkFiles: MutableList<File> =
            ArrayList()
        val apkDirectory = File(PathUtil.getRootApkPath(context))
        for (splitApk in apkDirectory.listFiles()) {
            if (splitApk.path.contains(
                    StringBuilder()
                        .append(packageName)
                        .append(".")
                        .append(versionCode)
                )
            ) {
                apkFiles.add(splitApk)
            }
        }
        packageInstaller?.addInstallationStatusListener(object : AppInstallerAbstract.InstallationStatusListener {
            override fun onStatusChanged(status: Int, packageName: String) {

                val statusMessage = getStatusString(status)
                val app = appHashMap[packageName]

                var displayName =
                    if (app != null) TextUtil.emptyIfNull(app.getDisplayName()) else TextUtil.emptyIfNull(
                        packageName
                    )

                if (StringUtils.isEmpty(displayName)) displayName =
                    context.getString(R.string.app_name)

                Log.i(
                    "Package Installer -> %s : %s",
                    displayName,
                    TextUtil.emptyIfNull(statusMessage)
                )

                clearNotification(app)

                if (status === PackageInstaller.STATUS_SUCCESS) {
                    sendStatusBroadcast(packageName, 1)
                    if (app != null && shouldDeleteApk(context)) {
                        clearInstallationFiles(app)
                    }
                } else {
                    sendStatusBroadcast(packageName, 0)
                }

                statusMessage?.let {
                    QuickNotification.show(
                        context,
                        displayName,
                        it,
                        getContentIntent(packageName)
                    )
                }
                appHashMap.remove(packageName)
                checkAndProcessQueuedApps()
            }

        })
        AsyncTask.execute {
            packageInstaller!!.installApkFiles(
                packageName!!,
                apkFiles
            )
        }
    }
    private fun checkAndProcessQueuedApps() {
        if (installationQueue.isEmpty()) {
            isWaiting = false
            isInstalling = false
        }
        if (isWaiting) processApp(installationQueue[0])
    }
    private fun getContentIntent(packageName: String): PendingIntent? {
        val intent = Intent(context, AppDetailsActivity::class.java)
        intent.putExtra(Constant.INTENT_PACKAGE_NAME, packageName)
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
    private fun clearInstallationFiles(app: App) {
        var success = false
        val apkDirectory = File(PathUtil.getRootApkPath(context))
        for (file in apkDirectory.listFiles()) {
            if (file.name
                    .contains(app.getPackageName().toString() + "." + app.getVersionCode())
            ) {
                success = file.delete()
            }
        }
        if (success) Log.i("Installation files deleted") else Log.i("Could not delete installation files")
    }
    private fun sendStatusBroadcast(packageName: String, status: Int) {
        AppSetsApplication.rxNotify(Event(Event.SubType.INSTALLED, packageName, status))
    }
    private fun clearNotification(app: App?) {
        if (app == null) return
        val any = context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
        val notificationManager = any as NotificationManager
        notificationManager?.cancel(app.getPackageName().hashCode())
    }
    private fun processApp(app: App) {
        val packageName = app.getPackageName()
        val versionCode = app.getVersionCode()!!
        isInstalling = true
        installationQueue.remove(app)
        if (isNativeInstallerEnforced(context))
            install(packageName, versionCode)
        else installSplit(packageName, versionCode)
    }
    interface InstallationStatusListener {
        fun onStatusChanged(status: Int, packageName: String?)
    }
}