package xcj.appsets.viewmodel

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xcj.appsets.Constant
import xcj.appsets.HttpRequestStack
import xcj.appsets.model.*
import xcj.appsets.server.AppSetsServer
import xcj.appsets.util.PreferenceUtil
import java.util.*

class UserTrendsViewModel(application:Application):BaseViewModel(application) {
    val uerTrendsCatchTime = Constant.KEY_UER_TRENDS_CATCH_TIME

    val userTrendsPreferenceKey = Constant.KEY_USER_TRENDS_CONTENT
    val userNotificationPreferenceKey = Constant.KEY_USER_NOTIFICATION
    val userDevAppsPreferenceKey = Constant.KEY_USER_DEV_APPS
    val userInfoLitePreferenceKey = Constant.KEY_USER_INFO_LITE
    val userTrendsLiveData:MutableLiveData<List<UserTrends>>  = MutableLiveData()
    val userNotificationLiveData:MutableLiveData<List<UserNotification>>  = MutableLiveData()
    val userDevAppsLiveData:MutableLiveData<List<TodayApp>>  = MutableLiveData()
    val userInfoLiteLiveData:MutableLiveData<TrendsUserInfo?>  = MutableLiveData()
    val userAccount = AppSetsLoginInfo.getSavedInstance(getApplication())?.account
    init {
        userAccount?.let {
            fetchAllInCatch(userAccount)
        }
    }
    private fun fetchAllInCatch(userAccount: String){
        CoroutineScope(IO).launch {
            val aGson = Gson()
            val pair1 = Pair<Application, String>(getApplication(), userTrendsPreferenceKey)
            val pair2 = Pair<Application, String>(getApplication(), userNotificationPreferenceKey)
            val pair3 = Pair<Application, String>(getApplication(), userDevAppsPreferenceKey)
            val pair4 = Pair<Application, String>(getApplication(), userInfoLitePreferenceKey)

           /* val data1 = MyAsyncTask<List<UserTrends>>().execute(pair1).get()
            val data2 = MyAsyncTask<List<UserNotification>>().execute(pair2).get()
            val data3 = MyAsyncTask<List<TodayApp>>().execute(pair3).get()
            val data4 = MyAsyncTask<TrendsUserInfo>().execute(pair4).get()*/

            val json1 = PreferenceUtil.getString(getApplication(), userTrendsPreferenceKey)
            val json2 = PreferenceUtil.getString(getApplication(), userNotificationPreferenceKey)
            val json3 = PreferenceUtil.getString(getApplication(), userDevAppsPreferenceKey)
            val json4 = PreferenceUtil.getString(getApplication(), userInfoLitePreferenceKey)

            val data1 = aGson.fromJson<List<UserTrends>>(json1, object : TypeToken<List<UserTrends>>() {}.type)
            val data2 = aGson.fromJson<List<UserNotification>>(json2, object : TypeToken<List<UserNotification>>() {}.type)
            val data3 = aGson.fromJson<List<TodayApp>>(json3, object : TypeToken<List<TodayApp>>() {}.type)
            val data4 = aGson.fromJson<TrendsUserInfo>(json4, object : TypeToken<TrendsUserInfo>() {}.type)

            withContext(Main){
                if(data1!=null)
                    userTrendsLiveData.value = data1
                else{
                    HttpRequestStack.pushRequestToStack(userTrendsPreferenceKey)
                }
                if(data2!=null)
                    userNotificationLiveData.value = data2
                else{
                    HttpRequestStack.pushRequestToStack(userNotificationPreferenceKey)
                }
                if(data3!=null)
                    userDevAppsLiveData.value = data3
                else {
                    HttpRequestStack.pushRequestToStack(userDevAppsPreferenceKey)
                }
                if(data4!=null)
                    userInfoLiteLiveData.value = data4
                else{
                    HttpRequestStack.pushRequestToStack(userInfoLitePreferenceKey)
                }
                withContext(IO){
                    val requestStackSize = HttpRequestStack.getRequestStackSize()?:0
                    for(i in 0 until requestStackSize){
                        when(HttpRequestStack.popFromStackTop()){
                            userTrendsPreferenceKey->{
                                delay(2500)
                                fetchFromServer(userAccount, userTrendsPreferenceKey)
                            }
                            userNotificationPreferenceKey->{
                                delay(2500)
                                fetchFromServer(userAccount, userNotificationPreferenceKey)
                            }
                            userDevAppsPreferenceKey->{
                                delay(2500)
                                fetchFromServer(userAccount, userDevAppsPreferenceKey)
                            }
                            userInfoLitePreferenceKey->{
                                delay(2500)
                                fetchFromServer(userAccount, userInfoLitePreferenceKey)
                            }
                        }
                    }
                }

            }
        }
    }
    fun fetchFromServer(userAccount:String, key: String){
        when(key){
            userTrendsPreferenceKey->{
                Observable.fromCallable {
                    AppSetsServer.getUserTrendContent(userAccount, getApplication())
                }.subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        it?.let {
                            userTrendsLiveData.value = it
                            saveToCache1(it, userTrendsPreferenceKey)
                        }
                    }){}?.let {
                        compositeDisposable.add(it)
                    }
            }
            userNotificationPreferenceKey->{
                Observable.fromCallable {
                    AppSetsServer.getUserNotifications(userAccount, getApplication())
                }.subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        it?.let {
                            userNotificationLiveData.value = it
                            saveToCache2(it, userNotificationPreferenceKey)
                        }
                    }){}?.let {
                        compositeDisposable.add(it)
                    }
            }
            userDevAppsPreferenceKey->{
                Observable.fromCallable {
                    AppSetsServer.getUserDevApps(userAccount, getApplication())
                }.subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        it?.let {
                            userDevAppsLiveData.value = it
                            saveToCache3(it, userDevAppsPreferenceKey)
                        }
                    }){}?.let {
                        compositeDisposable.add(it)
                    }
            }
            userInfoLitePreferenceKey->{
                Observable.fromCallable {
                    AppSetsServer.getUserInfoLitebyAccount(userAccount, getApplication())
                }.subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        it?.let {
                            userInfoLiteLiveData.value = it
                            saveToCache4(it, userInfoLitePreferenceKey)
                        }
                    }){}?.let {
                        compositeDisposable.add(it)
                    }
            }
        }
    }
    private fun saveToCache1(
        any: List<UserTrends>,
        key: String
    ) {
        val aGson = Gson()
        val jsonString = aGson.toJson(any)
        PreferenceUtil.putString(getApplication(), key, jsonString)
        PreferenceUtil.putString(getApplication(), String.format("%s%s",key, uerTrendsCatchTime), Calendar.getInstance().timeInMillis.toString())
    }
    private fun saveToCache2(
        any: List<UserNotification>,
        key: String
    ) {
        val aGson = Gson()
        val jsonString = aGson.toJson(any)
        PreferenceUtil.putString(getApplication(), key, jsonString)
        PreferenceUtil.putString(getApplication(), String.format("%s%s",key, uerTrendsCatchTime), Calendar.getInstance().timeInMillis.toString())
    }
    private fun saveToCache3(
        any: List<TodayApp>,
        key: String
    ) {
        val aGson = Gson()
        val jsonString = aGson.toJson(any)
        PreferenceUtil.putString(getApplication(), key, jsonString)
        PreferenceUtil.putString(getApplication(), String.format("%s%s",key, uerTrendsCatchTime), Calendar.getInstance().timeInMillis.toString())
    }
    private fun saveToCache4(
        any: TrendsUserInfo,
        key: String
    ) {
        val aGson = Gson()
        val jsonString = aGson.toJson(any)
        PreferenceUtil.putString(getApplication(), key, jsonString)
        PreferenceUtil.putString(getApplication(), String.format("%s%s",key, uerTrendsCatchTime), Calendar.getInstance().timeInMillis.toString())
    }
    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
    class MyAsyncTask<T>:AsyncTask<Pair<Application, String>, Unit, T>(){
        override fun doInBackground(vararg params: Pair<Application, String>?): T {
            val  jsonString = params[0]?.let {
                PreferenceUtil.getString(it.first, it.second)
            }
            val aGson = Gson()
            return  aGson.fromJson<T>(jsonString, object : TypeToken<T>() {}.type)
        }
    }

}

