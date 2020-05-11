package xcj.appsets.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.dragons.aurora.playstoreapiv2.AuthException
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI
import io.reactivex.disposables.CompositeDisposable
import xcj.appsets.enums.ErrorType
import xcj.appsets.exception.CredentialsEmptyException
import xcj.appsets.exception.InvalidApiException
import xcj.appsets.exception.TooManyRequestsException
import xcj.appsets.util.Log

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    protected var api: GooglePlayAPI? = null
    protected var compositeDisposable = CompositeDisposable()
    protected var errorTypeMutableLiveData: MutableLiveData<ErrorType> = MutableLiveData<ErrorType>()

    val error: MutableLiveData<ErrorType>
        get() = errorTypeMutableLiveData

    fun handleError(err: Throwable) {
        if (err is NullPointerException) errorTypeMutableLiveData.setValue(ErrorType.NO_API) else if (err is CredentialsEmptyException || err is InvalidApiException) errorTypeMutableLiveData.setValue(
            ErrorType.LOGOUT_ERR
        ) else if (err is AuthException || err is TooManyRequestsException)errorTypeMutableLiveData.setValue(ErrorType.SESSION_EXPIRED) else if (err is java.net.UnknownHostException)errorTypeMutableLiveData.setValue(ErrorType.NO_NETWORK) else errorTypeMutableLiveData.setValue(ErrorType.UNKNOWN)
        Log.d(err.message)
    }
}