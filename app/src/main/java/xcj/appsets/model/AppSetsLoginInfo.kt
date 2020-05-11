package xcj.appsets.model

import android.content.Context
import com.google.gson.Gson
import xcj.appsets.util.AppSetsAccountant
import xcj.appsets.util.PreferenceUtil

data class AppSetsLoginInfo(var account: String? = "",
                            var username:String?  ="",
                            var avatar: String? = "",
                            var phoneNumber: String? = "",
                            var favoriteAppId: Int? = 0,
                            var todayAppId: Int? = 0,
                            var signupTime: String? = "",
                            var lastSigninTime: String? = "",
                            var uic:String?="") {


    companion object {
        @JvmStatic
        fun save(context: Context, appSetsLoginInfo: AppSetsLoginInfo) {
            val gson = Gson()
            val appSetsLoginString = gson.toJson(appSetsLoginInfo, appSetsLoginInfo::class.java)
            PreferenceUtil.putBoolean(context, AppSetsAccountant.APPSETS_USER_LOGGEDIN, true)
            PreferenceUtil.putString(context, AppSetsAccountant.APPSETS_DATA, appSetsLoginString)
        }

        @JvmStatic
        fun getSavedInstance(context: Context): AppSetsLoginInfo? {
            val gson = Gson()
            val appSetsLoginString: String? =
                PreferenceUtil.getString(context, AppSetsAccountant.APPSETS_DATA)
            val appSetsLoginInfo = gson.fromJson(appSetsLoginString, AppSetsLoginInfo::class.java)
            return appSetsLoginInfo ?: AppSetsLoginInfo()
        }

        @JvmStatic
        fun removeSavedInstance(context: Context) {
            PreferenceUtil.putString(context, AppSetsAccountant.APPSETS_DATA, "")
        }
    }

    override fun toString(): String {
        return "AppSetsLoginInfo(account=$account, username=$username, avatar=$avatar, phoneNumber=$phoneNumber, favoriteAppId=$favoriteAppId, todayAppId=$todayAppId, signupTime=$signupTime, lastSigninTime=$lastSigninTime, uic=$uic)"
    }


}