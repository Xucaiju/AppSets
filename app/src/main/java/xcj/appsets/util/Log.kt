package xcj.appsets.util

import android.content.Context
import android.os.Environment
import android.util.Log
import xcj.appsets.Constant
import java.io.File
import java.io.FileWriter
import java.io.IOException

class Log {
    companion object{
        val defMessage = "User not provide a message for this!"
        fun e(message: String?, vararg args: Any?) {
            e(String.format(message!!, *args))
        }

        fun e(message: String?) {
            Log.e(Constant.TAG, message?:defMessage)
        }

        fun i(message: String?, vararg args: Any?) {
            i(String.format(message!!, *args))
        }

        fun i(message: String?) {
            Log.i(Constant.TAG, message?:defMessage)
        }

        fun d(message: String?, vararg args: Any?) {
            d(String.format(message!!, *args))
        }

        fun d(message: String?) {
            Log.d(Constant.TAG, message?:defMessage)
        }

        fun w(message: String?, vararg args: Any?) {
            w(String.format(message!!, *args))
        }

        fun w(message: String?) {
            Log.w(Constant.TAG, message?:defMessage)
        }

        fun writeToFile(context: Context, any: Any) {
            try {
                val out =
                    FileWriter(File(context.filesDir, "AuroraLogs.txt"))
                out.write(any.toString())
                out.close()
            } catch (e: IOException) {
                e(e.message)
            }
        }

        fun writeLogFile(any: Any) {
            try {
                val out = FileWriter(
                    File(
                        Environment.getExternalStorageDirectory().path,
                        "Aurora/Logcat.txt"
                    )
                )
                out.write(any.toString())
                out.close()
            } catch (e: IOException) {
                e(e.message)
            }
        }
    }

}