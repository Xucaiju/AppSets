package xcj.appsets.task

import android.content.Context
import xcj.appsets.exception.InvalidApiException
import xcj.appsets.iterator.CustomAppListIterator
import xcj.appsets.model.App

class SearchTask(private val context: Context) {
    @Throws(Exception::class)
    fun getSearchResults(iterator: CustomAppListIterator?): MutableList<App>? {

        if (iterator == null)
            throw InvalidApiException()
        return if (!iterator.hasNext()) {
            mutableListOf()
        } else
            getNextBatch(iterator)
    }

    private fun getNextBatch(iterator: CustomAppListIterator): MutableList<App>? {

        val apps: MutableList<App>? = mutableListOf()
        if (!iterator.hasNext())
            return apps
        apps?.addAll(iterator.next())
       /* return if (filterGoogleAppsEnabled(context))
            filterGoogleApps(apps)*/

        return apps
    }

/*    private fun filterGoogleApps(apps: MutableList<App>?): MutableList<App>? {
        val gAppsSet: MutableSet<String> =
            HashSet()
        gAppsSet.add("com.chrome.beta")
        gAppsSet.add("com.chrome.canary")
        gAppsSet.add("com.chrome.dev")
        gAppsSet.add("com.android.chrome")
        gAppsSet.add("com.niksoftware.snapseed")
        gAppsSet.add("com.google.toontastic")
        val appList: MutableList<App>? = mutableListOf()
        if (apps != null) {
            for (app in apps) {
                if (!app.getPackageName()?.startsWith("com.google")!! && !gAppsSet.contains(app.getPackageName())) {
                    appList?.add(app)
                }
            }
        }
        return appList
    }*/

}