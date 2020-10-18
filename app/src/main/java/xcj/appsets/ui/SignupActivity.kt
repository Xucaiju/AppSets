package xcj.appsets.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_signup.*
import xcj.appsets.R
import xcj.appsets.enums.SignResultCode
import xcj.appsets.model.User
import xcj.appsets.server.AppSetsServer
import xcj.appsets.util.ContextUtil

class SignupActivity : BaseActivity() {
   // private val compositeDisposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        val currentUserInfo = User()
        signup_button.setOnClickListener {
            val account:String? = signTextInputLayout.editText?.text.toString()
            val password:String? = signTextInputLayout2.editText?.text.toString()
            val passwordRepeat = signTextInputLayout3.editText?.text.toString()
            val userEmail = signTextInputLayout4.editText?.text.toString()
            when {
                account.isNullOrEmpty() -> {
                    signTextInputLayout.error = getString(R.string.please_input_current_account)//"请输入正确的账号"
                    //signTextInputLayout.isErrorEnabled = true
                }
                password.isNullOrEmpty() -> {
                    signTextInputLayout2.error = getString(R.string.the_password_cant_empty)//"密码不能为空"
                    //signTextInputLayout2.isErrorEnabled = true
                    signTextInputLayout.error = null
                }
                password!=passwordRepeat -> {
                    signTextInputLayout3.error = getString(R.string.passwords_are_inconsistent)//"密码不一致"
                   // signTextInputLayout3.isErrorEnabled = true
                    signTextInputLayout.error = null
                    signTextInputLayout2.error = null
                }
                userEmail.isNullOrEmpty()->{
                    signTextInputLayout4.error = getString(R.string.please_input_your_email)//"请输入邮箱"
                   // signTextInputLayout4.isErrorEnabled = true
                    signTextInputLayout3.error = null
                    signTextInputLayout2.error = null
                    signTextInputLayout.error = null
                }
                else -> {
                    signTextInputLayout.error = null
                    signTextInputLayout2.error = null
                    signTextInputLayout3.error = null
                    currentUserInfo.account = account
                    currentUserInfo.password = passwordRepeat
                    currentUserInfo.email = userEmail
                    Log.d("User", currentUserInfo.toString())
                    signUp(currentUserInfo)
                }
            }
        }

    }
    private fun signUp(userInfo:User){
        val disposable: Disposable = Observable.fromCallable {
                AppSetsServer.signUp(this,userInfo)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                signup_button.text=getText(R.string.action_registering)
                signup_button.isEnabled = false
            }.subscribe({
                when(it){
                    SignResultCode.SUCCESSFUl->{
                        Toast.makeText(this,getString(R.string.registration_success),Toast.LENGTH_SHORT).show()
                        ContextUtil.runOnUiThread(Runnable { turnBackToLoginActivity(userInfo) })
                    }
                    SignResultCode.FAILED->{
                        Toast.makeText(this,getString(R.string.registration_failed),Toast.LENGTH_SHORT).show()
                        ContextUtil.runOnUiThread(Runnable { resetSignup() })
                    }
                    else -> {}
                }
            }){
                Toast.makeText(this,getString(R.string.server_exception),Toast.LENGTH_SHORT).show()
            }
        this.disposable.add(disposable)
    }
    private fun resetSignup(){
        signup_button.text=getText(R.string.signup_text)
        signup_button.isEnabled = true
    }
    private fun turnBackToLoginActivity(userInfo: User){
        val intent = Intent(this,LoginActivity::class.java)
        intent.putExtra("account",userInfo.account)
        intent.putExtra("password",userInfo.password)
        setResult(2,intent)
        this.finish()
    }

    override fun onDestroy() {
        this.disposable.dispose()
        super.onDestroy()
    }

    fun toAbout(view: View) {
        startActivity(Intent(view.context, AboutActivity::class.java))
    }

}
