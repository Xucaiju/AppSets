package xcj.appsets.util

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import com.dragons.aurora.playstoreapiv2.ApiBuilderException
import com.dragons.aurora.playstoreapiv2.DeviceInfoProvider
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI
import org.apache.commons.lang3.StringUtils
import xcj.appsets.Constant
import xcj.appsets.adapter.OkHttpClientAdapter
import xcj.appsets.api.PlayStoreApiAuthenticator
import xcj.appsets.exception.CredentialsEmptyException
import xcj.appsets.manager.LocaleManager
import xcj.appsets.model.LoginInfo
import xcj.appsets.provider.NativeDeviceInfoProvider
import java.io.IOException
import java.util.*

class ApiBuilderUtil {
    companion object{

        @Throws(Exception::class)
        @JvmStatic
        fun buildFromPreferences(context: Context): GooglePlayAPI? {
            val loginInfo: LoginInfo? = LoginInfo.getSavedInstance(context)
            if (TextUtils.isEmpty(loginInfo?.getEmail()) || TextUtils.isEmpty(loginInfo?.getAuthToken())) {
                throw CredentialsEmptyException()
            }
            val builder = getBuilder(context, loginInfo!!)
            return builder.build()
        }

        @Throws(Exception::class)
        @JvmStatic
        fun generateApiWithNewAuthToken(context: Context): GooglePlayAPI? {
            val api: GooglePlayAPI?
            val loginInfo: LoginInfo?
            if (Accountant.isAnonymous(context)!!) {
                api = PlayStoreApiAuthenticator.login(context)
                loginInfo = LoginInfo.getSavedInstance(context)
            } else {
                loginInfo = LoginInfo.getSavedInstance(context)
                loginInfo!!.setAuthToken(null)
                val builder = getBuilder(context, loginInfo!!)
                api = builder.build()
            }
            if (api != null) {
                loginInfo!!.setGsfId(api.gsfId)
                loginInfo.setAuthToken(api.token)
                loginInfo.setDfeCookie(api.dfeCookie)
                loginInfo.setDeviceConfigToken(api.deviceConfigToken)
                loginInfo.setDeviceCheckinConsistencyToken(api.deviceCheckinConsistencyToken)
                LoginInfo.save(context, loginInfo)
            }
            Accountant.setAnonymous(context, Accountant.isAnonymous(context)!!)
            return api
        }

        @Throws(IOException::class)
        @JvmStatic
        fun buildApi(
            context: Context,
            loginInfo: LoginInfo,
            isAnonymous: Boolean
        ): GooglePlayAPI? {

            try {
                val builder = getBuilder(context, loginInfo)
                if (isAnonymous) builder.mtokenDispenserUrl = loginInfo.getTokenDispenserUrl()

                val api = builder.build()

                loginInfo.setEmail(builder.memail)
                if (api !== null) {
                    loginInfo.setGsfId(api.gsfId)
                    loginInfo.setAuthToken(api.token)
                    loginInfo.setDfeCookie(api.dfeCookie)
                    loginInfo.setDeviceConfigToken(api.deviceConfigToken)
                    loginInfo.setDeviceCheckinConsistencyToken(api.deviceCheckinConsistencyToken)
                    LoginInfo.save(context, loginInfo)
                    Accountant.setAnonymous(context, isAnonymous)
                }
                return api

            } catch (e: ApiBuilderException) {
                throw RuntimeException(e)
            }
        }
        @JvmStatic
        private fun getBuilder(
            context: Context,
            loginInfo: LoginInfo
        ): MyPlayStoreApiBuilder {
            val sharedPreferences: SharedPreferences = getSharedPreferences(context)
            val locale = sharedPreferences.getString(
                Constant.PREFERENCE_REQUESTED_LANGUAGE,
                StringUtils.EMPTY
            )
            loginInfo.setLocale(if (TextUtils.isEmpty(locale)) Locale.getDefault().language else locale!!)
            val builder = MyPlayStoreApiBuilder()
            builder.mhttpClient = OkHttpClientAdapter(context)
            builder.mdeviceInfoProvider = getDeviceInfoProvider(context)
            builder.mlocale = loginInfo.getLocale()
            builder.memail = loginInfo.getEmail()
            builder.maasToken = loginInfo.getAasToken()
            builder.mgsfId = loginInfo.getGsfId()
            builder.mauthToken = loginInfo.getAuthToken()
            builder.mdeviceCheckinConsistencyToken = loginInfo.getDeviceCheckinConsistencyToken()
            builder.mdeviceConfigToken = loginInfo.getDeviceConfigToken()
            builder.mdfeCookie = loginInfo.getDfeCookie()
            return builder
        }
        @JvmStatic
        fun getDeviceInfoProvider(context: Context): DeviceInfoProvider {
            return NativeDeviceInfoProvider().setContext(context).setLocaleString(LocaleManager(context).locale.toString())
        }

    }

}