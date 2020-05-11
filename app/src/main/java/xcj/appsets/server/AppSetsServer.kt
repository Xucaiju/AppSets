package xcj.appsets.server

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import xcj.appsets.AppSetsApplication
import xcj.appsets.Constant
import xcj.appsets.Constant.APPSETS_PRIMARY_SERVER
import xcj.appsets.adapter.OkHttpClientAdapter
import xcj.appsets.database.AppSetsDatabase
import xcj.appsets.database.repository.AppSetsTodayAppRepository
import xcj.appsets.database.repository.AppSetsUserFavoriteAppsRepository
import xcj.appsets.database.repository.AppSetsUserReviewRepository
import xcj.appsets.enums.SignResultCode
import xcj.appsets.model.*
import xcj.appsets.util.PreferenceUtil

class AppSetsServer {
    companion object {
        @JvmField
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()

        var requestUrl: String? = StringUtils.EMPTY
        var httpClientAdapter: OkHttpClientAdapter? = null
        var loggedInfo: AppSetsLoginInfo? = null
        var response: String? = null
        var appsetsDB:AppSetsDatabase? = null

        @JvmStatic
        fun signUp(context: Context, userInfo: User): SignResultCode {
            requestUrl = "${APPSETS_PRIMARY_SERVER}user/${userInfo.account}/${userInfo.password}/su"
            httpClientAdapter = OkHttpClientAdapter(context)
            response = httpClientAdapter?.get(requestUrl)?.let { String(it) }
            return when (response) {
                "\"SUCCESSFUL\"" -> {
                    SignResultCode.SUCCESSFUl
                }
                "\"FAILED\"" -> {
                    SignResultCode.FAILED
                }
                else -> {
                    SignResultCode.FAILED
                }
            }
        }

        @JvmStatic
        fun signIn(context: Context, userInfo: User): String? {
            requestUrl = "${APPSETS_PRIMARY_SERVER}user/${userInfo.account}/${userInfo.password}/si"
            httpClientAdapter = OkHttpClientAdapter(context)
            response = httpClientAdapter?.get(requestUrl)?.let { String(it) }
            val appSetsLoginInfo: AppSetsLoginInfo?
            return when (response) {
                "\"FAILED\"" -> "FAILED"
                else -> {
                    appSetsLoginInfo =
                        response?.let { getLoggedUserProfile(context, userInfo.account, it) }
                    if (appSetsLoginInfo != null) {
                        AppSetsLoginInfo.save(context, appSetsLoginInfo)
                    }
                    response
                }
            }
        }

        @JvmStatic
        fun getTodayData(context: Context): TodayApp? {
            return getTodaysDataByMobile(context)
        }

        @JvmStatic
        private fun getTodaysDataByMobile(context: Context): TodayApp? {
            val todayAppRepositoy:AppSetsTodayAppRepository?
            requestUrl = "${APPSETS_PRIMARY_SERVER}today"
            httpClientAdapter = OkHttpClientAdapter(context)
            response = httpClientAdapter?.get(requestUrl)?.let { String(it) }

            val fromJson = gson.fromJson<TodayApp?>(response, object : TypeToken<TodayApp>() {}.type)
            fromJson?.let {

                //appsetsDB = AppSetsApplication.getAppSetsDB()


                CoroutineScope(IO).launch {
                    getAppSetsDB().collect{db->
                        db?.todayAppDao()?.let { it1 -> AppSetsTodayAppRepository(it1).saveTodayApp(it) }
                    }
                  //  todayAppRepositoy?.saveTodayApp(it)
                }

            }
            return fromJson

        }
        @JvmStatic
        private fun getAppSetsDB():Flow<AppSetsDatabase?> = flow{
            val appsetsDB = AppSetsApplication.getAppSetsDB()
            emit(appsetsDB)
        }
        @JvmStatic
        fun getLoggedUserFavoriteTodayApps(context: Context): MutableList<TodayApp>? {

            loggedInfo = AppSetsLoginInfo.getSavedInstance(context)
            requestUrl =
                "${APPSETS_PRIMARY_SERVER}user/todayfavoriteapps/${loggedInfo?.account}/${loggedInfo?.uic}"
            httpClientAdapter = OkHttpClientAdapter(context)
            response = httpClientAdapter?.get(requestUrl)?.let { String(it) }

            return if (response == "NO_FAVORITE_APPS") {
                arrayListOf()
            } else {
                val todayAppList: MutableList<TodayApp>? = gson.fromJson<MutableList<TodayApp>>(
                    response,
                    object : TypeToken<MutableList<TodayApp>>() {}.type
                )
                todayAppList?.let {
                    CoroutineScope(IO).launch {
                        getAppSetsDB().collect{db->
                            db?.appSetsUserFavoriteAppsDao()?.let {  favoriteAppsDao->
                                val favoriteAppRepository = AppSetsUserFavoriteAppsRepository(favoriteAppsDao)
                                val favoriteAppsRowCount =
                                    favoriteAppRepository.favoriteAppsRowCount()
                                for (i in it.indices) {
                                    val appSetsTodayFavoriteApp = AppSetsTodayFavoriteApp(id = (favoriteAppsRowCount+i+1), todayAppId = it[i].id!!, userAccount = loggedInfo?.account!!)
                                    favoriteAppRepository.saveUserFavoriteApps(appSetsTodayFavoriteApp)
                                }
                            }
                        }
                    }
                }
                todayAppList ?: arrayListOf()
            }

        }

        @JvmStatic
        fun getLoggedUserFavoriteGooglePlayApps(context: Context): MutableList<App>? {
            loggedInfo = AppSetsLoginInfo.getSavedInstance(context)
            requestUrl =
                "${APPSETS_PRIMARY_SERVER}user/googleplayfavoriteapps/${loggedInfo?.account}/${loggedInfo?.uic}"
            httpClientAdapter = OkHttpClientAdapter(context)
            response = httpClientAdapter?.get(requestUrl)?.let { String(it) }
            val googleplayAppList: MutableList<App>? = gson.fromJson<MutableList<App>>(
                response,
                object : TypeToken<MutableList<App>>() {}.type
            )
           /* googleplayAppList?.let {

            }*/
            return googleplayAppList ?: arrayListOf()
        }

        @JvmStatic
        private fun getLoggedUserProfile(
            context: Context,
            account: String?,
            uic: String
        ): AppSetsLoginInfo {
            requestUrl = "${APPSETS_PRIMARY_SERVER}userdetails/${account}/${uic}"
            httpClientAdapter = OkHttpClientAdapter(context)
            response = httpClientAdapter?.get(requestUrl)?.let { String(it) }

            val userData: User? = gson.fromJson<User>(response, object : TypeToken<User>() {}.type)
            return AppSetsLoginInfo(
                account = userData?.account,
                username = userData?.username,
                avatar = userData?.avatar,
                phoneNumber = userData?.phoneNumber,
                favoriteAppId = userData?.favoriteAppsId,
                todayAppId = userData?.todayAppId,
                signupTime = userData?.signupTime,
                lastSigninTime = userData?.lastSigninTime,
                uic = uic
            )
        }

        @JvmStatic
        fun userSignout(context: Context): String {

            loggedInfo = AppSetsLoginInfo.getSavedInstance(context)
            requestUrl = "${APPSETS_PRIMARY_SERVER}user/${loggedInfo?.account}/so"
            httpClientAdapter = OkHttpClientAdapter(context)
            response = httpClientAdapter?.get(requestUrl)?.let { String(it) }

            return if (response == "退出登录成功") {
                Log.d("退出状态", "yes")
                "SIGN_OUT_SUCCESSFUL"
            } else {
                Log.d("退出状态", "没有登陆")
                "OPERATION_FAILED"
            }
        }

        @JvmStatic
        fun updateAppSetsTodayFavoriteApps(//更新当前用户 收藏的TodayApp 列表, 添加或者删除
            context: Context,
            account: String?,
            uic: String?,
            todayAppId: Int?,
            operationType: Int
        ): Int {
            httpClientAdapter = OkHttpClientAdapter(context)
            return when (operationType) {
                0 -> {
                    requestUrl =
                        "${APPSETS_PRIMARY_SERVER}userfavoriteapps/${todayAppId}/${account}/${uic}/d"
                    when (httpClientAdapter?.get(requestUrl)?.let { String(it) }) {
                        "0" -> 0
                        "1" -> 1
                        else -> 0
                    }

                }
                1 -> {
                    requestUrl =
                        "${APPSETS_PRIMARY_SERVER}userfavoriteapps/${todayAppId}/${account}/${uic}/i"

                    when (httpClientAdapter?.get(requestUrl)?.let { String(it) }) {
                        "0" -> 0
                        "1" -> 1
                        else -> 0
                    }
                }
                else -> {
                    0
                }
            }
        }

        @JvmStatic
        fun updateTodayAppFavoriteTimes(//更新特定TodayApp 的favorites 次数, +1 或者 -1
            context: Context,
            account: String?,
            uic: String?,
            todayAppId: Int?,
            operationType: Int
        ): Int {

            httpClientAdapter = OkHttpClientAdapter(context)
            return when (operationType) {
                0 -> {
                    requestUrl = "${APPSETS_PRIMARY_SERVER}today/favoritesDecrease/${todayAppId}/${account}/${uic}"
                    when (httpClientAdapter?.get(requestUrl)?.let { String(it) }) {
                        "0" -> 0
                        "1" -> 1
                        else -> 0
                    }

                }
                1 -> {
                    requestUrl = "${APPSETS_PRIMARY_SERVER}today/favoritesIncrease/${todayAppId}/${account}/${uic}"
                    when (httpClientAdapter?.get(requestUrl)?.let { String(it) }) {
                        "0" -> 0
                        "1" -> 1
                        else -> 0
                    }
                }
                else -> {
                    0
                }
            }
        }

        @JvmStatic
        fun getALlTodayApps(context: Context): MutableList<TodayApp>? {
            loggedInfo = AppSetsLoginInfo.getSavedInstance(context)
            requestUrl = "${APPSETS_PRIMARY_SERVER}user/alltodayapps/${loggedInfo?.account}/${loggedInfo?.uic}"
            httpClientAdapter = OkHttpClientAdapter(context)
            response = httpClientAdapter?.get(requestUrl)?.let { String(it) }
            return if(response=="未登录或与当前登录用户不匹配,请求出错!"){
                mutableListOf()
            }else{
                val todayAppList: MutableList<TodayApp>? = gson.fromJson<MutableList<TodayApp>>(
                    response,
                    object : TypeToken<MutableList<TodayApp>>() {}.type
                )
                todayAppList?.let {
                    CoroutineScope(IO).launch {
                        getAppSetsDB().collect{db->
                            db?.todayAppDao()?.let { todayAppDao ->
                                it.forEach{
                                    AppSetsTodayAppRepository(todayAppDao).saveTodayApp(it)
                                }
                            }
                        }
                    }
                }
                todayAppList ?: arrayListOf()
            }

        }


        @JvmStatic
        fun getAllUserReview(context: Context, appId: Int?): List<AppSetsUserReview>? {
            loggedInfo = AppSetsLoginInfo.getSavedInstance(context)
            requestUrl = "${APPSETS_PRIMARY_SERVER}todayapp/review/${appId}/${loggedInfo?.account}/${loggedInfo?.uic}"
            httpClientAdapter = OkHttpClientAdapter(context)
            response = httpClientAdapter?.get(requestUrl)?.let { String(it) }
            return if(response=="未登录或与当前登录用户不匹配,请求出错!"){
                listOf()
            }else{
                val fromJson = gson.fromJson<List<AppSetsUserReview>>(
                    response,
                    object : TypeToken<List<AppSetsUserReview>>() {}.type
                )
                fromJson?.let {
                    CoroutineScope(IO).launch {
                        getAppSetsDB().collect{db->
                            db?.appSetsUserReviewDao()?.let { appSetsUserReviewDao ->
                                it.forEach{
                                    AppSetsUserReviewRepository(appSetsUserReviewDao).addCurrentTodayAppUserReview(it)
                                }
                            }
                        }
                    }
                }
                fromJson
            }
        }
        @JvmStatic
        fun updateCurrentTodayAppUserReview(){

        }
        @JvmStatic
        fun addCurrentTodayAppUserReview(){

        }
        @JvmStatic
        fun uploadFcmToken(context: Context): String? {
            loggedInfo = AppSetsLoginInfo.getSavedInstance(context)
            val token = PreferenceUtil.getString(context, Constant.FIRE_BASE_FCM_TOKEN)
            requestUrl = "${APPSETS_PRIMARY_SERVER}user/firebase/token/${token}/${loggedInfo?.account}/${loggedInfo?.uic}"
            httpClientAdapter = OkHttpClientAdapter(context)
            response = httpClientAdapter?.get(requestUrl)?.let { String(it) }
            return when (response) {
                "success" -> {
                    "success"
                }
                "failed" -> {
                    "failed"
                }
                else -> {
                    "failed"
                }
            }
        }
    }
}