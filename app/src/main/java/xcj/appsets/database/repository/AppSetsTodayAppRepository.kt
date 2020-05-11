package xcj.appsets.database.repository

import androidx.lifecycle.LiveData
import xcj.appsets.database.dao.TodayAppDao
import xcj.appsets.model.TodayApp

class AppSetsTodayAppRepository(private val todayAppDao: TodayAppDao) {
    suspend fun saveTodayApp(todayApp:TodayApp) {
        todayAppDao.insertTodayApp(todayApp)
    }

    val allTodayApp: LiveData<List<TodayApp>> = todayAppDao.qureyAllTodayApp()

    fun getTodayApp():LiveData<TodayApp?> {

        return todayAppDao.queryTodayApp()
    }

    suspend fun updateTodayApp(app:TodayApp):Int {
        return todayAppDao.updateTodayApp(app)
    }
}