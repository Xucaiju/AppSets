package xcj.appsets.manager

import android.content.Context
import android.content.res.Configuration
import xcj.appsets.Constant
import xcj.appsets.util.PreferenceUtil
import xcj.appsets.util.getSharedPreferences
import java.util.*

class LocaleManager(var context: Context) {
    val locale: Locale
        get() = if (isCustomLocaleEnabled(context)) customLocale else Locale.getDefault()

    private val customLocale: Locale
        private get() {
            val language: String? = PreferenceUtil.getString(context, Constant.PREFERENCE_LOCALE_LANG)
            val country: String? = PreferenceUtil.getString(context, Constant.PREFERENCE_LOCALE_COUNTRY)
            return if (language == "b") {
                Locale(country)
            } else Locale(language, country)
        }

    fun setLocale() {
        updateResources(locale)
    }

    fun setNewLocale(locale: Locale, isCustom: Boolean) {
        if (isCustom) saveLocale(locale)
        updateResources(locale)
    }

    private fun saveLocale(locale: Locale) {
        PreferenceUtil.putString(context, Constant.PREFERENCE_LOCALE_LANG, locale.language)
        PreferenceUtil.putString(context, Constant.PREFERENCE_LOCALE_COUNTRY, locale.country)
        PreferenceUtil.putBoolean(context, Constant.PREFERENCE_LOCALE_CUSTOM, true)
    }

    private fun updateResources(locale: Locale) {
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration =
            Configuration(resources.configuration)
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
    private fun isCustomLocaleEnabled(context: Context) = getSharedPreferences(context).getBoolean(Constant.PREFERENCE_LOCALE_CUSTOM, false)

}
