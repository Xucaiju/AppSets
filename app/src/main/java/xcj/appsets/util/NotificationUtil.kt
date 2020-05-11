package xcj.appsets.util

import android.content.Context
import androidx.core.app.NotificationCompat
import xcj.appsets.Constant

object NotificationUtil {
    fun isNotificationEnabled(context: Context?): Boolean {
        return getSharedPreferences(context!!).getBoolean(Constant.PREFERENCE_NOTIFICATION_TOGGLE, true)
    }

    fun getNotificationPriority(context: Context?): Int {
        val prefValue: String? = getSharedPreferences(context!!).getString(Constant.PREFERENCE_NOTIFICATION_PRIORITY, "")
        return when (prefValue) {
            "1" -> NotificationCompat.PRIORITY_HIGH
            "2" -> NotificationCompat.PRIORITY_MAX
            else -> NotificationCompat.PRIORITY_DEFAULT
        }
    }
}
