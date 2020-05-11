package xcj.appsets.util

import android.util.Log
import java.io.*

class Root {
    companion object{
       private const val TAG:String = "SAIRoot"
        @JvmStatic
        fun requestRoot(): Boolean {
            return try {
                val root = Runtime.getRuntime().exec("su -c exit")
                root.waitFor()
                root.exitValue() == 0
            } catch (e: java.lang.Exception) {
                Log.w(TAG, "Unable to acquire root access: ")
                Log.w(TAG, e)
                false
            }
        }
    }

    private var mSuProcess: Process? = null
    private var mIsAcquired = true
    private var mIsTerminated = false

    private var mWriter: BufferedWriter? = null
    private var mReader: BufferedReader? = null
    private var mErrorReader: BufferedReader? = null
    init {
        try{
            mSuProcess = Runtime.getRuntime().exec("su")
            mWriter = BufferedWriter(OutputStreamWriter(mSuProcess!!.outputStream))
            mReader = BufferedReader(InputStreamReader(mSuProcess!!.inputStream))
            mErrorReader = BufferedReader(InputStreamReader(mSuProcess!!.errorStream))
            exec("echo test")
        }catch (e: IOException){
            mIsAcquired = false
            mIsTerminated = true
            Log.w(TAG, "Unable to acquire root access: ")
            Log.w(Root.TAG, e)
        }

    }

    fun exec(command: String): String? {
        try {
            val sb = StringBuilder()
            val breaker =
                "『BREAKER』" //Echoed after main command and used to determine when to stop reading from the stream
            mWriter!!.write("$command\necho $breaker\n")
            mWriter!!.flush()
            val buffer = CharArray(256)
            while (true) {
                sb.append(buffer, 0, mReader!!.read(buffer))
                val bi = sb.indexOf(breaker)
                if (bi != -1) {
                    sb.delete(bi, bi + breaker.length)
                    break
                }
            }
            return sb.toString().trim { it <= ' ' }
        } catch (e: Exception) {
            mIsAcquired = false
            mIsTerminated = true
            Log.w(TAG, "Unable execute command: ")
            Log.w(TAG, e)
        }
        return null
    }

    fun readError(): String? {
        try {
            val sb = java.lang.StringBuilder()
            val breaker = "『BREAKER』"
            mWriter!!.write("echo $breaker >&2\n")
            mWriter!!.flush()
            val buffer = CharArray(256)
            while (true) {
                sb.append(buffer, 0, mErrorReader!!.read(buffer))
                val bi = sb.indexOf(breaker)
                if (bi != -1) {
                    sb.delete(bi, bi + breaker.length)
                    break
                }
            }
            return sb.toString().trim { it <= ' ' }
        } catch (e: java.lang.Exception) {
            mIsAcquired = false
            mIsTerminated = true
            Log.w(TAG, "Unable execute command: ")
            Log.w(TAG, e)
        }
        return null
    }
    fun terminate() {
        if (mIsTerminated) return
        mIsTerminated = true
        mSuProcess!!.destroy()
    }

    fun isTerminated()  = mIsTerminated

    fun isAcquired() = mIsAcquired
}