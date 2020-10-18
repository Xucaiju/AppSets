package xcj.appsets.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import xcj.appsets.model.TodayApp

@Dao
interface TodayAppDao {

    @Query("SELECT * FROM TodayApp WHERE showedDate <= cast(date('now') as TEXT) ORDER by showedDate DESC")
    fun qureyAllTodayApp(): LiveData<List<TodayApp>>

    @Query("SELECT * FROM TodayApp WHERE showedDate = cast(date('now') as TEXT)")
    fun queryTodayApp():LiveData<TodayApp?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodayApp(todayApp: TodayApp)

    @Update
    suspend fun updateTodayApp(todayApp: TodayApp):Int



   // @Query("SELECT todayApp.id, todayApp.appIcon, todayApp.appDisplayname, todayApp.appRecommendedPictureA, todayApp.appPrice, todayApp.appTypes, todayApp.favorites, todayApp.appFeatures, todayApp.editorNote, todayApp.DownloadLink, todayapp.appPackageName, b.userAccount as isFavorited From  todayapp, appsetsuserreview b where todayApp.id = b.todayAppId")

    /*  @Query("DELETE FROM word_table")
      suspend fun deleteAll()*/

}