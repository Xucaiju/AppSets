package xcj.appsets.util
import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import org.json.JSONObject
import java.util.*
class PreferenceUtil {
    companion object{
        fun remove(context: Context?, key: String?) {
            getSharedPreferences(context!!).edit().remove(key).apply()
        }

        fun putString(
            context: Context,
            key: String?,
            value: String?
        ) {
            getSharedPreferences(context.applicationContext).edit().putString(key, value).apply()
        }

        fun putInteger(
            context: Context,
            key: String?,
            value: Int
        ) {
            getSharedPreferences(context.applicationContext).edit().putInt(key, value).apply()
        }

        fun putFloat(
            context: Context,
            key: String?,
            value: Float
        ) {
            getSharedPreferences(context.applicationContext).edit().putFloat(key, value).apply()
        }

        fun putBoolean(
            context: Context,
            key: String?,
            value: Boolean
        ) {
            getSharedPreferences(context.applicationContext).edit().putBoolean(key, value).apply()
        }

        fun putListString(
            context: Context,
            key: String?,
            stringList: ArrayList<String>
        ) {
            val myStringList = stringList.toTypedArray()
            getSharedPreferences(context.applicationContext).edit()
                .putString(key, TextUtils.join("‚‗‚", myStringList)).apply()
        }

        fun putStringSet(
            context: Context,
            key: String?,
            set: Set<String?>?
        ) {
            getSharedPreferences(context.applicationContext).edit().putStringSet(key, set).apply()
        }


        fun getString(context: Context, key: String?): String? {
            return getSharedPreferences(context.applicationContext).getString(key, "")
        }

        fun getInteger(context: Context, key: String?): Int {
            return getSharedPreferences(context.applicationContext).getInt(key, 0)
        }

        fun getFloat(context: Context, key: String?): Float {
            return getSharedPreferences(context.applicationContext).getFloat(key, 0.0f)
        }

        fun getBoolean(context: Context, key: String?): Boolean? {
            return getSharedPreferences(context.applicationContext).getBoolean(key, false)
        }

        fun getListString(
            context: Context,
            key: String?
        ): ArrayList<String>? {
            return ArrayList(
                listOf(
                    *TextUtils.split(
                        getSharedPreferences(context.applicationContext).getString(key, ""), "‚‗‚"
                    )
                )
            )
        }

        fun getStringSet(
            context: Context,
            key: String?
        ): Set<String?>? {
            return getSharedPreferences(context.applicationContext)
                .getStringSet(key, HashSet<String>())
        }

        fun saveMap(
            context: Context?,
            map: MutableMap<String, String>,
            key: String?
        ) {
            val mPreferences: SharedPreferences = getSharedPreferences(context!!)
            if (mPreferences != null) {
                val jsonObject = JSONObject(map as Map<*, *>)
                val jsonString = jsonObject.toString()
                val editor = mPreferences.edit()
                editor.remove(key).apply()
                editor.putString(key, jsonString)
                editor.commit()
            }
        }

        fun getMap(
            context: Context?,
            key: String?
        ): MutableMap<String, String> {
            val outputMap: MutableMap<String, String> =
                HashMap()
            val mPreferences: SharedPreferences = getSharedPreferences(context!!)
            try {
                if (mPreferences != null) {
                    val jsonString = mPreferences.getString(key, JSONObject().toString())
                    val jsonObject = JSONObject(jsonString)
                    val keysItr = jsonObject.keys()
                    while (keysItr.hasNext()) {
                        val k = keysItr.next()
                        val value = jsonObject[k] as String
                        outputMap[k] = value
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return outputMap
        }
    }

}