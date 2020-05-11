package xcj.appsets.receiver

import android.app.DownloadManager.ACTION_NOTIFICATION_CLICKED
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AppSetsDownloadReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val extras = intent?.extras
        if(extras!=null) {
            val get = extras.get(ACTION_NOTIFICATION_CLICKED)
        }
    }
}