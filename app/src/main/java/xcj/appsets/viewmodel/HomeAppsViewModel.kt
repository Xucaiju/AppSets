package xcj.appsets.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import xcj.appsets.AppSetsApplication
import xcj.appsets.Constant
import xcj.appsets.enums.ErrorType
import xcj.appsets.model.App
import xcj.appsets.task.FeaturedAppsTask
import xcj.appsets.util.PreferenceUtil
import xcj.appsets.util.setCacheCreateTime
import java.lang.reflect.Modifier
import java.util.*

class HomeAppsViewModel(application: Application) : AndroidViewModel(application) {

    private var mapplication = application
    private var disposable: CompositeDisposable = CompositeDisposable()
    private var api: GooglePlayAPI? = AppSetsApplication.api
    private var gson: Gson = GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).create()
    private var topGames = MutableLiveData<MutableList<App>>()
    private var topApps = MutableLiveData<MutableList<App>>()
    private var topFamily = MutableLiveData<MutableList<App>>()
    private var error: MutableLiveData<ErrorType> = MutableLiveData()

    fun getTopGames() = topGames
    fun getTopApps() = topApps
    fun getTopFamilys() = topFamily
    fun getError() = error

    init {
        fetchAppsFromCache(Constant.TOP_APPS)
        fetchAppsFromCache(Constant.TOP_GAME)
        fetchAppsFromCache(Constant.TOP_FAMILY)
    }

    private fun fetchAppsFromCache(categoryId: String) {

        val type = object : TypeToken<MutableList<App>>() {}.type
        val jsonString: String? = PreferenceUtil.getString(mapplication, categoryId)
        val appList = gson.fromJson<MutableList<App>>(jsonString, type)

        if (appList != null && appList.isNotEmpty()) {
            Log.d("HomeApp获取方式","Catch")
            when (categoryId) {
                Constant.TOP_APPS -> topApps.value = appList
                Constant.TOP_GAME -> topGames.value = appList
                Constant.TOP_FAMILY -> topFamily.value = appList
            }
        } else{
            Log.d("HomeApp获取方式","网络")
            fetchApps(categoryId)
        }
    }

    private fun fetchApps(categoryId: String) {
        disposable.add(Observable.fromCallable {
                FeaturedAppsTask(mapplication).getApps(
                    api,
                    getPlayCategoryId(categoryId),
                    GooglePlayAPI.SUBCATEGORY.TOP_FREE
                )
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it?.let {applist->
                    when (categoryId) {
                        Constant.TOP_APPS -> topApps.value = applist
                        Constant.TOP_GAME -> topGames.value = applist
                        Constant.TOP_FAMILY -> topFamily.value = applist
                    }
                    Log.d("apps保存状态", "即将保存")
                    saveToCache(it, categoryId)
                    Log.d("apps保存状态", "已保存到Catch")
                    setCacheCreateTime(mapplication, Calendar.getInstance().timeInMillis, "Home")
                }

            }) {
                error.value = ErrorType.UNKNOWN
            }
        )
    }

    private fun getPlayCategoryId(categoryId: String): String {
        return when (categoryId) {
            Constant.TOP_FAMILY -> Constant.CATEGORY_FAMILY
            Constant.TOP_GAME -> Constant.CATEGORY_GAME
            else -> Constant.CATEGORY_APPS
        }
    }

    private fun saveToCache(
        appList: MutableList<App>?,
        key: String
    ) {
        val jsonString = gson.toJson(appList)
        PreferenceUtil.putString(mapplication, key, jsonString)
    }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }

}