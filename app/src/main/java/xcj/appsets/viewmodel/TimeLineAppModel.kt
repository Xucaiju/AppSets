package xcj.appsets.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import xcj.appsets.Constant
import xcj.appsets.model.TodayApp
import xcj.appsets.server.AppSetsServer
import xcj.appsets.util.PreferenceUtil
import xcj.appsets.util.setCacheCreateTime
import java.util.*

class TimeLineAppModel(var mApplication: Application):BaseViewModel(mApplication) {
    private val key = Constant.ALL_TODAY_APPS
    private val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
    private var allTodayApps:MutableLiveData<MutableList<TodayApp>?>? = MutableLiveData()
    val apps:MutableLiveData<MutableList<TodayApp>?>?
        get() = this.allTodayApps
    init {
        fetchFromCatch()
    }
    private fun fetchFromCatch(){
        val type = object : TypeToken<MutableList<TodayApp>>() {}.type
        val jsonString: String? = PreferenceUtil.getString(mApplication, key)
        val appList = gson.fromJson<MutableList<TodayApp>>(jsonString, type)

        if (appList != null && appList.isNotEmpty()) {
            Log.d("HomeApp获取方式","Catch")
           allTodayApps?.value = appList
        } else{
            Log.d("HomeApp获取方式","网络")
            fetchAppsFromServer()
        }
    }
    private fun fetchAppsFromServer(){
        compositeDisposable.add(Observable.fromCallable {
           AppSetsServer.getALlTodayApps(mApplication)
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                allTodayApps?.value = it
                Log.d("TimeLineApps保存状态", "即将保存")
                saveToCache(it, key)
                Log.d("TimeLineApps保存状态", "已保存到Catch")
                setCacheCreateTime(mApplication, Calendar.getInstance().timeInMillis, "TimeLineApps")
            }) {
               // error.setValue(ErrorType.UNKNOWN)
            }
        )
    }
    companion object{

    }
    private fun saveToCache(
        appList: MutableList<TodayApp>?,
        key: String
    ) {
        val jsonString = gson.toJson(appList)
        PreferenceUtil.putString(mApplication, key, jsonString)
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}