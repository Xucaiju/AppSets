package xcj.appsets.notification

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import xcj.appsets.Constant
import xcj.appsets.R
import xcj.appsets.util.NotificationUtil
import xcj.appsets.util.humanReadableByteSpeed
import xcj.appsets.util.isPrivilegedInstall

class GoogleAppDownloadNotification(context: Context) :BaseNotification(context) {
    fun show(
        contentTitle: String?,
        contentText: String?,
        contentIntent: PendingIntent?
    ) {
        if (NotificationUtil.isNotificationEnabled(context)) {
            manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            builder = NotificationCompat.Builder(context, Constant.NOTIFICATION_CHANNEL_ALERT)
                .setAutoCancel(true)
                .setColor(context.resources.getColor(R.color.colorGoogleBlue, null))
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_download_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher_round))
            if (contentIntent != null) builder?.setContentIntent(contentIntent)
            manager?.notify(GOOGLE_APP_DOWNLOAD_NOTIFICATION_CHANNEL_ID, builder?.build())
        }
    }
    fun show() {
        if (NotificationUtil.isNotificationEnabled(context)) {
            Glide.with(context.applicationContext)
                .asBitmap()
                .load(app?.getIconUrl())
                .into(object : SimpleTarget<Bitmap?>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap?>?
                    ) {
                        builder?.setLargeIcon(resource)
                    }
                })
            manager?.notify(app?.getPackageName().hashCode(), builder?.build())
        }
    }
    @SuppressLint("RestrictedApi")
    fun notifyResume(requestId: Int) {
        builder?.mActions?.clear()
        builder?.setOngoing(false)
        builder?.setContentText(context.getString(R.string.download_paused))
        builder?.addAction(
            R.drawable.ic_resume, context.getString(R.string.action_resume),
            getResumeIntent(requestId)
        )
        builder?.addAction(
            R.drawable.ic_cancel, context.getString(R.string.action_cancel),
            getCancelIntent(requestId)
        )
        show()
    }

    @SuppressLint("RestrictedApi")
    fun notifyProgress(
        progress: Int,
        downloadedBytesPerSecond: Long,
        requestId: Int
    ) {
        var progress = progress
        builder?.mActions?.clear()
        builder?.setOngoing(true)
        if (progress < 0) progress = 0
        builder?.setProgress(100, progress, false)
        builder?.setSubText(
            StringBuilder()
                .append(humanReadableByteSpeed(downloadedBytesPerSecond, true))
        )
        builder?.setContentText(StringBuilder().append(progress).append("%"))
        builder?.addAction(
            R.drawable.ic_resume, context.getString(R.string.action_pause),
            getPauseIntent(requestId)
        )
        builder?.addAction(
            R.drawable.ic_cancel, context.getString(R.string.action_cancel),
            getCancelIntent(requestId)
        )
        show()
    }

    @SuppressLint("RestrictedApi")
    fun notifyQueued() {
        builder?.mActions?.clear()
        builder?.setOngoing(false)
        builder?.setContentText(context.getString(R.string.download_queued))
        show()
    }

    @SuppressLint("RestrictedApi")
    fun notifyFailed() {
        builder?.mActions?.clear()
        builder?.setOngoing(false)
        builder?.setContentText(context.getString(R.string.download_failed))
        show()
    }

    @SuppressLint("RestrictedApi")
    fun notifyCompleted() {
        builder?.mActions?.clear()
        builder?.setOngoing(false)
        builder?.setContentText(context.getString(R.string.download_completed))
        builder?.setProgress(0, 0, false)
        if (!isPrivilegedInstall(context)) builder?.addAction(
            R.drawable.ic_installation,
            context.getString(R.string.details_install),
            installIntent
        )
        builder?.setAutoCancel(true)
        show()
    }

    @SuppressLint("RestrictedApi")
    fun notifyCancelled() {
        builder?.mActions?.clear()
        builder?.setOngoing(false)
        builder?.setContentText(context.getString(R.string.download_canceled))
        builder?.setProgress(0, 0, false)
        show()
    }

    @SuppressLint("RestrictedApi")
    fun notifyInstalling() {
        builder?.mActions?.clear()
        builder?.setContentText(context.getString(R.string.installer_status_ongoing))
        show()
    }

    @SuppressLint("RestrictedApi")
    fun notifyExtractionProgress() {
        builder?.mActions?.clear()
        builder?.setContentText(context.getString(R.string.download_extraction))
        show()
    }

    @SuppressLint("RestrictedApi")
    fun notifyExtractionFinished() {
        builder?.mActions?.clear()
        builder?.setOngoing(false)
        builder?.setContentText(context.getString(R.string.download_extraction_completed))
        show()
    }

    @SuppressLint("RestrictedApi")
    fun notifyExtractionFailed() {
        builder?.mActions?.clear()
        builder?.setOngoing(false)
        builder?.setContentText(context.getString(R.string.download_extraction_failed))
        show()
    }
    companion object {
        private const val GOOGLE_APP_DOWNLOAD_NOTIFICATION_CHANNEL_ID = 9983
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
    init {
        builder = getBuilder
    }
}