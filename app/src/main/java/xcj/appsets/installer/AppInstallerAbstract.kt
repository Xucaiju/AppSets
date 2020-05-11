package xcj.appsets.installer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Handler
import android.os.Looper
import java.io.File

abstract class AppInstallerAbstract internal constructor(context: Context) {
    var context: Context = context.applicationContext
    private var broadcastReceiver: BroadcastReceiver
    private var handler = Handler(Looper.getMainLooper())
    private var listener: InstallationStatusListener? = null
    init {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(
                context: Context,
                intent: Intent
            ) {
                val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, -1)
                val packageName = intent.getStringExtra(
                    PackageInstaller.EXTRA_PACKAGE_NAME
                )
                dispatchSessionUpdate(status, packageName)
            }
        }
    }
    fun getBroadcastReceiver() = broadcastReceiver
    fun addInstallationStatusListener(listener: InstallationStatusListener?) {
        this.listener = listener
    }

    fun dispatchSessionUpdate(status: Int, packageName: String) {
        handler.post {
            if (listener != null) listener!!.onStatusChanged(
                status,
                packageName
            )
        }
    }

    abstract fun installApkFiles(
        packageName: String,
        apkFiles: List<File>
    )

    interface InstallationStatusListener {
        fun onStatusChanged(status: Int, packageName: String)
    }


}