package xcj.appsets

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Message
import android.text.TextUtils
import android.util.Log
import com.alipay.sdk.app.AuthTask
import com.alipay.sdk.app.PayTask
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_alipay_demo.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import xcj.appsets.javamethod.AlipayAuthResult
import xcj.appsets.ui.BaseActivity
import xcj.appsets.util.OrderInfoUtil

class AlipayDemoActivity : BaseActivity() {
    val msg = Message()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alipay_demo)
        Observable.just(msg).observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe {
            it?.let {
                when(it.what) {
                    SDK_AUTH_FLAG->{
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
                                this,
                                "验证结果",
                                String.format("成功: result[%s]", authResult)
                            )
                        } else {
                            showDialog(
                                this,
                                "验证结果",
                                String.format("失败: result[%s]", authResult)
                            )
                        }
                    }
                    SDK_PAY_FLAG->{

                    }
                }
            }
        }.let {
            disposable.add(it)
        }
        show_alipay_sdk_version.setOnClickListener{
            val payTask = PayTask(this)
            val version = payTask.version
            showDialog(this, "支付宝SDK版本", "version: $version")
        }

        get_alipay_auth_action.setOnClickListener{


            if (TextUtils.isEmpty(PID) || TextUtils.isEmpty(APP_ID) || TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE) || TextUtils.isEmpty(TARGET_ID))
             {

                 showDialog(
                    this,
                    "Error",
                    getString(R.string.error_auth_missing_partner_appid_rsa_private_target_id)
                )
                 return@setOnClickListener
            }


            /*
		 * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
		 * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
		 * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
		 *
		 * authInfo 的获取必须来自服务端；
		 */
            val rsa2: Boolean = RSA2_PRIVATE.isNotEmpty()
            val authInfoMap: Map<String, String> = OrderInfoUtil.buildAuthInfoMap(PID, APP_ID, TARGET_ID, rsa2)
            val info: String = OrderInfoUtil.buildOrderParam(authInfoMap)
            val privateKey: String = if (rsa2) RSA2_PRIVATE else RSA_PRIVATE
            val sign: String = OrderInfoUtil.getSign(authInfoMap, privateKey, rsa2)
            val authInfo = "$info&$sign"

            CoroutineScope(Default).launch {

                // 构造AuthTask 对象
                val authTask = AuthTask(this@AlipayDemoActivity)
                // 调用授权接口，获取授权结果
                // 调用授权接口，获取授权结果
                val result = authTask.authV2(authInfo, true)

                msg.what = SDK_AUTH_FLAG
                msg.obj = result
               // mHandler.sendMessage(msg)
            }


        }
        test_pay_aciton.setOnClickListener{
            if (TextUtils.isEmpty(APP_ID) || TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE)
            ) {
                showDialog(
                    this,
                    "错误",
                    getString(R.string.error_missing_appid_rsa_private)
                )
                return@setOnClickListener
            }
            /*
		 * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
		 * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
		 * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
		 *
		 * orderInfo 的获取必须来自服务端；
		 */
            val rsa2: Boolean = RSA2_PRIVATE.isNotEmpty()
            val params: Map<String, String> = OrderInfoUtil.buildOrderParamMap(APP_ID, rsa2)
            val orderParam: String = OrderInfoUtil.buildOrderParam(params)
            val privateKey: String = if (rsa2) RSA2_PRIVATE else RSA_PRIVATE
            val sign: String = OrderInfoUtil.getSign(params, privateKey, rsa2)
            val orderInfo = "$orderParam&$sign"

            CoroutineScope(Default).launch {

                val alipay = PayTask(this@AlipayDemoActivity)
                val result = alipay.payV2(orderInfo, true)
                Log.i("msp", result.toString())
                msg.what = SDK_PAY_FLAG
                msg.obj = result
            }
        }


    }
    private fun showDialog(context: Context, title:String, message:String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .setOnDismissListener{
                it.dismiss()
            }
            .show()
    }


    companion object {
        val APP_ID = "2016101600700301"
        val PID = ""
        val TARGET_ID = ""
        val RSA2_PRIVATE = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCEu3y8dB5BG3CYsfUWZAhVE4nQwMrX6e3tsL6+2YNnZ9/yGYjoIfGqP0LDq07hJ/VR9UmggorXP4wOdVaYKmtyMKSQXM2tMl2x36gIPsr210UpCCfG7XMXsJjsPrDtLfvh+VW1LuaX4qyjCmZ/HR6rZc/5riL+vgBLEPQ0e9YVt+LJG/ABJwJyTXupnujspMhgTtAec+dNG99oDXaIKuNWD6SAHdUmmRpcFrZX/nHzld1AbGPRMuPLDnbDxVslPjcHwdnxAhPulMApHvk5hCXxzp+9KGzDgtpI5t2soBUJ+6U8bRUroQtPMzKZkToKu9I9W1btaWkjZ8gacAXABRnfAgMBAAECggEAPmnvGKq9gz4E7zhXGAVHycGbP3c4qtjMUcWXnCSp3pkAvFX3g7habAS9P0cxmzxv5e8ihQB9iwPNtwrEAl+IELUA3QVWjceuOTMDT4U/aV0Gn2m/UpJbWVtoPUapaY4C8FqPS9hXBDrJlQ0R71nhDxWb+ztAJ2tutd2uB3Pn0mPtOftqAr+0mAGf/7zYl87PcSoFvRXLsnVsT/Kn38VesLQuxufGdtN+PTovF+VPIbTRKSogsGzvQUiUoCN1rZZjDBLixhQFLRidxQmVUv5f4ZEjknWkMun+bzvsZzrkqYou1tXbH4pC5qLwK3vyOgNx0rPFjOMcRMYRMfrSb12hYQKBgQC5qQJX2U2TB1KGhQfBjEpc0gs+ZLFCxqvNupYMsz8N0TMTyxX+ZhfYxdQEP7k3xDr/FEBt8+JxNyF8Xh2vI+VxGvHWbrAoEUzIFcVdY3yTbLsG2V9f2aUrwqjpF3cDwvOvjUg8V3I+jIrMpcUVzCui511LZQfpavAHQwrN08vw0QKBgQC3BRA3N/4O8yxmw7S7j7VqAKP9l6v9syVGF6PKmx5N3ycNEnKdhPOempoEbQQh8dl+f/JLMMMhOXzVPeNeRnpafmdFTTbjTp3dmG1JBquQXEe7QFVAlEBXoVQYysOBeDf/HNEcmmuaq12zz7GccLDmTpPWLdYUWivLG8vCBLqLrwKBgQCwLa8pie3RLcukFyzq/6O5PSCqTobfZQO3L+4fkyCsje44RClUxbCvZdrxRT8PqkiJscAKFrq00KoYCH+GqTua/wSqhVLoUrJX1ED7g+K9SxqXP0MAA9p3EjuTJU8s5Jy15A2+JQUmUduMo2nIa3ylE4Q2fWLqny2y1m20L92BYQKBgE0EgXVrR6uM46N/OgAwEpzAFkKyX/tMNyRO8GtjaZZFkQ5sM/VbrJWS47t3EOUj/G3Cc/j2VTcXFRQ7jgHvUK/iP50nSDDRtZc5/MtdVjdSzhDbmsSpXoZB6rzgHvPN3mqp+sm5pgyZjr4Laee2eIsCiC36bft0krdPRCiqisIbAoGAVHnUXRdWPGzv3ph2YsmrT1XIA22WdjCpXfUqaRL54Qiis7QXN/dYajq3eiRXS0o/pNCUjASw7vZfJkwY9NrnkSXmHfhHmQkM83S/lw6a3GUkssMlgz9LQqEIgcBFkjclCHD+oY2afc/gdZQaVNlMHSYaLCoC1j8E4ACzQdMhoys="
        val RSA_PRIVATE = ""
        val SDK_PAY_FLAG = 1
        val SDK_AUTH_FLAG = 2
    }
}
