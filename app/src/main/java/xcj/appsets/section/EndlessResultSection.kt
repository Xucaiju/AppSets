package xcj.appsets.section

import android.content.Context
import org.apache.commons.lang3.StringUtils
import xcj.appsets.R
import xcj.appsets.model.App
import xcj.appsets.util.PackageUtil
import xcj.appsets.util.humanReadableByteValue

class EndlessResultSection(context: Context?, clickListener: ClickListener?) : InstallAppSection(context, clickListener) {
    fun add(app: App?) {
        if (app != null) {
            appList?.add(app)
        }
    }

    val count: Int?
        get() = appList?.size

    fun purgeData() {
        appList!!.clear()
    }

    override fun getDetails(
        Version: MutableList<String?>,
        Extra: MutableList<String?>,
        app: App?
    ) {
        Version.add(app?.getSize()?.let {humanReadableByteValue(it, true)})
        if (!app?.isEarlyAccess()!!)
            Version.add(
            context?.getString(
                R.string.details_rating_number,
                app?.getRating()?.getAverage()
            )
        )
        if (PackageUtil.isInstalled(context!!, app?.getPackageName()))
            Version.add(context?.getString(R.string.action_installed))
        Extra.add(app?.getPrice())
        Extra.add(context?.getString(if (app?.isContainsAds()!!) R.string.list_app_has_ads else R.string.list_app_no_ads))
        Extra.add(
            context?.getString(
                if (app?.getDependencies()?.isEmpty()!!)
                    R.string.list_app_independent_from_gsf
                else
                    R.string.list_app_depends_on_gsf
            )
        )
        if (!StringUtils.isEmpty(app?.getUpdated())) Extra.add(app?.getUpdated())
    }
}