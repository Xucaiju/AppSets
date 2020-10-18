package xcj.appsets.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import biz.laenger.android.vpbs.ViewPagerBottomSheetDialogFragment
import com.alipay.sdk.app.PayTask
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.user_payment_bottom_sheet.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xcj.appsets.AlipayDemoActivity
import xcj.appsets.R
import xcj.appsets.javamethod.AlipayAuthResult
import xcj.appsets.model.TodayApp
import xcj.appsets.ui.fragment.UserPayAppBottomSheetDailogFragment.Companion.msg
import xcj.appsets.util.OrderInfoUtil

class UserPayAppBottomSheetDailogFragment() : ViewPagerBottomSheetDialogFragment(){
    companion object{
        val msg = Message()
    }
    val compositeDisposable = CompositeDisposable()
    lateinit var appPaymentAppIcon :AppCompatImageView
    lateinit var appPaymentAppName :MaterialTextView
    lateinit var appPaymentAppPrice:MaterialTextView
    lateinit var appPaymentActionConfirm:MaterialButton
    lateinit var alipayRadioButton: RadioButton
    lateinit var wechatpayRadioButton: RadioButton
    lateinit var alipayCardView: MaterialCardView
    lateinit var wechatpayCardView: MaterialCardView
    private var alipayRadioButtonIsChecked = false
    private var wechatpayRadioButtonIsChecked = false
    lateinit var app:TodayApp
    constructor(todayApp:TodayApp?) : this(){
        if (todayApp != null) {
            app = todayApp
        }
    }

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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("todayApp", app)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            val todayApp = it.get("todayApp") as? TodayApp
            if (todayApp != null) {
                app = todayApp
            }
        }
        Observable.just(msg).observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe {
            it?.let {
                when(it.what) {
                    AlipayDemoActivity.SDK_AUTH_FLAG ->{
                        val authResult = AlipayAuthResult(
                            msg.obj as Map<String?, String?>,
                            true
                        )
                        val resultStatus: String = authResult.resultStatus

                        // 判断resultStatus 为“9000”且result_code
                        // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档

                        // 判断resultStatus 为“9000”且result_code
                        // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                        if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(
                                authResult.resultCode,
                                "200"
                            )
                        ) {
                            // 获取alipay_open_id，调支付时作为参数extern_token 的value
                            // 传入，则支付账户为该授权账户
                            showDialog(
                                this.requireContext(),
                                "验证结果",
                                String.format("成功: result[%s]", authResult)
                            )
                        } else {
                            showDialog(
                                this.requireContext(),
                                "验证结果",
                                String.format("失败: result[%s]", authResult)
                            )
                        }
                    }
                    AlipayDemoActivity.SDK_PAY_FLAG ->{

                    }
                }
            }
        }.let {
            compositeDisposable.add(it)
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
                val payment_method = if(alipayRadioButton.isChecked){
                    "PAYMENT_METHOD_ALIPAY"
                }else if(wechatpayRadioButton.isChecked){
                    "PAYMENT_METHOD_WECHATPAY"
                }else{
                    "PAYMENT_METHOD_OTHER"
                }
                CoroutineScope(Default).launch {
                    dummyPay(payment_method)
                    withContext(Main){
                        appPaymentActionConfirm.text = getString(R.string.payment_completed)
                        Toast.makeText(it.context,getString(R.string.payment_completed), Toast.LENGTH_SHORT).show()
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
                    val price = FragmentRecommend.conversionPrice(it.toDouble(), requireContext())
                    text = String.format("%s %s", price, getString(R.string.price_symbol))//"$price ${getString(R.string.price_symbol)}"
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
                appPaymentActionConfirm?.isEnabled = false
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
    private suspend fun dummyPay(paymentMethod: String) {
        withContext(Main){
            when(paymentMethod){
                "PAYMENT_METHOD_ALIPAY"->{
                    alipayCaller(this@UserPayAppBottomSheetDailogFragment.requireActivity())
                }
                "PAYMENT_METHOD_WECHATPAY"->{

                }
                else->{

                }
            }
            appPaymentActionConfirm.text = getString(R.string.purchasing)
        }
        delay(3000)
    }
}
fun alipayCaller(activity: Activity){
    if (TextUtils.isEmpty(AlipayDemoActivity.APP_ID) || TextUtils.isEmpty(AlipayDemoActivity.RSA2_PRIVATE) && TextUtils.isEmpty(
            AlipayDemoActivity.RSA_PRIVATE
        )
    ) {
        showDialog(
            activity,
            "错误",
            activity.getString(R.string.error_missing_appid_rsa_private)
        )
        return
    }
    /*
 * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
 * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
 * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
 *
 * orderInfo 的获取必须来自服务端；
 */
    val rsa2: Boolean = AlipayDemoActivity.RSA2_PRIVATE.isNotEmpty()
    val params: Map<String, String> = OrderInfoUtil.buildOrderParamMap(AlipayDemoActivity.APP_ID, rsa2)
    val orderParam: String = OrderInfoUtil.buildOrderParam(params)
    val privateKey: String = if (rsa2) AlipayDemoActivity.RSA2_PRIVATE else AlipayDemoActivity.RSA_PRIVATE
    val sign: String = OrderInfoUtil.getSign(params, privateKey, rsa2)
    val orderInfo = "$orderParam&$sign"

    CoroutineScope(Default).launch {

        val alipay = PayTask(activity)
        val result = alipay.payV2(orderInfo, true)
        Log.i("msp", result.toString())
        msg.what = AlipayDemoActivity.SDK_PAY_FLAG
        msg.obj = result
    }
}
fun showDialog(context: Context, title:String, message:String) {
    AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("OK", null)
        .setOnDismissListener{
            it.dismiss()
        }
        .show()
}