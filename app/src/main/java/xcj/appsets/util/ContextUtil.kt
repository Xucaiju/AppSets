package xcj.appsets.util

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast

internal object ContextUtil {
    fun toast(context: Context, stringId: Int, vararg params: String?) {
        toastLong(context, context.getString(stringId, *params as Array<Any?>))
    }

    fun toastShort(context: Context?, message: String?) {
        runOnUiThread(Runnable {
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT
            ).show()
        })
    }

    fun toastLong(context: Context?, message: String?) {
        runOnUiThread(Runnable {
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_LONG
            ).show()
        })
    }

    fun runOnUiThread(action: Runnable) {
        if (isUiThread) {
            action.run()
        } else {
            Handler(Looper.getMainLooper()).post { action.run() }
        }
    }

    fun runOnUiThread(action: Runnable, delay: Int) {
        if (isUiThread) {
            action.run()
        } else {
            Handler(Looper.getMainLooper())
                .postDelayed({ action.run() }, delay.toLong())
        }
    }

    val isUiThread: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Looper.getMainLooper().isCurrentThread else Thread.currentThread() === Looper.getMainLooper().thread

    fun isAlive(context: Context?): Boolean {
        if (context !is Activity) {
            return false
        }
        return !context.isDestroyed
    }
}