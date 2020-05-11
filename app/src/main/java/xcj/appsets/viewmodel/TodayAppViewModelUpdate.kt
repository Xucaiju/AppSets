package xcj.appsets.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import xcj.appsets.AppSetsApplication
import xcj.appsets.database.repository.AppSetsTodayAppRepository
import xcj.appsets.database.repository.AppSetsUserFavoriteAppsRepository
import xcj.appsets.model.AppSetsLoginInfo
import xcj.appsets.model.AppSetsTodayFavoriteApp
import xcj.appsets.model.TodayApp
import xcj.appsets.server.AppSetsServer

class TodayAppViewModelUpdate(application:Application):BaseViewModel(application){
    private var todayAppRepository: AppSetsTodayAppRepository?=null
    private var userFavoriteAppsRepository: AppSetsUserFavoriteAppsRepository?=null
    var app: LiveData<TodayApp?>? = null
    var isFavorited:MutableLiveData<Int?>? = null

    fun getTodayAppFromDB() {

            todayAppRepository?.getTodayApp()?.let {
                app = it

        }
    }
    fun addCurrentTodayAppToFavorites() {
        viewModelScope.launch(IO) {
            val favoriteAppsRowCount = userFavoriteAppsRepository?.favoriteAppsRowCount()?:0
            val account = AppSetsLoginInfo.getSavedInstance(getApplication())?.account
            userFavoriteAppsRepository?.saveUserFavoriteApps(AppSetsTodayFavoriteApp(favoriteAppsRowCount+1, (app?.value?.id)?:0, account?:"Unknown"))
        }
    }
    fun getTodayAppFromServer() {
        CoroutineScope(IO).launch {
            AppSetsServer.getTodayData(getApplication())
        }

    }

    fun getIsFavorite(todayApp: TodayApp) {

        Observable.fromCallable { userFavoriteAppsRepository?.isCurrentUserFavorited(todayApp) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe ({
                    isFavorited?.value = it?.value
            }){
                Log.d("异常异常异常异常异常异常异常","yes")
            }?.let {
                compositeDisposable.add(it)
            }


       /* isFavorited?.value = todayApp.let {
                userFavoriteAppsRepository?.isCurrentUserFavorited(it)
            }*/
    }

    init {
        todayAppRepository = AppSetsApplication.getAppSetsDB()?.todayAppDao()?.let {
            AppSetsTodayAppRepository(it)
        }
        userFavoriteAppsRepository = AppSetsApplication.getAppSetsDB()?.appSetsUserFavoriteAppsDao()?.let {
            AppSetsUserFavoriteAppsRepository(it)
        }
        getTodayAppFromDB()

    }
}