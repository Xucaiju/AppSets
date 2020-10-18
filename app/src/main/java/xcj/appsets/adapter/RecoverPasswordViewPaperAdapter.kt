package xcj.appsets.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_recover_password_viewpager_item_0.view.*
import kotlinx.android.synthetic.main.activity_recover_password_viewpager_item_1.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import xcj.appsets.R
import xcj.appsets.server.AppSetsServer
import xcj.appsets.ui.AboutActivity
import xcj.appsets.ui.RetrievePasswordActivity

class RecoverPasswordViewPaperAdapter(private val activity: RetrievePasswordActivity) :RecyclerView.Adapter<RecoverPasswordViewPaperAdapter.ViewHolder>() {
    companion object{
        var userAccount = StringUtils.EMPTY
        private var userPassword = StringUtils.EMPTY
    }
    val compositeDisposable =  CompositeDisposable()
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

    }

    override fun getItemViewType(position: Int): Int {
        return if(position==0){
            0
        }else{
            1
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View = if(viewType==0){
            LayoutInflater.from(parent.context).inflate(R.layout.activity_recover_password_viewpager_item_0, parent, false)
        }else{
            LayoutInflater.from(parent.context).inflate(R.layout.activity_recover_password_viewpager_item_1, parent, false)
        }
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = 2

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.itemView?.let {
                if(position==0){
                    it.forget_account_action.setOnClickListener {
                        Toast.makeText(activity, "没关系, 重新注册一个账号就好了!", Toast.LENGTH_SHORT).show()

                    }
                    it.verify_i_am_me_action.setOnClickListener {_->
                        userAccount = it?.retrieve_password_textinputlayout.editText?.text.toString()
                        if(userAccount.isNotEmpty()){
                            Observable.fromCallable { AppSetsServer.getCaptcha(userAccount,activity) }
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({code->
                                    if(code!=null){
                                        activity.viewpager.currentItem=1
                                        if(code=="INFO_CHECK_YOUR_ACCOUNT"){
                                            Toast.makeText(activity, "检查你的账号!",Toast.LENGTH_SHORT).show()
                                        }else{
                                            RetrievePasswordActivity.userGetedCaptcha = code
                                        }
                                    }
                                }) {
                                }?.let {disposable->
                                    compositeDisposable.add(disposable)
                                }
                        }else{
                            Toast.makeText(activity, "输入正确的账号!",Toast.LENGTH_SHORT).show()
                        }
                    }
                    it.chip_about_0.setOnClickListener {
                        activity.startActivity(Intent(activity, AboutActivity::class.java))
                    }
                }
                if(position==1){
                    Observable.just(it.textinputlayout_captcha_one,
                        it.textinputlayout_captcha_two,
                        it.textinputlayout_captcha_three,
                        it.textinputlayout_captcha_four)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .doOnNext {lay->
                            when(lay.id){
                                R.id.textinputlayout_captcha_one->{
                                    if(lay.editText?.text.toString().isNotEmpty()){
                                        it.textinputlayout_captcha_one.clearFocus()
                                        it.textinputlayout_captcha_two.isActivated = true
                                        it.textinputlayout_captcha_two.requestFocus()
                                    }
                                }
                                R.id.textinputlayout_captcha_two->{
                                    if(lay.editText?.text.toString().isNotEmpty()){
                                        it.textinputlayout_captcha_two.clearFocus()
                                        it.textinputlayout_captcha_three.isActivated = true
                                        it.textinputlayout_captcha_three.requestFocus()
                                    }
                                }
                                R.id.textinputlayout_captcha_three->{
                                    if(lay.editText?.text.toString().isNotEmpty()){
                                        it.textinputlayout_captcha_three.clearFocus()
                                        it.textinputlayout_captcha_four.isActivated = true
                                        it.textinputlayout_captcha_four.requestFocus()
                                    }
                                }
                            }

                        }.subscribe()?.let {ob->
                            compositeDisposable.add(ob)
                        }
                        it.verif_captcha_action.setOnClickListener {action->
                            val code1 = it?.textinputlayout_captcha_one.editText?.text.toString()
                            val code2 = it?.textinputlayout_captcha_two.editText?.text.toString()
                            val code3 = it?.textinputlayout_captcha_three.editText?.text.toString()
                            val code4 = it?.textinputlayout_captcha_four.editText?.text.toString()
                            if(code1.isEmpty()||code2.isEmpty()||code3.isEmpty()||code4.isEmpty()){
                                Toast.makeText(activity, "请输入完整的验证码!", Toast.LENGTH_SHORT).show()
                            }else{
                                CoroutineScope(Main).launch {
                                    delay(3000)

                                }
                                val combinedCode = String.format("%s%s%s%s", code1, code2, code3, code4)
                                Observable.fromCallable { AppSetsServer.validateCaptcha(userAccount, activity, combinedCode) }
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({result->
                                        if(result=="INFO_CAPTCHA_ERROR"){
                                            Toast.makeText(activity, "验证码错误!", Toast.LENGTH_SHORT).show()
                                        }else{
                                            Toast.makeText(activity, "你的密码是:$result!", Toast.LENGTH_SHORT).show()
                                        }
                                    }){

                                    }.let {
                                        compositeDisposable.add(it)
                                    }
                            }
                    }
                    it.chip_about_1.setOnClickListener {
                        activity.startActivity(Intent(activity, AboutActivity::class.java))
                    }

                }
            }

    }
}