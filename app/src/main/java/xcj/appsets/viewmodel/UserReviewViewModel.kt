package xcj.appsets.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xcj.appsets.Constant
import xcj.appsets.model.AppSetsUserReview
import xcj.appsets.server.AppSetsServer
import xcj.appsets.server.AppSetsServer.Companion.gson
import xcj.appsets.util.PreferenceUtil
import xcj.appsets.util.setCacheCreateTime
import java.util.*

class UserReviewViewModel(application: Application):BaseViewModel(application) {
    val key = Constant.USER_REVIEW
    private val userReviews:MutableLiveData<List<AppSetsUserReview>?>? = MutableLiveData()
    val reviews:MutableLiveData<List<AppSetsUserReview>?>?
        get() = userReviews

    private suspend fun getReviewFromCatch(appId:Int?){
        withContext(IO){
            val jsonString = PreferenceUtil.getString(getApplication(), key+appId)
            val appData = gson.fromJson<MutableList<AppSetsUserReview>>(jsonString, object : TypeToken<MutableList<AppSetsUserReview>>() {}.type)
            if(appData!=null){
                withContext(Main){
                    userReviews?.value = appData
                }
            }else{
                getReviewFromServer(appId)
            }
        }

    }
    fun featchUserReviewsByAppId(appId:Int?){
        CoroutineScope(IO).launch{
            getReviewFromCatch(appId)
        }

    }
    private  suspend fun getReviewFromServer(appId:Int?){
        withContext(IO){
            val allUserReview = AppSetsServer.getAllUserReview(getApplication(), appId)
            if(allUserReview!=null) {
               withContext(Main){
                   userReviews?.value = allUserReview
               }
                saveToCacheAppSets(allUserReview, key, getApplication(), appId)
                setCacheCreateTime(getApplication(), Calendar.getInstance().timeInMillis,
                    "TodayAppUserReview$appId"
                )
            }
        }
       /* compositeDisposable.add(Observable.fromCallable {
            AppSetsServer.getAllUserReview(getApplication())
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                if(it!=null) {
                    userReviews?.value = it
                    saveToCacheAppSets(it,"AppSetsUserReview")
                    setCacheCreateTime(getApplication(), Calendar.getInstance().timeInMillis, "TodayAppUserReview")
                }
            }){

            })*/
    }
    companion object{
        @JvmStatic
        fun saveToCacheAppSets(
            appList: List<AppSetsUserReview>?,
            key: String,
            context: Context,
            appId: Int?
        ) {
            val jsonString = gson.toJson(appList)
            PreferenceUtil.putString(context, key+appId, jsonString)
        }
    }


}