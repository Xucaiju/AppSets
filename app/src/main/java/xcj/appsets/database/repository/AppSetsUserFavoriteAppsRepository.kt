package xcj.appsets.database.repository

import androidx.lifecycle.LiveData
import xcj.appsets.database.dao.AppSetsUserFavoriteAppsDao
import xcj.appsets.model.AppSetsTodayFavoriteApp
import xcj.appsets.model.TodayApp

class AppSetsUserFavoriteAppsRepository(private val favoriteAppDao: AppSetsUserFavoriteAppsDao) {
     suspend fun saveUserFavoriteApps(favoriteApp:AppSetsTodayFavoriteApp):Long {
        return favoriteAppDao.insertUserFavoriteApps(favoriteApp)
    }
    fun favoriteAppsRowCount():Int {
        return favoriteAppDao.queryFavoriteAppsRecordLength()
    }

    fun isCurrentUserFavorited(app:TodayApp):LiveData<Int?>{
        android.util.Log.d("appppppppppppppppppppppppppppppppp", app.toString())


        return favoriteAppDao.queryTodayAppById(app.id!!)
    }

}