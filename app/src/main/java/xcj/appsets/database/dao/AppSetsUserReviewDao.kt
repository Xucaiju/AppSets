package xcj.appsets.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import xcj.appsets.model.AppSetsUserReview

@Dao
interface AppSetsUserReviewDao {
    @Query("SELECT * from appsetsuserreview where todayAppId =:appId")
    fun queryCurrentTodayAppReviewsByAppId(appId:Int): LiveData<List<AppSetsUserReview>>

    @Insert
    suspend fun insertUserReviewToTodayApp(userReview: AppSetsUserReview):Long

    @Update
    suspend fun updataCurrentTodayAppUserReview(userReview: AppSetsUserReview)


    @Query("SELECT count(id) from appsetsuserreview")
    fun queryRowCount(): Int

    @Delete
    suspend fun deleteCurrentTodayAppUserReview(userReview: AppSetsUserReview)
}