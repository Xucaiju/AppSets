package xcj.appsets.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.dragons.aurora.playstoreapiv2.AuthException
import com.dragons.aurora.playstoreapiv2.CategoryAppsIterator
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI.SUBCATEGORY
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import xcj.appsets.AppSetsApplication
import xcj.appsets.enums.ErrorType
import xcj.appsets.exception.CredentialsEmptyException
import xcj.appsets.exception.InvalidApiException
import xcj.appsets.iterator.CustomAppListIterator
import xcj.appsets.manager.FilterManager
import xcj.appsets.model.App
import xcj.appsets.task.CategoryAppsTask
import java.net.UnknownHostException

class CategoryAppsModel(application: Application) : BaseViewModel(application) {
    private var categoryAppsTask: CategoryAppsTask? = null
    private var iterator: CustomAppListIterator? = null
    private val listMutableLiveData: MutableLiveData<List<App>?> = MutableLiveData<List<App>?>()



    val categoryApps: MutableLiveData<List<App>?>
        get() = listMutableLiveData

    fun fetchCategoryApps(
        categoryId: String,
        subcategory: SUBCATEGORY,
        shouldIterate: Boolean
    ) {
        if (!shouldIterate) getIterator(categoryId, subcategory)
        val disposable: Disposable = Observable.fromCallable {
                iterator?.let { categoryAppsTask?.getApps(it) }
            }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                        if(it!=null){
                            listMutableLiveData.value = it
                        }
                    }
                )
                { err ->
                    err.printStackTrace()
                    if (err is CredentialsEmptyException || err is InvalidApiException) errorTypeMutableLiveData.setValue(
                        ErrorType.LOGOUT_ERR
                    ) else if (err is AuthException) errorTypeMutableLiveData.setValue(ErrorType.SESSION_EXPIRED) else if (err is UnknownHostException) errorTypeMutableLiveData.setValue(
                        ErrorType.NO_NETWORK
                    ) else
                        errorTypeMutableLiveData.setValue(ErrorType.UNKNOWN)
                }
        compositeDisposable.add(disposable)
    }

    private fun getIterator(categoryId: String, subcategory: SUBCATEGORY) {
        try {
            val categoryAppsIterator = CategoryAppsIterator(api, categoryId, subcategory)
            iterator = CustomAppListIterator(categoryAppsIterator)
            iterator?.setFilterEnabled(true)
            iterator?.setFilterModel(FilterManager.getFilterPreferences(getApplication()))
        } catch (err: Exception) {
            err.printStackTrace()
        }
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    init {
        api = AppSetsApplication.api
        categoryAppsTask = CategoryAppsTask(application)
    }
}