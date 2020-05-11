package xcj.appsets

import android.content.SharedPreferences
import java.util.*

class SharedPreferencesTranslator(private val prefs: SharedPreferences) {
    fun getString(id: String, vararg params: Any?): String {
        return String.format(
            prefs.getString(
                getFullId(
                    id
                ), id
            )!!, *params
        )
    }

    fun putString(id: String, value: String?) {
        prefs.edit().putString(getFullId(id), value).apply()
    }

    companion object {
        private const val PREFIX = "translation"
        private fun getFullId(partId: String): String {
            return PREFIX + "_" + Locale.getDefault().language + "_" + partId
        }
    }

}
