package xcj.appsets.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import xcj.appsets.AppSetsApplication
import xcj.appsets.notification.BaseNotification.Companion.INTENT_APP_VERSION
import xcj.appsets.notification.BaseNotification.Companion.INTENT_PACKAGE_NAME

class InstallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras
        if (extras != null) {
            val packageName = extras.getString(INTENT_PACKAGE_NAME, "")
            val appVersion = extras.getInt(INTENT_APP_VERSION, -1)
            if (packageName.isNotEmpty() && appVersion != -1) {
                AppSetsApplication.getInstaller()?.installSplit(packageName, appVersion)
            }
        }
    }
}