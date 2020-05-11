package xcj.appsets.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import xcj.appsets.Constant
import xcj.appsets.R
import xcj.appsets.util.NotificationUtil

class QuickNotification constructor(context: Context) : BaseNotification(context) {
    fun show(
        contentTitle: String?,
        contentText: String?,
        contentIntent: PendingIntent?
    ) {
        if (NotificationUtil.isNotificationEnabled(context)) {
            manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            builder = NotificationCompat.Builder(context, Constant.NOTIFICATION_CHANNEL_ALERT)
                .setAutoCancel(true)
                .setColor(context.resources.getColor(R.color.colorAccent, null))
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher_round))
            if (contentIntent != null) builder?.setContentIntent(contentIntent)
            manager?.notify(QUICK_NOTIFICATION_CHANNEL_ID, builder?.build())
        }
    }

    companion object {
        private const val QUICK_NOTIFICATION_CHANNEL_ID = 69
        fun show(
            context: Context,
            contentTitle: String,
            contentText: String,
            contentIntent: PendingIntent?
        ): QuickNotification {
            val quickNotification = QuickNotification(context)
            quickNotification.show(contentTitle, contentText, contentIntent)
            return quickNotification
        }
    }
}