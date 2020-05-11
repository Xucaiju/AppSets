package xcj.appsets.extendedclass

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import biz.laenger.android.vpbs.ViewPagerBottomSheetDialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.user_payment_bottom_sheet.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xcj.appsets.R
import xcj.appsets.model.TodayApp
import xcj.appsets.ui.fragment.FragmentToday

class UserPayAppBottomSheetDailogFragment(private  val app:TodayApp?) : ViewPagerBottomSheetDialogFragment(){
    lateinit var appPaymentAppIcon :AppCompatImageView
    lateinit var appPaymentAppName :MaterialTextView
    lateinit var appPaymentAppPrice:MaterialTextView
    lateinit var appPaymentActionConfirm:MaterialButton
    lateinit var alipayRadioButton: RadioButton
    lateinit var wechatpayRadioButton: RadioButton
    lateinit var alipayCardView: MaterialCardView
    lateinit var wechatpayCardView: MaterialCardView
    private var alipayRadioButtonIsCheck = false
    private var wechatpayRadioButtonIsCheck = false
    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(requireContext(), R.layout.user_payment_bottom_sheet, null)
        dialog.setContentView(contentView)
        contentView?.apply {
            appPaymentAppIcon = app_payment_app_icon
            appPaymentAppName = app_payment_app_displayname
            appPaymentAppPrice = app_payment_app_price
            appPaymentActionConfirm = app_payment_action_confirm
            alipayRadioButton = app_payment_alipay_radio
            wechatpayRadioButton = app_payment_wechatpay_radio
            alipayCardView = app_payment_alipay_card
            wechatpayCardView = app_payment_wechat_pay_card
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fillPaymentContent()
    }

    private fun fillPaymentContent(){
/*        Observable.just(
            alipayRadioButton,
            wechatpayRadioButton
        ).subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread()).subscribe{
                if(it.isChecked){
                    appPaymentActionConfirm.isEnabled = true
                }
            }*/
        if(alipayRadioButton.isChecked||wechatpayRadioButton.isChecked){
            appPaymentActionConfirm
        }
        appPaymentActionConfirm?.apply {
            isEnabled = alipayRadioButton.isChecked||wechatpayRadioButton.isChecked
            setOnClickListener {
                CoroutineScope(Default).launch {
                    dummyPay()
                    withContext(Main){
                        Toast.makeText(it.context,"支付完成!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        app?.let {todayApp->
            todayApp.appIcon?.let{
                appPaymentAppIcon?.apply {
                    Glide
                        .with(requireContext())
                        .asBitmap()
                        .load(it)
                        .placeholder(R.color.colorTransparent)
                        .transition(BitmapTransitionOptions().crossFade())
                        .transform(CenterCrop(), RoundedCorners(250))
                        .into(this)
                }
            }
            todayApp.appDisplayname.let {
                appPaymentAppName?.apply {
                    text = it
                }
            }
            todayApp.appPrice?.let {
                appPaymentAppPrice?.apply {
                    val price = FragmentToday.conversionPrice(it.toDouble(), requireContext())
                    text = "$price ${getString(R.string.price_symbol)}"
                }
            }
        }
        alipayCardView.setOnClickListener{
            if(alipayRadioButton.isChecked){//已选择alipay,点击取消勾选
                appPaymentActionConfirm.isEnabled = false

                alipayRadioButton?.apply {
                    isChecked = false
                    isEnabled = false
                }
                wechatpayRadioButton?.apply {
                    isChecked = false
                    isEnabled = false
                }
            }else{//未选择alipay, 点击勾选
                appPaymentActionConfirm.apply {
                    isEnabled = true
                }
                alipayRadioButton?.apply {
                    isChecked = true
                    isEnabled = true
                }
                wechatpayRadioButton?.apply {
                    isChecked = false
                    isEnabled = false
                }
            }

        }
        wechatpayCardView.setOnClickListener {
            if(wechatpayRadioButton.isChecked){//已选择wechat pay,点击取消勾选
                appPaymentActionConfirm.isEnabled = false
                alipayRadioButton?.apply {
                    isChecked = false
                    isEnabled = false
                }
                wechatpayRadioButton?.apply {
                    isChecked = false
                    isEnabled = false
                }
            }else{//未选择wechat pay, 点击勾选
                appPaymentActionConfirm.isEnabled = true
                alipayRadioButton?.apply {
                    isChecked = false
                    isEnabled = false
                }
                wechatpayRadioButton?.apply {

                    isChecked = true
                    isEnabled = true
                }
            }

        }

    }
    private suspend fun dummyPay(){
        withContext(Main){
            appPaymentActionConfirm.text = getString(R.string.purchasing)
        }
        delay(3000)
    }
}