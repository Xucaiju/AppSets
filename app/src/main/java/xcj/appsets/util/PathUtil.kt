package xcj.appsets.util

import android.content.Context
import android.os.Build
import android.os.Environment
import xcj.appsets.Constant
import xcj.appsets.model.App
import java.io.File

object PathUtil {
    fun getApkPath(packageName: String, version: Int): File {
        val filename = "$packageName.$version.apk"
        return File(rootApkCopyPath, filename)
    }

    fun getRootApkPath(context: Context): String? {
        return if (isCustomPath(context)!!)
            PreferenceUtil.getString(
            context,
            Constant.PREFERENCE_DOWNLOAD_DIRECTORY
        ) else getBaseDirectory(context)
    }

    val rootApkCopyPath: String
        get() = baseCopyDirectory

    fun getLocalApkPath(context: Context, app: App): String? {
        return app.getPackageName()?.let { app.getVersionCode()?.let { it1 ->
            getLocalApkPath(context, it,
                it1
            )
        } }
    }

    fun getLocalSplitPath(
        context: Context,
        app: App,
        tag: String
    ): String? {
        return app.getPackageName()?.let {
            app.getVersionCode()?.let { it1 ->
                getLocalSplitPath(context,
                    it, it1, tag)
            }
        }
    }

    fun getObbPath(app: App, main: Boolean, isGZipped: Boolean): String? {
        return app.getPackageName()?.let { app.getVersionCode()?.let { it1 ->
            getObbPath(it,
                it1, main, isGZipped)
        } }
    }

    fun getLocalApkPath(
        context: Context,
        packageName: String,
        versionCode: Int
    ): String {
        return getRootApkPath(context) + "/" + packageName + "." + versionCode + ".apk"
    }

    private fun getLocalSplitPath(
        context: Context,
        packageName: String,
        versionCode: Int,
        tag: String
    ): String {
        return getRootApkPath(context) + "/" + packageName + "." + versionCode + "." + tag + ".apk"
    }

    fun getObbPath(
        packageName: String,
        version: Int,
        main: Boolean,
        isGZipped: Boolean
    ): String {
        val obbDir = Environment.getExternalStorageDirectory()
            .toString() + "/Android/obb/" + packageName
        val ext = if (isGZipped) ".gzip" else ".obb"
        val filename =
            (if (main) "/main" else "/patch") + "." + version + "." + packageName + ext
        return obbDir + filename
    }

    fun isCustomPath(context: Context): Boolean? {
        return getCustomPath(context)?.isNotEmpty()
    }

    fun getCustomPath(context: Context): String? {
        return PreferenceUtil.getString(context, Constant.PREFERENCE_DOWNLOAD_DIRECTORY)
    }

    fun checkBaseDirectory(context: Context): Boolean {
        val success = File(getRootApkPath(context)).exists()
        return success || createBaseDirectory(context)
    }

    fun createBaseDirectory(context: Context): Boolean {
        return File(getRootApkPath(context)).mkdir()
    }

    fun getBaseDirectory(context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && isRootInstallEnabled(
                context
            )
        ) {
            context.filesDir.path
        } else Environment.getExternalStorageDirectory().path + "/AppSets"
    }

    fun getExtBaseDirectory(context: Context?): String {
        return Environment.getExternalStorageDirectory().path + "/AppSets"
    }

    val baseCopyDirectory: String
        get() = Environment.getExternalStorageDirectory().path + "/AppSets/Copy/APK"

    fun fileExists(context: Context, app: App): Boolean {
        return File(getLocalApkPath(context, app)).exists()
    }
}