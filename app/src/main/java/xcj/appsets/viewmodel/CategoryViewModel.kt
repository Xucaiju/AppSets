package xcj.appsets.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import xcj.appsets.AppSetsApplication
import xcj.appsets.manager.CategoryManager
import xcj.appsets.task.CategoryListTask

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val disposable = CompositeDisposable()
    private val api: GooglePlayAPI?
    private val categoryManager: CategoryManager
    val fetchCompleted = MutableLiveData<Boolean>()

    fun fetchCategories() {
        if (categoryManager.categoryListEmpty()!!)
            categoriesFromAPI
        else
            fetchCompleted.setValue(true)
    }

    private val categoriesFromAPI: Unit
        get() {
            disposable.add(
                Observable.fromCallable {

                      api?.let { CategoryListTask(getApplication(), it).result }

                }
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        fetchCompleted.value = it
                    }) {

                    }
            )
        }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }

    init {
        api = AppSetsApplication.api
        categoryManager = CategoryManager(application)
    }
}