package xcj.appsets.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.dragons.aurora.playstoreapiv2.SearchIterator
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import xcj.appsets.AppSetsApplication
import xcj.appsets.enums.ErrorType
import xcj.appsets.iterator.CustomAppListIterator
import xcj.appsets.manager.FilterManager
import xcj.appsets.model.App
import xcj.appsets.task.SearchTask
import xcj.appsets.util.NetworkUtil

class SearchAppsViewModel(private var mApplication: Application) :BaseViewModel(mApplication) {
    protected var iterator: CustomAppListIterator? = null
    protected var searchIterator: SearchIterator? = null
    protected var listMutableLiveData :MutableLiveData<MutableList<App>?>?= MutableLiveData<MutableList<App>?>()
    protected var relatedMutableLiveData = MutableLiveData<List<String>>()
    fun getRelatedTags():MutableLiveData<List<String>> {
        return relatedMutableLiveData
    }

    fun getQueriedApps(): MutableLiveData<MutableList<App>?> ?{
        return listMutableLiveData
    }

    fun fetchQueriedApps(query: String?, shouldIterate: Boolean) {
        Log.d("fetchQueriedApps","step1")
        if (!NetworkUtil.isConnected(mApplication)) {
            errorTypeMutableLiveData.value = ErrorType.NO_NETWORK
            return
        }
        Log.d("fetchQueriedApps","step2")
        Log.d("fetchQueriedApps","${AppSetsApplication.api}")

        this.api = AppSetsApplication.api
        Log.d("fetchQueriedApps This api","${this.api}")
        if (!shouldIterate)
            getIterator(query)
        compositeDisposable.add(Observable.fromCallable{ SearchTask(mApplication).getSearchResults(iterator) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                      /* it?.forEach {app->
                           Log.d("AppName","${app}")
                       }*/
                            listMutableLiveData?.value = it

                    }){
                        it?.let{
                            handleError(it)
                        }
                }
            )
        Log.d("fetchQueriedApps","step99")
    }
    private fun getIterator(query: String?) {
        try {
            searchIterator = SearchIterator(this.api, query)
            searchIterator?.apply {
                iterator = CustomAppListIterator(this)
                iterator?.setFilterEnabled(true)
                iterator?.setFilterModel(FilterManager.getFilterPreferences(mApplication))
            }

            //relatedMutableLiveData.setValue(iterator.getRelatedTags());
        } catch (err: Exception) {
            handleError(err)
        }
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
    init {
        Log.d("SearchAppsViewModel创建","已创建")
    }
}