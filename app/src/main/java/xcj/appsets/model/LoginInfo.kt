package xcj.appsets.model

import android.content.Context
import android.text.TextUtils
import com.google.gson.Gson
import xcj.appsets.util.Accountant
import xcj.appsets.util.PreferenceUtil
import java.util.*

class LoginInfo {

    private var email: String? = null
    private var userName: String? = null
    private var userPicUrl: String? = null
    private var aasToken: String? = null
    private var gsfId: String? = null
    private var authToken: String? = null
    private var locale: String? = null
    private var tokenDispenserUrl: String? = null
    private var deviceDefinitionName: String? = null
    private var deviceDefinitionDisplayName: String? = null
    private var deviceCheckinConsistencyToken: String? = null
    private var deviceConfigToken: String? = null
    private var dfeCookie: String? = null
    companion object{
        @JvmStatic
        fun save(context: Context, loginInfo: LoginInfo) {
            val gson = Gson()
            val loginString = gson.toJson(loginInfo, LoginInfo::class.java)
            PreferenceUtil.putBoolean(context, Accountant.LOGGED_IN, true)
            PreferenceUtil.putString(context, Accountant.DATA, loginString)
        }

        @JvmStatic
        fun getSavedInstance(context: Context): LoginInfo? {
            val gson = Gson()
            val loginString: String? = PreferenceUtil.getString(context, Accountant.DATA)
            val loginInfo = gson.fromJson(loginString, LoginInfo::class.java)
            return loginInfo ?: LoginInfo()
        }

        @JvmStatic
        fun removeSavedInstance(context: Context) {
            PreferenceUtil.putString(context, Accountant.DATA, "")
        }
    }
    fun setAasToken(aasToken:String){
        this.aasToken = aasToken
    }
    fun getUserName():String? = this.userName


    fun getUserPicUrl():String? = this.userPicUrl

    fun getDeviceDefinitionName() = this.deviceDefinitionName

    fun getDeviceDefinitionDisplayName() = this.deviceDefinitionDisplayName

    fun setGsfId(gsfId:String){
        this.gsfId = gsfId
    }

    fun getGsfId() = this.gsfId

    fun setLocale(locale: String) {
        this.locale = locale
    }

    fun getAuthToken() = this.authToken

    fun setAuthToken(authToken: String?) {
        this.authToken = authToken
    }
    fun getEmail() = this.email

    fun setTokenDispenserUrl(url: String) {
        this.tokenDispenserUrl = url
    }

    fun getLocale(): Locale? = if (TextUtils.isEmpty(locale)) {
        Locale.getDefault()
    } else Locale(locale)

    fun isEmpty(): Boolean = email!!.isEmpty() && authToken!!.isEmpty()

    fun setDfeCookie(dfeCookie: String?) {
        this.dfeCookie = dfeCookie
    }

    fun setDeviceConfigToken(deviceConfigToken: String?) {
        this.deviceConfigToken = deviceConfigToken
    }

    fun setDeviceCheckinConsistencyToken(deviceCheckinConsistencyToken: String?) {
        this.deviceCheckinConsistencyToken = deviceCheckinConsistencyToken
    }

    fun setEmail(email: String?) {
        this.email = email
    }

    fun getTokenDispenserUrl(): String? = this.tokenDispenserUrl

    fun getAasToken(): String? = this.aasToken

    fun getDeviceCheckinConsistencyToken(): String? = this.deviceCheckinConsistencyToken

    fun getDeviceConfigToken(): String? = this.deviceConfigToken

    fun getDfeCookie(): String? = this.dfeCookie
    override fun toString(): String {
        return "LoginInfo(email=$email, userName=$userName, userPicUrl=$userPicUrl, aasToken=$aasToken, gsfId=$gsfId, authToken=$authToken, locale=$locale, tokenDispenserUrl=$tokenDispenserUrl, deviceDefinitionName=$deviceDefinitionName, deviceDefinitionDisplayName=$deviceDefinitionDisplayName, deviceCheckinConsistencyToken=$deviceCheckinConsistencyToken, deviceConfigToken=$deviceConfigToken, dfeCookie=$dfeCookie)"
    }

}