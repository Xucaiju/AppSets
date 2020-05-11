package xcj.appsets.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import xcj.appsets.AppSetsApplication
import xcj.appsets.database.AppSetsDatabase
import xcj.appsets.database.repository.AppSetsTodayAppRepository
import xcj.appsets.model.TodayApp

class TodayAppViewModelTest(application: Application):AndroidViewModel(application) {
    private var appsetsDB:AppSetsDatabase? = null
    var todayApp: LiveData<TodayApp?>? = null
    private var todayAppRepository:AppSetsTodayAppRepository? = null

    fun getTodayAppData(){

        var todayApp:LiveData<TodayApp?>?=null

        CoroutineScope(IO).launch {
            todayApp = todayAppRepository?.getTodayApp()
        }
        this.todayApp = todayApp
       /* runBlocking {


            todayApp= getTodayAppDataAsync().asLiveData().value
        }*/



    }
   /* fun getTodayAppDataAsync()= flow {
        val todayApp = todayAppRepository?.getTodayApp()
        emit(todayApp)
    }*/
    init {
        appsetsDB = AppSetsApplication.getAppSetsDB()
        todayAppRepository = appsetsDB?.todayAppDao()?.let { AppSetsTodayAppRepository(it) }
    }
}