package xcj.appsets.download

import android.content.Context
import com.dragons.aurora.playstoreapiv2.AndroidAppDeliveryData
import com.dragons.aurora.playstoreapiv2.SplitDeliveryData
import com.tonyodev.fetch2.EnqueueAction
import com.tonyodev.fetch2.NetworkType
import com.tonyodev.fetch2.Priority
import com.tonyodev.fetch2.Request
import xcj.appsets.model.App
import xcj.appsets.util.PathUtil
import xcj.appsets.util.TextUtil
import java.util.*

object RequestBuilder {
    /*
     *
     * Build Simple App Download Request from URL
     * @param Context - Application Context
     * @param App -  App object
     * @param Url -  APK Url
     * @return Request
     *
     */
    fun buildRequest(context: Context?, app: App, Url: String?): Request? {
        val request: Request? = Request(Url?:"", context?.let { PathUtil.getLocalApkPath(it, app) } ?:"")?.apply {
            priority = Priority.HIGH
            enqueueAction = EnqueueAction.UPDATE_ACCORDINGLY
            groupId = app.getPackageName().hashCode()
            networkType = NetworkType.ALL
            tag = app.getPackageName()
        }
       /* request.priority= Priority.HIGH
        request.enqueueAction = EnqueueAction.UPDATE_ACCORDINGLY
        request.groupId = app.getPackageName().hashCode()

        request.networkType = NetworkType.ALL

        request.tag = app.getPackageName()*/
        return request
    }

    /*
     *
     * Build Bundled App Download Request from URL
     * @param Context - Application Context
     * @param App -  App object
     * @param Url -  APK Url
     * @return Request
     *
     */
    fun buildSplitRequest(
        context: Context?,
        app: App,
        split: SplitDeliveryData
    ): Request? {
        val request = context?.let {
            PathUtil.getLocalSplitPath(it, app, split.name)?.let { path->
                Request(
                    split.downloadUrl,
                    path
                )
            }
        }?.apply {
            priority = Priority.HIGH
            enqueueAction = EnqueueAction.UPDATE_ACCORDINGLY
            groupId = app.getPackageName().hashCode()
            networkType = NetworkType.ALL
            tag = app.getPackageName()
        }
     /*   request.setPriority(Priority.HIGH)
        request.setEnqueueAction(EnqueueAction.UPDATE_ACCORDINGLY)
        request.setGroupId(app.getPackageName().hashCode())*/
       /* if (Util.isDownloadWifiOnly(context)) request.setNetworkType(NetworkType.WIFI_ONLY) else request.setNetworkType(*/
      /*  request?.networkType = NetworkType.ALL

        request?.tag = app.getPackageName()*/
        return request
    }

    /*
     *
     * Build Bundled App Download RequestList from SplitList
     * @param Context - Application Context
     * @param App -  App object
     * @param AndroidAppDeliveryData -  DeliveryData Objects
     * @return RequestList
     *
     */
    fun buildSplitRequestList(
        context: Context?, app: App,
        deliveryData: AndroidAppDeliveryData
    ): List<Request> {
        val splitList =
            deliveryData.splitDeliveryDataList
        val requestList: MutableList<Request> = ArrayList<Request>()
        for (split in splitList) {
            val request: Request? = buildSplitRequest(context, app, split)
            request?.let {
                requestList.add(request)
            }
        }
        return requestList
    }

    /*
     *
     * Build Obb Download Request from URL
     * @param Context - Application Context
     * @param App -  App object
     * @param Url -  APK Url
     * @param isMain - boolean to determine obb type
     * @return Request
     *
     */
    fun buildObbRequest(
        context: Context?,
        app: App,
        Url: String?,
        isMain: Boolean,
        isGZipped: Boolean
    ): Request? {
        val request: Request?= Url?.let {url-> PathUtil.getObbPath(app, isMain, isGZipped)?.let {path-> Request(url, path) } }
            ?.apply {
                priority = Priority.HIGH
                enqueueAction = EnqueueAction.UPDATE_ACCORDINGLY
                groupId = app.getPackageName().hashCode()
                networkType = NetworkType.ALL
                tag = app.getPackageName()
            }

       /* request.setEnqueueAction(EnqueueAction.UPDATE_ACCORDINGLY)
        request.setPriority(Priority.HIGH)
        request.setGroupId(app.getPackageName().hashCode())
        if (Util.isDownloadWifiOnly(context)) request.setNetworkType(NetworkType.WIFI_ONLY) else request.setNetworkType(
            NetworkType.ALL
        )
        request.setTag(app.getPackageName())*/
        return request
    }

    /*
     *
     * Build Obb App Download RequestList from DeliveryDataList and GroupId
     * @param Context - Application Context
     * @param AndroidAppDeliveryData -  App object
     * @param groupId - Request GroupId
     * @return RequestList
     *
     */
    fun buildObbRequestList(
        context: Context?,
        app: App,
        appDeliveryData: AndroidAppDeliveryData
    ): List<Request> {
        val requestList: MutableList<Request> = ArrayList<Request>()
        val appFileMetadataList =
            appDeliveryData.additionalFileList
        if (appFileMetadataList.size == 1) {
            val obbFileMetadata = appDeliveryData.getAdditionalFile(0)
            if (TextUtil.isEmpty(obbFileMetadata.downloadUrlGzipped)) buildObbRequest(
                context,
                app,
                obbFileMetadata.downloadUrl,
                isMain = true,
                isGZipped = false
            )?.let {
                requestList.add(
                    it
                )
            } else buildObbRequest(
                context,
                app,
                obbFileMetadata.downloadUrlGzipped,
                isMain = true,
                isGZipped = true
            )?.let {
                requestList.add(
                    it
                )
            }
        }
        if (appFileMetadataList.size == 2) {
            val obbFileMetadata = appDeliveryData.getAdditionalFile(1)
            if (TextUtil.isEmpty(obbFileMetadata.downloadUrlGzipped)) buildObbRequest(
                context,
                app,
                obbFileMetadata.downloadUrl,
                isMain = false,
                isGZipped = false
            )?.let {
                requestList.add(
                    it
                )
            } else buildObbRequest(
                context,
                app,
                obbFileMetadata.downloadUrlGzipped,
                isMain = false,
                isGZipped = true
            )?.let {
                requestList.add(
                    it
                )
            }
        }
        return requestList
    }
}