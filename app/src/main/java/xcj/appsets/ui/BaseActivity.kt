package xcj.appsets.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.CompositeDisposable
import xcj.appsets.AppSetsApplication
import xcj.appsets.R
import xcj.appsets.events.Event
import xcj.appsets.livedata.ConnectionLiveData
import xcj.appsets.util.ThemeUtil

abstract class BaseActivity:AppCompatActivity(){
    var disposable = CompositeDisposable()
    var themeUtil = ThemeUtil()
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        themeUtil.onCreate(this)
        ConnectionLiveData(this).observe(this, Observer {
            if(it.isConnected){
                AppSetsApplication.rxNotify(Event(Event.SubType.NETWORK_AVAILABLE))
            }else{
                AppSetsApplication.rxNotify(Event(Event.SubType.NETWORK_UNAVAILABLE))

            }
        })
    }

    override fun onResume() {
        super.onResume()
        themeUtil.onResume(this)
    }
    protected open fun showSnackBar(
        view: View?, @StringRes message: Int,
        duration: Int,
        onClickListener: View.OnClickListener?
    ) {
        val snackbar = Snackbar.make(view!!, message, duration)
        snackbar.setAction(R.string.action_retry, onClickListener)
        snackbar.show()
    }
    protected open fun showSnackBar(
        view: View?, @StringRes message: Int,
        onClickListener: View.OnClickListener?
    ) {
        showSnackBar(view, message, 0, onClickListener)
    }
}