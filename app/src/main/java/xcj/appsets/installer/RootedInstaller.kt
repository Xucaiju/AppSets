package xcj.appsets.installer

import android.content.Context
import android.content.pm.PackageInstaller
import xcj.appsets.util.ContextUtil
import xcj.appsets.util.Log
import xcj.appsets.util.Root
import xcj.appsets.util.getInstallationProfile
import java.io.File
import java.util.*
import java.util.regex.Pattern

class RootedInstaller private constructor(context: Context) : AppInstallerAbstract(context) {
    init {
        instance = this
    }
    override fun installApkFiles(packageName: String, apkFiles: List<File>) {
        try {
            if (root!!.isTerminated() || !root!!.isAcquired()) {
                Root.requestRoot()
                if (!root!!.isAcquired()) {
                    ContextUtil.toastLong(context, "Root access not available")
                    dispatchSessionUpdate(PackageInstaller.STATUS_FAILURE, packageName)
                    return
                }
            }
            var totalSize = 0
            for (apkFile in apkFiles) totalSize += apkFile.length().toInt()
            var result = ensureCommandSucceeded(
                root!!.exec(
                    java.lang.String.format(
                        Locale.getDefault(),
                        "pm install-create -i com.android.vending --user %s -r -S %d",
                        getInstallationProfile(context),
                        totalSize
                    )
                )
            )
            val sessionIdPattern =
                Pattern.compile("(\\d+)")
            val sessionIdMatcher = sessionIdPattern.matcher(result)
            val found = sessionIdMatcher.find()
            val sessionId = sessionIdMatcher.group(1).toInt()
            for (apkFile in apkFiles) ensureCommandSucceeded(
                root!!.exec(
                    String.format(
                        Locale.getDefault(),
                        "cat \"%s\" | pm install-write -S %d %d \"%s\"",
                        apkFile.absolutePath,
                        apkFile.length(),
                        sessionId,
                        apkFile.name
                    )
                )
            )
            result = ensureCommandSucceeded(
                root!!.exec(
                    String.format(
                        Locale.getDefault(),
                        "pm install-commit %d ",
                        sessionId
                    )
                )
            )
            if (result.toLowerCase().contains("success")) dispatchSessionUpdate(
                PackageInstaller.STATUS_SUCCESS,
                packageName
            ) else dispatchSessionUpdate(PackageInstaller.STATUS_FAILURE, packageName)
        } catch (e: Exception) {
            Log.w(e.message)
        }
    }

    private fun ensureCommandSucceeded(result: String?): String {
        if (result == null || result.isEmpty()) throw RuntimeException(
            root!!.readError()
        )
        return result
    }

    companion object {
        private var instance: RootedInstaller? = null
        private var root: Root? = null
        fun getInstance(context: Context): RootedInstaller? {
            if (instance == null) {
                synchronized(RootedInstaller::class.java) {
                    if (instance == null) {
                        instance = RootedInstaller(context)
                        root = Root()
                    }
                }
            }
            return instance
        }
    }
}