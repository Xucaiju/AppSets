package xcj.appsets.installer

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.content.pm.PackageInstaller.SessionParams
import org.apache.commons.io.IOUtils
import xcj.appsets.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class AppInstaller(context: Context) : AppInstallerAbstract(context) {
     override fun installApkFiles(packageName: String, apkFiles: List<File>) {
        val packageInstaller: PackageInstaller = context.packageManager.packageInstaller
        try {
            val sessionParams = SessionParams(SessionParams.MODE_FULL_INSTALL)
            val sessionID = packageInstaller.createSession(sessionParams)
            val session = packageInstaller.openSession(sessionID)
            for (apkFile in apkFiles) {
                val inputStream: InputStream = FileInputStream(apkFile)
                val outputStream =
                    session.openWrite(apkFile.name, 0, apkFile.length())
                IOUtils.copy(inputStream, outputStream)
                session.fsync(outputStream)
                inputStream.close()
                outputStream.close()
            }
            val callbackIntent = Intent(context, InstallerService::class.java)
            val pendingIntent = PendingIntent.getService(
                context,
                sessionID,
                callbackIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            session.commit(pendingIntent.intentSender)
            session.close()
        } catch (e: Exception) {
            Log.w(e.message)
        }
    }

    companion object {
        private  var instance: AppInstaller? = null
        fun getInstance(context: Context): AppInstaller {
            if (instance == null) {
                synchronized(
                    AppInstaller::class.java
                ) {
                    if (instance == null)
                        instance = AppInstaller(context)
                }
            }
            return instance!!
        }
    }

    init {
        instance = this
    }
}