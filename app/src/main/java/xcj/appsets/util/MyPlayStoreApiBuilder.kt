package xcj.appsets.util

import com.dragons.aurora.playstoreapiv2.*
import xcj.appsets.adapter.OkHttpClientAdapter
import java.io.IOException
import java.util.*

class MyPlayStoreApiBuilder:PlayStoreApiBuilder() {
     var memail: String? = null
     var maasToken: String? = null
     var mauthToken: String? = null
     var mgsfId: String? = null
     var mlocale: Locale? = null
     var mtokenDispenserUrl: String? = null
     var mdeviceCheckinConsistencyToken: String? = null
     var mdeviceConfigToken: String? = null
     var mdfeCookie: String? = null

    @Transient
    var mdeviceInfoProvider: DeviceInfoProvider? = null
    @Transient
    var mhttpClient: OkHttpClientAdapter? = null
    @Transient
    var mtokenDispenserClient: TokenDispenserClient? = null

    override fun build(): GooglePlayAPI? {
        var buildUpon1:GooglePlayAPI?=null
        try {
            buildUpon1 = buildUpon(GooglePlayAPI())

        }catch (e:IOException){
            e.printStackTrace()
        }catch (e:ApiBuilderException){
            e.printStackTrace()
        }
        return buildUpon1
    }

    @Throws(IOException::class, ApiBuilderException::class)
    private fun buildUpon(api: GooglePlayAPI): GooglePlayAPI {
        if (null == mhttpClient) {
            throw ApiBuilderException("HttpClientAdapter is required")
        }
        if (null == mdeviceInfoProvider) {
            throw ApiBuilderException("DeviceInfoProvider is required")
        }
        api.setLocale(if (null == mlocale) Locale.getDefault() else mlocale)
        api.client = mhttpClient
        api.setDeviceInfoProvider(mdeviceInfoProvider)
        return if (isEmpty(maasToken) && isEmpty(mauthToken) && isEmpty(mtokenDispenserUrl)) {
            throw ApiBuilderException("Email-aasToken pair, a authToken or a authToken dispenser url is required")
        } else {
            if (!isEmpty(mtokenDispenserUrl)) {
                mtokenDispenserClient = TokenDispenserClient(mtokenDispenserUrl!!, mhttpClient!!)
            }else{
            }
            if ((isEmpty(mauthToken) || isEmpty(mgsfId)) && isEmpty(memail) && null != mtokenDispenserClient) {
                memail = mtokenDispenserClient!!.randomEmail


                if (isEmpty(memail)) {
                    throw ApiBuilderException("Could not get email from authToken dispenser")
                }
            }
            if (isEmpty(memail) && (isEmpty(mauthToken) || isEmpty(mgsfId))) {
                throw ApiBuilderException("Email is required")
            } else {
                var needToUploadDeviceConfig = false
                if (isEmpty(mgsfId)) {
                    mgsfId = generateGsfId(api)
                    needToUploadDeviceConfig = true
                }
                api.gsfId = mgsfId
                if (isEmpty(mauthToken)) {
                    mauthToken = generateToken(api)
                }
                api.token = mauthToken
                if (needToUploadDeviceConfig) {
                    api.uploadDeviceConfig()
                }
                if (isEmpty(api.deviceCheckinConsistencyToken)) {
                    api.deviceCheckinConsistencyToken = mdeviceCheckinConsistencyToken
                }
                if (isEmpty(api.deviceConfigToken)) {
                    api.deviceConfigToken = mdeviceConfigToken
                }
                if (isEmpty(api.dfeCookie)) {
                    api.dfeCookie = mdfeCookie
                }
                api
            }
        }
    }

    @Throws(IOException::class)
    private fun generateGsfId(api: GooglePlayAPI): String {
        return api.generateGsfId()
    }

    @Throws(IOException::class)
    private fun generateToken(api: GooglePlayAPI): String {
        return if (isEmpty(maasToken)) mtokenDispenserClient!!.getToken(memail) else api.generateToken(
            memail,
            maasToken
        )
    }

    private fun isEmpty(value: String?): Boolean {
        return null == value || value.isEmpty()
    }

    override fun toString(): String {
        return "MyPlayStoreApiBuilder(memail=$memail, maasToken=$maasToken, mauthToken=$mauthToken, mgsfId=$mgsfId, mlocale=$mlocale, mtokenDispenserUrl=$mtokenDispenserUrl, mdeviceCheckinConsistencyToken=$mdeviceCheckinConsistencyToken, mdeviceConfigToken=$mdeviceConfigToken, mdfeCookie=$mdfeCookie, mdeviceInfoProvider=$mdeviceInfoProvider, mhttpClient=$mhttpClient, mtokenDispenserClient=$mtokenDispenserClient)"
    }

}