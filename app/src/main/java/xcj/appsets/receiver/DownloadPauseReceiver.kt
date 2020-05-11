package xcj.appsets.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import xcj.appsets.notification.BaseNotification.Companion.REQUEST_ID

class DownloadPauseReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras
        if (extras != null) {
            val requestId = extras.getInt(REQUEST_ID, -1)
            //DownloadManager.getFetchInstance(context).pauseGroup(requestId)
        }
    }
}
