package xcj.appsets.util

import android.content.Context
import xcj.appsets.model.LoginInfo

object Accountant {
    const val DATA = "DATA"
    const val EMAIL = "EMAIL"
    const val PROFILE_NAME = "PROFILE_NAME"
    const val PROFILE_AVATAR = "PROFILE_AVATAR"
    const val PROFILE_BACKGROUND = "PROFILE_BACKGROUND"
    const val LOGGED_IN = "LOGGED_IN"
    const val ANONYMOUS = "ANONYMOUS"

    fun isLoggedIn(context: Context): Boolean? {
        return PreferenceUtil.getBoolean(context, LOGGED_IN)
    }


    fun isAnonymous(context: Context): Boolean? {
        return PreferenceUtil.getBoolean(context, ANONYMOUS)
    }

    fun getUserName(context: Context): String? {
        return PreferenceUtil.getString(context, PROFILE_NAME)
    }

    fun getEmail(context: Context): String? {
        return PreferenceUtil.getString(context, EMAIL)
    }

    fun getImageURL(context: Context): String? {
        return PreferenceUtil.getString(context, PROFILE_AVATAR)
    }

    fun getBackgroundImageURL(context: Context): String? {
        return PreferenceUtil.getString(context, PROFILE_BACKGROUND)
    }

    fun completeCheckout(context: Context) {
        PreferenceUtil.remove(context, LOGGED_IN)
        PreferenceUtil.remove(context, EMAIL)
        PreferenceUtil.remove(context, PROFILE_NAME)
        PreferenceUtil.remove(context, PROFILE_AVATAR)
        PreferenceUtil.remove(context, PROFILE_BACKGROUND)
        LoginInfo.removeSavedInstance(context)
    }

    fun setLoggedIn(context: Context) {
        PreferenceUtil.putBoolean(context, LOGGED_IN, true)
    }

    fun setAnonymous(context: Context, value: Boolean) {
        PreferenceUtil.putBoolean(context, ANONYMOUS, value)
    }
}