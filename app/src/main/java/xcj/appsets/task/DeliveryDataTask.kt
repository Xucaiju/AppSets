package xcj.appsets.task

import android.content.Context
import com.dragons.aurora.playstoreapiv2.AndroidAppDeliveryData
import com.dragons.aurora.playstoreapiv2.DeliveryResponse
import xcj.appsets.Constant
import xcj.appsets.exception.AppNotFoundException
import xcj.appsets.exception.NotPurchasedException
import xcj.appsets.model.App
import xcj.appsets.util.Log
import xcj.appsets.util.PreferenceUtil
import java.io.IOException

class DeliveryDataTask(context: Context?) : BaseTask(context) {
    var deliveryData: AndroidAppDeliveryData? = null
    private var downloadToken: String? = null

    @Throws(Exception::class)
    fun getDeliveryData(app: App): AndroidAppDeliveryData? {
        api = getGooglePlayApi()
        purchase(app)
        delivery(app)
        return deliveryData
    }

    fun purchase(app: App) {
        try {
            val buyResponse = api?.purchase(app.getPackageName(), app.getVersionCode()?:0, app.getOfferType()?:0)
            if (buyResponse?.hasPurchaseStatusResponse()!!
                && buyResponse?.purchaseStatusResponse?.hasAppDeliveryData()!!
                && buyResponse?.purchaseStatusResponse.appDeliveryData
                    .hasDownloadUrl()
            ) {
                deliveryData = buyResponse.purchaseStatusResponse.appDeliveryData
            }
            if (buyResponse.hasDownloadToken()) {
                downloadToken = buyResponse.downloadToken
            }
        } catch (e: Exception) {
            Log.d("Failed to purchase %s", app.getDisplayName())
        }
    }

    @Throws(IOException::class)
    fun delivery(app: App) {
        val deliveryResponse: DeliveryResponse? =
            api?.delivery(
                app.getPackageName(),
                (if (shouldDownloadDelta(app))
                    app.getInstalledVersionCode()?:0
                else
                    0),
                app.getVersionCode()?:0,
                app.getOfferType()?:0,
                downloadToken
            )

        if (deliveryResponse?.hasAppDeliveryData()!! && deliveryResponse.appDeliveryData.hasDownloadUrl()) {
            deliveryData = deliveryResponse.appDeliveryData
        } else if (deliveryData == null && deliveryResponse.hasStatus()) {
            handleError(app, deliveryResponse.status)
        }
    }

    @Throws(IOException::class)
    private fun handleError(app: App, statusCode: Int) {
        when (statusCode) {
            2 -> throw AppNotFoundException(app.getDisplayName(), statusCode)
            3 -> throw NotPurchasedException(app.getDisplayName(), statusCode)
        }
    }

    private fun shouldDownloadDelta(app: App): Boolean {
        return (context?.let { PreferenceUtil.getBoolean(it, Constant.PREFERENCE_DOWNLOAD_DELTAS) }!!
                && app.getInstalledVersionCode()!! < app.getVersionCode()!!)
    }
}