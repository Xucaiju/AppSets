package xcj.appsets.task

import android.content.Context
import com.dragons.aurora.playstoreapiv2.DetailsResponse
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI
import xcj.appsets.model.App
import xcj.appsets.model.AppBuilder
import xcj.appsets.util.PackageUtil

class AppDetailTask(private val context: Context, private val api: GooglePlayAPI) {
    @Throws(Exception::class)
    fun getInfo(packageName: String?): App? {
        val response:DetailsResponse? = api.details(packageName)
        val app: App? = response?.let { AppBuilder.build(it) }
        if (app?.let { PackageUtil.isInstalled(context, it) }!!)
            app?.setInstalled(true)
        return app
    }

}