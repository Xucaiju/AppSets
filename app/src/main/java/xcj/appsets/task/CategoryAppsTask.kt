package xcj.appsets.task

import android.content.Context
import xcj.appsets.iterator.CustomAppListIterator
import xcj.appsets.model.App

class CategoryAppsTask(context: Context?) : BaseTask(context) {
    fun getApps(iterator: CustomAppListIterator): List<App>? {

        return if (!iterator.hasNext()) {
            listOf()
        } else
            getNextBatch(iterator)
    }

    fun getNextBatch(iterator: CustomAppListIterator): List<App>? {
        val apps: List<App>? = iterator.next()
        //if (filterGoogleAppsEnabled(context)) filterGoogleApps(apps) else
        return  apps
    }
}