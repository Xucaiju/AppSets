package xcj.appsets.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.dragons.aurora.playstoreapiv2.AuthException
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI
import com.dragons.aurora.playstoreapiv2.TocResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import xcj.appsets.AppSetsApplication
import xcj.appsets.R
import xcj.appsets.api.PlayStoreApiAuthenticator
import xcj.appsets.events.Event
import xcj.appsets.exception.CredentialsEmptyException
import xcj.appsets.exception.TooManyRequestsException
import xcj.appsets.util.ApiBuilderUtil.Companion.generateApiWithNewAuthToken
import xcj.appsets.util.Log

class ApiValidateService : Service() {
    private val disposable = CompositeDisposable()
    private val isRunning: Boolean
        get() = true

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        buildAndTestApi()
    }

    private fun buildAndTestApi() {
        android.util.Log.d("API 验证服务","构建并测试API")
        disposable.clear()
        disposable.add(
            Observable.fromCallable {
                PlayStoreApiAuthenticator.getPlayApi(this)
            }
                .flatMap{
                        AppSetsApplication.api = it
                        getTocResponse(it)
                    }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    AppSetsApplication.rxNotify(Event(Event.SubType.API_SUCCESS))
                    stopSelf()
                }){
                    Log.d(getString(R.string.toast_api_build_failed))
                    processException(it)
                }
        )
    }

    private fun processException(e: Throwable) {
        if (e is CredentialsEmptyException) {
            AppSetsApplication.rxNotify(Event(Event.SubType.API_ERROR))
        } else if (e is AuthException || e is TooManyRequestsException){
            AppSetsApplication.rxNotify(Event(Event.SubType.API_FAILED))
            newAuthToken
        } else if (e is java.net.UnknownHostException){
            AppSetsApplication.rxNotify(Event(Event.SubType.NETWORK_UNAVAILABLE))
        } else
            Log.e(e.message)
        stopSelf()
    }

    val newAuthToken: Unit
        get() {
            disposable.clear()
            disposable.add(
                Observable.fromCallable {
                    generateApiWithNewAuthToken(this)
                }
                    .flatMap {
                        AppSetsApplication.api = it
                        getTocResponse(it)
                    }
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        AppSetsApplication.rxNotify(Event(Event.SubType.API_SUCCESS))
                        stopSelf()
                    }){
                        Log.e(it.message)
                        AppSetsApplication.rxNotify(Event(Event.SubType.API_ERROR))
                    }
            )
        }

    fun getTocResponse(api: GooglePlayAPI): Observable<TocResponse?> {
        return Observable.create {
            val tocResponse = api.toc()
            it.onNext(tocResponse)
            it.onComplete()
        }
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    companion object {
        var instance: ApiValidateService? = null
        val isServiceRunning: Boolean
            get() = try {
                instance != null && instance?.isRunning!!
            } catch (e: NullPointerException) {
                false
            }
    }
}