package xcj.appsets.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import xcj.appsets.Constant
import xcj.appsets.model.App
import xcj.appsets.model.TodayApp
import xcj.appsets.server.AppSetsServer
import xcj.appsets.util.PreferenceUtil
import xcj.appsets.util.setCacheCreateTime
import java.util.*

class FavoriteAppViewModel(application: Application):AndroidViewModel(application) {
    private val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
    private val compositeDisposable = CompositeDisposable()
    private var appsetsTodayFavoriteApps:MutableLiveData<MutableList<TodayApp>?>? = MutableLiveData<MutableList<TodayApp>?>()

    fun getAppsetsTodayFavoriteApps() = this.appsetsTodayFavoriteApps
    private var googlePlayFavoriteApps:MutableLiveData<MutableList<App>?>? = MutableLiveData<MutableList<App>?>()
    fun getGooglePlayFavoriteApps() = this.googlePlayFavoriteApps


    fun getUserFavoriteApps():Map<String, MutableLiveData<MutableList<Any>>>{
        val map = mutableMapOf<String, MutableLiveData<MutableList<Any>>>()
        map["AppSetsFavoriteApps"] = getAppsetsTodayFavoriteApps() as MutableLiveData<MutableList<Any>>
        map["GooglePlayFavoriteApps"] = getGooglePlayFavoriteApps() as MutableLiveData<MutableList<Any>>
        return map
    }


    private fun getFavoriteAppsDataFromCatch(favoriteType:String) {
        val jsonString: String?
        val appData:Any?
        when(favoriteType) {
            Constant.FAVORITE_APPSETS-> {
                jsonString = PreferenceUtil.getString(getApplication(), favoriteType)
                appData = gson.fromJson<MutableList<TodayApp>>(jsonString, object : TypeToken<MutableList<TodayApp>>() {}.type)
                if(appData!=null) {
                    this.appsetsTodayFavoriteApps?.value = appData
                } else {
                    getFavoriteAppsDataFromServer(favoriteType)
                }
            }
            Constant.FAVORITE_GOOGLE_PLAY-> {
                jsonString = PreferenceUtil.getString(getApplication(), favoriteType)
                appData = gson.fromJson<MutableList<App>>(jsonString, object : TypeToken<MutableList<App>>() {}.type)
                if(appData!=null) {
                    android.util.Log.d("Google Favorite Apps 获取方式","catch, 大小为:[${appData.size}]")


                    this.googlePlayFavoriteApps?.value = appData
                } else {
                    android.util.Log.d("Google Favorite Apps 获取方式","server")
                    getFavoriteAppsDataFromServer(favoriteType)
                }
            }
        }


    }
    private fun getFavoriteAppsDataFromServer(favoriteType:String) {
        when(favoriteType) {
            Constant.FAVORITE_APPSETS-> {
                compositeDisposable.add(Observable.fromCallable {
                        AppSetsServer.getLoggedUserFavoriteTodayApps(getApplication())
                    }
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        android.util.Log.d("TodayApp List","${it?.isEmpty()}")
                    if(it!=null) {
                        this.appsetsTodayFavoriteApps?.value = it
                        saveToCacheAppSets(it,favoriteType)
                        setCacheCreateTime(getApplication(), Calendar.getInstance().timeInMillis, "Favorite")
                    }
                }){

                })
            }
            Constant.FAVORITE_GOOGLE_PLAY-> {
                compositeDisposable.add(Observable.fromCallable {
                        AppSetsServer.getLoggedUserFavoriteGooglePlayApps(getApplication())
                    }
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if(it!=null) {
                            this.googlePlayFavoriteApps?.value = it
                            saveToCacheGooglePlay(it,favoriteType)
                            setCacheCreateTime(getApplication(), Calendar.getInstance().timeInMillis, "Favorite")
                        }
                    }){

                    })
            }
        }
    }
    private fun saveToCacheAppSets(
        appList: MutableList<TodayApp>?,
        key: String
    ) {
        val jsonString = gson.toJson(appList)
        PreferenceUtil.putString(getApplication(), key, jsonString)
    }
    private fun saveToCacheGooglePlay(
        appList: MutableList<App>?,
        key: String
    ) {
        val jsonString = gson.toJson(appList)
        PreferenceUtil.putString(getApplication(), key, jsonString)
    }
    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
    init {
        getFavoriteAppsDataFromCatch(Constant.FAVORITE_APPSETS)
        getFavoriteAppsDataFromCatch(Constant.FAVORITE_GOOGLE_PLAY)
    }

}