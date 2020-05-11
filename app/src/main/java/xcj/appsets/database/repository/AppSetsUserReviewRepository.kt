package xcj.appsets.database.repository

import androidx.lifecycle.LiveData
import xcj.appsets.database.dao.AppSetsUserReviewDao
import xcj.appsets.model.AppSetsUserReview

class AppSetsUserReviewRepository(private val reviewDao:AppSetsUserReviewDao) {

    fun getCurrentTodayAppReviews(appId:Int): LiveData<List<AppSetsUserReview>> = reviewDao.queryCurrentTodayAppReviewsByAppId(appId)
    fun getTodayAppReviewsCount():Int {
        return reviewDao.queryRowCount()
    }

    suspend fun addCurrentTodayAppUserReview(userReview: AppSetsUserReview):Long {
        return reviewDao.insertUserReviewToTodayApp(userReview)
    }

    suspend fun updateTodayAppUserReview(userReview: AppSetsUserReview) = reviewDao.updataCurrentTodayAppUserReview(userReview)

    suspend fun delectCurrentTodayAppUserReview(userReview: AppSetsUserReview) = reviewDao.deleteCurrentTodayAppUserReview(userReview)
}