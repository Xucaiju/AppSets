package xcj.appsets.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import xcj.appsets.AppSetsApplication.Companion.getAppSetsDB
import xcj.appsets.Constant.ALL_TODAY_APPS
import xcj.appsets.database.repository.AppSetsTodayAppRepository
import xcj.appsets.model.TodayApp
import xcj.appsets.server.AppSetsServer
import xcj.appsets.util.PreferenceUtil
import xcj.appsets.util.setCacheCreateTime
import java.lang.reflect.Type
import java.util.*


class TodayAppViewModel(application: Application) : AndroidViewModel(application) {
    var appSetsTodayAppRepository:AppSetsTodayAppRepository?=null
    val key = "AppSetstTodayApp"
    var mApplication: Application = application
    private val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var todayApp = MutableLiveData<TodayApp>()
    private val allTodayAppType: Type = object : TypeToken<MutableList<TodayApp>>() {}.type
    private var allTodayAppsJsonString: String? = null
    private var allTodayAppList: MutableList<TodayApp>? = null
    var isItExistInAllTodayApps = false
    init {
        appSetsTodayAppRepository = getAppSetsDB()?.todayAppDao()?.let {
            AppSetsTodayAppRepository(
                it
            )
        }
        allTodayAppsJsonString = PreferenceUtil.getString(mApplication, ALL_TODAY_APPS)
        allTodayAppList = allTodayAppsJsonString?.let {
            gson.fromJson<MutableList<TodayApp>>(allTodayAppsJsonString, allTodayAppType)
        }
        getAppDataFromCatch()
    }

    fun getTodayAppData() = this.todayApp
    fun getTodayAppDataForceViaServer(swipeRefresh:()->SwipeRefreshLayout){
        getAppDataFromServer()
        swipeRefresh().isRefreshing = false
    }
    fun setUpTodayAppFavoriteTimes(opeartionType:Int){
        val type = object : TypeToken<TodayApp>() {}.type
        val jsonString: String? = PreferenceUtil.getString(mApplication, key)
        val appData = gson.fromJson<TodayApp>(jsonString, type)
        when(opeartionType){
            0->{
              /*  CoroutineScope(IO).launch {
                    appSetsTodayAppRepository?.run {
                        getTodayApp().value?.let {
                            it.favorites = it.favorites?.minus(1)
                            updateTodayApp(it)
                        }
                    }
                }*/
                //today app favorite times --
                appData.favorites = appData.favorites?.minus(1)
                PreferenceUtil.putString(mApplication, key, gson.toJson(appData))
                setCacheCreateTime(mApplication, Calendar.getInstance().timeInMillis, "Today")
                todayApp.value?.favorites = todayApp.value?.favorites?.minus(1)
            }
            1->{
               /* CoroutineScope(IO).launch {
                appSetsTodayAppRepository?.run {
                    getTodayApp().value?.let {
                        it.favorites = it.favorites?.plus(1)
                        updateTodayApp(it)
                    }
                }
            }*/
                //today app favorite times ++
                appData.favorites = appData.favorites?.plus(1)
                PreferenceUtil.putString(mApplication, key, gson.toJson(appData))
                setCacheCreateTime(mApplication, Calendar.getInstance().timeInMillis, "Today")
                todayApp.value?.favorites = todayApp.value?.favorites?.plus(1)

            }
        }
    }

    private fun getAppDataFromCatch() {
        val type = object : TypeToken<TodayApp>() {}.type

        val jsonString: String? = PreferenceUtil.getString(mApplication, "AppSetstTodayApp")

        val appData = gson.fromJson<TodayApp>(jsonString, type)

        if (appData != null) {

            val calendarClient = Calendar.getInstance()
            val clientYear = calendarClient.get(Calendar.YEAR)-1990
            val clientMonth = calendarClient.get(Calendar.MONTH)+1
            val clientDayOfMonth = calendarClient.get(Calendar.DATE)

            val calendarApp = Calendar.getInstance().apply {
                time = appData.showedDate!!
            }


            val todayAppYear = calendarApp.get(Calendar.YEAR)-1990
            val todayAppMonth = calendarApp.get(Calendar.MONTH)+1
            val todayAppDayOfMonth = calendarApp.get(Calendar.DATE)
             if (
                 clientYear==todayAppYear
                 && clientMonth==todayAppMonth
                 && clientDayOfMonth==todayAppDayOfMonth
             ) {

                Log.d("TodaysApp获取方式", "Catch")
                todayApp.value = appData
                Log.d("否是今天推荐的", "是")

            } else {

                Log.d("否是今天推荐的", "不是")
                cleanCache()

                getAppDataFromServer()
            }
        } else {

            Log.d("TodaysApp获取方式", "网络")
            getAppDataFromServer()

        }
    }

    private fun getAppDataFromServer() {


        compositeDisposable.add(Observable.fromCallable {
                AppSetsServer.getTodayData(mApplication)
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                if (it != null) {

                    todayApp.value = it
                    //save to timeline apps catch
                    allTodayAppList?.let {allTodayApp->

                        for(app in allTodayApp){
                            if(app.appDisplayname == it.appDisplayname){
                                isItExistInAllTodayApps = true
                                break
                            }else
                                continue
                        }
                        if(!isItExistInAllTodayApps){
                            allTodayAppList?.add(it)
                            PreferenceUtil.putString(mApplication, ALL_TODAY_APPS, gson.toJson(allTodayAppList))
                            setCacheCreateTime(mApplication, Calendar.getInstance().timeInMillis, "TimeLineApps")
                        }

                    }



                    Log.d("TodaysApp保存状态", "即将保存")
                    saveToCache(it)
                    Log.d("TodaysApp保存状态", "已保存到Catch")
                    setCacheCreateTime(mApplication, Calendar.getInstance().timeInMillis, "Today")

                } else {

                    Log.d("信息异常", "todayApp服务器还没更新")

                }
            }) {
                //get from server exception!
            })

    }

    private fun cleanCache() {
        PreferenceUtil.putString(mApplication, key, null)//gson.toJson(TodayApp())
    }

    private fun saveToCache(
        app: TodayApp
    ) {
        val jsonString = gson.toJson(app)
        PreferenceUtil.putString(mApplication, key, jsonString)
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}