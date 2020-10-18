package xcj.appsets.ui

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_recover_password.*
import xcj.appsets.R
import xcj.appsets.adapter.RecoverPasswordViewPaperAdapter

class RetrievePasswordActivity : BaseActivity() {

    lateinit var viewpager:ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_password)
        viewpager = recoverpassword_activity_viewpager
        val pagerAdapter = RecoverPasswordViewPaperAdapter(this)
        viewpager.adapter = pagerAdapter
        viewpager.isUserInputEnabled = false
    }

    override fun onBackPressed() {

        if(viewpager.currentItem==1){
            viewpager.findViewById<TextInputLayout>(R.id.textinputlayout_captcha_one)?.editText?.text?.clear()
            viewpager.findViewById<TextInputLayout>(R.id.textinputlayout_captcha_two)?.editText?.text?.clear()
            viewpager.findViewById<TextInputLayout>(R.id.textinputlayout_captcha_three)?.editText?.text?.clear()
            viewpager.findViewById<TextInputLayout>(R.id.textinputlayout_captcha_four)?.editText?.text?.clear()
            viewpager.currentItem=0
        }else{
            super.onBackPressed()
        }
    }



    companion object{
        var userGetedCaptcha:String? = null
    }
}
