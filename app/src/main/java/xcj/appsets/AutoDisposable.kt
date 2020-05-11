package xcj.appsets

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.apache.commons.lang3.NotImplementedException

class AutoDisposable : LifecycleObserver {
    private var compositeDisposable: CompositeDisposable? = null

    fun bindTo(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
        compositeDisposable = CompositeDisposable()
    }

    fun add(disposable: Disposable?) {

        if (!compositeDisposable?.isDisposed!!) {
            disposable?.let {
                compositeDisposable?.add(it)
            }

        } else {
            throw NotImplementedException("Must bind AutoDisposable to a Lifecycle first")
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        compositeDisposable?.dispose()
    }
}