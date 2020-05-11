package xcj.appsets.api

import android.content.Context

import com.dragons.aurora.playstoreapiv2.GooglePlayAPI
import xcj.appsets.TokenDispenserMirrors
import xcj.appsets.model.LoginInfo
import xcj.appsets.util.ApiBuilderUtil
import java.io.IOException

class PlayStoreApiAuthenticator {

    companion object{
        /**
         * Google Account
         */
        @Throws(IOException::class)
        @JvmStatic
        fun login(context: Context, email:String, password:String):Boolean{
            return true
        }

        /**
         * Anoumosy
         */
        @Throws(IOException::class)
        @JvmStatic
        fun login(context: Context):GooglePlayAPI?{
            val loginInfo = LoginInfo()
            loginInfo.setTokenDispenserUrl(TokenDispenserMirrors[context]!!)
            return ApiBuilderUtil.buildApi(context, loginInfo,  true)
        }

        @Throws(Exception::class)
        @JvmStatic
        fun getPlayApi(context: Context): GooglePlayAPI? {
            return ApiBuilderUtil.buildFromPreferences(context)
        }
    }

}