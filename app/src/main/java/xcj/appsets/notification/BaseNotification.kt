package xcj.appsets.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import xcj.appsets.Constant
import xcj.appsets.R
import xcj.appsets.model.App
import xcj.appsets.receiver.DownloadCancelReceiver
import xcj.appsets.receiver.DownloadPauseReceiver
import xcj.appsets.receiver.DownloadResumeReceiver
import xcj.appsets.receiver.InstallReceiver
import xcj.appsets.ui.AppDetailsActivity
import xcj.appsets.util.NotificationUtil

open class BaseNotification(context: Context){
    var context = context
    var builder: NotificationCompat.Builder? = null
    protected var manager: NotificationManager? = null
    protected var app: App? = null

    constructor(context: Context, app: App?) : this(context) {
        this.context = context
        this.app = app
    }
    val getBuilder:NotificationCompat.Builder?
        get() {
            return NotificationCompat.Builder(
                context,
                Constant.NOTIFICATION_CHANNEL_GENERAL
            )
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setColor(context.resources.getColor(R.color.colorAccent,null))
                .setContentIntent(contentIntent)
                .setContentTitle(app?.getDisplayName())
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setPriority(NotificationUtil.getNotificationPriority(context))
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher_round))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        }
   /* protected fun getBuilder(): NotificationCompat.Builder {

    }*/

    /*
     *
     * All Pending Intents to handle App Download & App Installations
     * getContentIntent() to launch DetailsActivity for the App
     * getInstallIntent() to broadcast Install action on download complete
     * getCancelIntent() to broadcast Download Cancel action
     * getPauseIntent() to broadcast Download Pause action
     *
     */
    protected val contentIntent: PendingIntent
        protected get() {
            val intent = Intent(context, AppDetailsActivity::class.java)
            intent.putExtra(INTENT_PACKAGE_NAME, app?.getPackageName())
            return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

    protected val installIntent: PendingIntent
        get() {
            val intent = Intent(context, InstallReceiver::class.java)
            intent.putExtra(INTENT_PACKAGE_NAME, app?.getPackageName())
            intent.putExtra(INTENT_APP_VERSION, app?.getVersionCode())
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

    protected fun getCancelIntent(requestId: Int): PendingIntent {
        val intent = Intent(context, DownloadCancelReceiver::class.java)
        intent.putExtra(REQUEST_ID, requestId)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    protected fun getPauseIntent(requestId: Int): PendingIntent {
        val intent = Intent(context, DownloadPauseReceiver::class.java)
        intent.putExtra(REQUEST_ID, requestId)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    protected fun getResumeIntent(requestId: Int): PendingIntent {
        val intent = Intent(context, DownloadResumeReceiver::class.java)
        intent.putExtra(REQUEST_ID, requestId)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    companion object {
        const val INTENT_PACKAGE_NAME = "INTENT_PACKAGE_NAME"
        const val INTENT_APP_VERSION = "INTENT_APP_VERSION"
        const val REQUEST_ID = "REQUEST_ID"
    }
}