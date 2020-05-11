package xcj.appsets.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import xcj.appsets.AppSetsApplication
import xcj.appsets.model.App
import xcj.appsets.task.AppDetailTask

class AppDetailsViewModel (application: Application):BaseViewModel(application){
    private val listMutableLiveData: MutableLiveData<App?>? = MutableLiveData()

    var appDetails: MutableLiveData<App?>? = null
        get() = listMutableLiveData


    fun fetchAppDetails(packageName: String?) :AppDetailsViewModel{
        api = AppSetsApplication.api
        val disposable: Disposable = Observable.fromCallable {
                api?.let {
                    AppDetailTask(getApplication(), it).getInfo(packageName)
                }
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                listMutableLiveData?.value = it
               }
            ) {
                handleError(it)
            }
        compositeDisposable.add(disposable)
        return this
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

}