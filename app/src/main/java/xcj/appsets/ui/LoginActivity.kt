package xcj.appsets.ui

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import io.alterac.blurkit.BlurKit
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import xcj.appsets.R
import xcj.appsets.api.PlayStoreApiAuthenticator
import xcj.appsets.model.User
import xcj.appsets.server.AppSetsServer
import xcj.appsets.service.AppSetsFirebaseSerivce
import xcj.appsets.ui.SettingsActivity.Companion.getAllActivitys
import xcj.appsets.util.*
import xcj.appsets.util.AppSetsAccountant.APPSETS_USER_LOGGEDIN_AND_SKIP
import xcj.appsets.worker.SyncDataWithServerWorker


class LoginActivity : BaseActivity() {
    //private var compositeDisposable = CompositeDisposable()
    var beforeWidth: Float = 0.0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        checkPermissions()
        checkNetwork()
        login_button.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                beforeWidth = login_button.width.toFloat()
                login_button.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }

        })

        setUpOnClickListener()

    }

    private fun checkNetwork() {
        if (!NetworkUtil.isConnected(this)) {
            Toast.makeText(this, getString(R.string.error_no_network), Toast.LENGTH_SHORT).show()
            return
        }
    }

    private fun fillTextFiledIfSignUpSuccessful(account: String?, password: String?) {
        if (account != null && password != null) {
            loginActiviyAccount.setText(account)
            loginActiviyPw.setText(password)
        }
    }

    private fun setUpOnClickListener() {
        loginTextInputLayout.editText?.addTextChangedListener {
            login_button.isEnabled = it?.length!! > 0
            if (it.isEmpty())
                loginTextInputLayout.isErrorEnabled = false
        }
        loginTextInputLayout2.editText?.addTextChangedListener {
            if (it?.isEmpty()!!) {
                loginTextInputLayout.isErrorEnabled = false
            }
        }
        login_button.setOnClickListener {
            if (loginTextInputLayout2.editText?.text.toString().isEmpty()) {
                loginTextInputLayout2.error = "输入有效的密码"
                loginTextInputLayout2.isErrorEnabled = true
            } else {
                loginTextInputLayout.isErrorEnabled = false
                val user = User(
                    account = loginTextInputLayout.editText?.text.toString(),
                    password = loginTextInputLayout2.editText?.text.toString()
                )
                disposable.add(Observable.fromCallable { AppSetsServer.signIn(this, user) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe {

                        login_button.text = getString(R.string.action_logging_in)
                        beforeWidth = login_button.width.toFloat()
                        login_button.isEnabled = false
                        progress_bar.visibility = View.VISIBLE
                    }
                    .subscribe({
                        if(it!=null) {
                            when (it) {
                                "FAILED" -> {
                                    Toast.makeText(this, "失败! 请检查账号或密码是否正确", Toast.LENGTH_SHORT)
                                        .show()
                                    ContextUtil.runOnUiThread(Runnable { resetAppSetsLogin() })
                                }
                                else -> {
                                    loginToGooglePlayApiAnonymous()
                                }
                            }
                        }
                    }) {

                    }
                )

            }
        }
        skip_button.setOnClickListener {
            getServerDataOneTime()
            PreferenceUtil.putBoolean(this, APPSETS_USER_LOGGEDIN_AND_SKIP, true)
            //APPSETS_USER_LOGGEDIN_AND_SKIP = true
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        forget_password_action.setOnClickListener {
            startActivity(Intent(this, RecoverPasswordActivity::class.java))
        }
    }

    fun getServerDataOneTime() {
        val getDataAndSaveToNativeDB = OneTimeWorkRequestBuilder<SyncDataWithServerWorker>().build()
        WorkManager.getInstance(this).enqueue(getDataAndSaveToNativeDB)
    }
    override fun onResume() {
        super.onResume()
        val isAppSetsUserLogged = AppSetsAccountant.isAppSetsUserLoggedIn(this)
        val isUserSkipBuildApi = PreferenceUtil.getBoolean(this, APPSETS_USER_LOGGEDIN_AND_SKIP)
        if ((isAppSetsUserLogged!! && isUserSkipBuildApi!!) ||
            (isAppSetsUserLogged!! && Accountant.isLoggedIn(this)!!)
        ) {
            Log.d("已登录到AppSets或Google", "true")
             getAllActivitys(this)?.let {
                Log.d("登陆后 A 大小", "${it.size}")
                if (it.size > 1) {
                    for (activity in it) {
                        if (activity is LoginActivity) {
                            finish()
                        } else {
                            continue
                            /* if(activity!=null){
                                 continue
                             }else{
                                 startActivity(Intent(this, MainActivity::class.java))
                                 supportFinishAfterTransition()
                             }*/
                        }
                    }
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                    supportFinishAfterTransition()
                    it[0].finish()
                }

            }
        }else{
            Log.d("已登录到AppSets或Google", "false")
        }

    }

    override fun onDestroy() {
        disposable.clear()
        disposable.dispose()
        super.onDestroy()
    }

    private fun loginToGooglePlayApiAnonymous() {
        disposable.add(
            Observable.fromCallable { PlayStoreApiAuthenticator.login(this) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    login_button.text = getText(R.string.buildingApi)
                    val currentWidth = login_button.measuredWidth.toFloat()

                    val scalingRatio = (beforeWidth / currentWidth)

                    val pvhX = PropertyValuesHolder.ofFloat("scaleX", scalingRatio, 1f)
                    ObjectAnimator.ofPropertyValuesHolder(login_button, pvhX).apply {
                        duration = 450

                        start()
                    }
                    login_button.isEnabled = false
                    progress_bar.visibility = View.VISIBLE
                }
                .subscribe({
                    if (it != null) {
                        getServerDataOneTime()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        startActivity(intent, ViewUtil.getEmptyActivityBundle(this))
                        supportFinishAfterTransition()
                        //start FirebaseMessageService
                        val firebaseSerivceIntent = Intent(this, AppSetsFirebaseSerivce::class.java)
                        startService(firebaseSerivceIntent)


                    } else {
                        Toast.makeText(this, "构建 GooglePlay API 失败!", Toast.LENGTH_LONG).show()
                        ContextUtil.runOnUiThread(Runnable { resetAnonymousLogin() })
                    }
                }) {
                    Toast.makeText(this, "构建 GooglePlay API 失败!", Toast.LENGTH_LONG).show()
                    ContextUtil.runOnUiThread(Runnable { resetAnonymousLogin() })
                })


    }

    private fun resetAnonymousLogin() {
        login_button.isEnabled = true
        login_button.text = getText(R.string.signin_text)
        progress_bar.visibility = View.INVISIBLE
        skip_button.visibility = View.VISIBLE
    }

    private fun resetAppSetsLogin() {
        login_button.isEnabled = true
        login_button.text = getText(R.string.signin_text)
        progress_bar.visibility = View.INVISIBLE
    }

    private fun checkPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            12248
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            12248 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this, "已获取存储权限", Toast.LENGTH_SHORT).show()
                    loginTextInputLayout.isEnabled = true
                    loginTextInputLayout2.isEnabled = true
                } else {
                    BlurKit.getInstance().blur(materialTextView as View, 12)
                    loginTextInputLayout.isEnabled = false
                    loginTextInputLayout2.isEnabled = false
                    showSnackBar(login_activity_coordinator,
                        R.string.grant_storage_permissions,
                        LENGTH_INDEFINITE,
                        View.OnClickListener {
                            checkPermissions()
                        }
                    )
                }
            }
        }
    }

    fun toSignup(view: View) {
        val intent = Intent(this, SignupActivity::class.java)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                if (requestCode == 2) {
                    loginActiviyAccount.text?.clear()
                    loginActiviyPw.text?.clear()
                    val account = data?.getStringExtra("account")
                    val password = data?.getStringExtra("password")
                    fillTextFiledIfSignUpSuccessful(account, password)
                }
            }
        }
    }

    fun toAbout(view: View) {
        startActivity(Intent(this, AboutActivity::class.java))
    }

    override fun onBackPressed() {
        getAllActivitys(this)?.forEach {
            it.finish()
        }
        if (AppSetsAccountant.isAppSetsUserLoggedIn(this)!! && !PreferenceUtil.getBoolean(this, APPSETS_USER_LOGGEDIN_AND_SKIP)!!) {
            getSharedPreferences(this).edit {
                clear()
                apply()
            }
        }
        super.onBackPressed()
    }

}
