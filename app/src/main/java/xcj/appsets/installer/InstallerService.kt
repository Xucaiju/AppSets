package xcj.appsets.installer

import android.app.Service
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.IBinder
import androidx.annotation.Nullable

class InstallerService:Service() {
    @Nullable
    override fun onBind(intent: Intent?): IBinder? = null
    companion object{
        const val ACTION_INSTALLATION_STATUS_NOTIFICATION = "xcj.appsets.action.INSTALLATION_STATUS_NOTIFICATION"
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, -1)
        val packageName = intent.getStringExtra(PackageInstaller.EXTRA_PACKAGE_NAME)
        //Send broadcast for the installation status of the package
        sendStatusBroadcast(status, packageName)
        //Launch user confirmation activity
        if (status == PackageInstaller.STATUS_PENDING_USER_ACTION) {
            val confirmationIntent = intent.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)
            confirmationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                startActivity(confirmationIntent)
            } catch (e: Exception) {
                sendStatusBroadcast(PackageInstaller.STATUS_FAILURE, packageName)
            }
        }
        stopSelf()
        return START_NOT_STICKY
    }

    private fun sendStatusBroadcast(status: Int, packageName: String) {
        val statusIntent = Intent(ACTION_INSTALLATION_STATUS_NOTIFICATION)
        statusIntent.putExtra(PackageInstaller.EXTRA_STATUS, status)
        statusIntent.putExtra(PackageInstaller.EXTRA_PACKAGE_NAME, packageName)
        sendBroadcast(statusIntent)
    }

}