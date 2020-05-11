package xcj.appsets.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dragons.aurora.playstoreapiv2.SearchSuggestEntry
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import xcj.appsets.AppSetsApplication
import xcj.appsets.task.SuggestionTask
import xcj.appsets.util.Log

class SearchSuggestionViewModel(application: Application) : BaseViewModel(application) {
    private val listMutableLiveData =
        MutableLiveData<List<SearchSuggestEntry>>()

    val suggestions: LiveData<List<SearchSuggestEntry>>
        get() = listMutableLiveData

    fun fetchSuggestions(query: String?) {
        api = AppSetsApplication.api
        var subscribe = Observable.fromCallable {
                api?.let {
                    SuggestionTask(it).getSearchSuggestions(query)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                listMutableLiveData.setValue(
                    it
                )
            }
            .doOnError {
                Log.e(
                    it.message
                )
            }
            .subscribe({

            }) {

            }
        compositeDisposable.add(subscribe)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}