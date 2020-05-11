package xcj.appsets.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import xcj.appsets.model.AppSetsTodayFavoriteApp

@Dao
interface AppSetsUserFavoriteAppsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserFavoriteApps(favoriteApp: AppSetsTodayFavoriteApp):Long

    @Update(onConflict = OnConflictStrategy.REPLACE )
    suspend fun insertUserFavoriteApp(favoriteApp: AppSetsTodayFavoriteApp):Int

    @Query("DELETE FROM AppSetsTodayFavoriteApp WHERE userAccount=:account and todayAppId =:appId")
    suspend fun deleteFavoriteAppByAccountAndAppId(account:String, appId:Int)


    @Query("SELECT count(*) FROM AppSetsTodayFavoriteApp ")
    fun queryFavoriteAppsRecordLength():Int


    @Query("SELECT count(*) FROM AppSetsTodayFavoriteApp WHERE todayAppId=:id")
    fun queryTodayAppById(id: Int): LiveData<Int?>

    //@Query("SELECT * from")
}