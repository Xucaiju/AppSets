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
            when {
                account.isNullOrEmpty() -> {
                    signTextInputLayout.error = "请输入正确的账号"
                    signTextInputLayout.isErrorEnabled = true
                }
                password.isNullOrEmpty() -> {
                    signTextInputLayout2.error = "密码不能为空"
                    signTextInputLayout2.isErrorEnabled = true
                    signTextInputLayout.isErrorEnabled = false
                }
                password!=passwordRepeat -> {
                    signTextInputLayout3.error = "密码不一致"
                    signTextInputLayout3.isErrorEnabled = true
                    signTextInputLayout.isErrorEnabled = false
                    signTextInputLayout2.isErrorEnabled = false
                }
                else -> {
                    signTextInputLayout.isErrorEnabled = false
                    signTextInputLayout2.isErrorEnabled = false
                    signTextInputLayout3.isErrorEnabled = false
                    currentUserInfo.account = account
                    currentUserInfo.password = passwordRepeat
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
                        Toast.makeText(this,"注册成功!",Toast.LENGTH_SHORT).show()
                        ContextUtil.runOnUiThread(Runnable { turnBackToLoginActivity(userInfo) })
                    }
                    SignResultCode.FAILED->{
                        Toast.makeText(this,"注册失败!",Toast.LENGTH_SHORT).show()
                        ContextUtil.runOnUiThread(Runnable { resetSign() })
                    }
                    else -> {}
                }
            }){
                Toast.makeText(this,"服务器异常!",Toast.LENGTH_SHORT).show()
            }
        this.disposable.add(disposable)
    }
    private fun resetSign(){
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
