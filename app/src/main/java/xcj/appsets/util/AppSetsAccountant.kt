package xcj.appsets.util

import android.content.Context

object AppSetsAccountant {
    const val APPSETS_USER_LOGGEDIN = "APPSETS_USER_LOGGEDIN"
    const val APPSETS_DATA = "APPSETS_DATA"
    var APPSETS_USER_LOGGEDIN_AND_SKIP = "APPSETS_USER_LOGGEDIN_AND_SKIP"
    fun isAppSetsUserLoggedIn(context: Context): Boolean? {
        return PreferenceUtil.getBoolean(context, APPSETS_USER_LOGGEDIN)
    }
}