package xcj.appsets.iterator

import com.dragons.aurora.playstoreapiv2.AppListIterator
import xcj.appsets.model.App
import xcj.appsets.model.AppBuilder
import xcj.appsets.model.FilterModel
import xcj.appsets.util.Log


class CustomAppListIterator(private val iterator: AppListIterator) : Iterator<Any>{

    private var filterEnabled = false
    private var filterModel: FilterModel = FilterModel()
    private val relatedTags: MutableList<String> = ArrayList()

    fun getRelatedTags(): List<String> {
        val set: Set<String> =
            HashSet(relatedTags)
        relatedTags.clear()
        relatedTags.addAll(set)
        return relatedTags
    }

    fun setFilterEnabled(filterEnabled: Boolean) {
        this.filterEnabled = filterEnabled
    }

    fun setFilterModel(filterModel: FilterModel) {
        this.filterModel = filterModel
    }

    override fun next(): MutableList<App> {

        val apps: MutableList<App> = arrayListOf()
        for (docV2 in iterator.next()!!) {
            if (docV2.docType == 53)
                relatedTags.add(docV2.title)
            else if (docV2.docType == 1) {
                addApp(apps, AppBuilder.build(docV2))
            } else { /*
                 * This one is either a movie, music or book, will exploit it at some point of time
                 * Movies = 6, Music = 2, Audio Books = 64
                 */
                continue
            }
        }
        return apps
    }

    override fun hasNext(): Boolean {
        return iterator.hasNext()
    }

    private fun shouldSkip(app: App): Boolean {
        return (!filterModel.isPaidApps() && !(app.getIsFree()!!)
                || !filterModel.isAppsWithAds() && app.getIsContainsAds()!!
                || !filterModel.isGsfDependentApps() && app.getDependencies()!!.isNotEmpty()
                || filterModel.getRating() > 0 && app.getRating()!!.getAverage() < filterModel.getRating()
                || filterModel.getDownloads() > 0 && (app.getInstalls()!! < filterModel.getDownloads()))
    }

    private fun addApp(apps: MutableList<App>, app: App) {
        //android.util.Log.d("APPPPPPPPPP", app.toString())
        if (filterEnabled && shouldSkip(app)) {
            Log.i("Filtering out " + app.getPackageName())
        } else {
            apps.add(app)
        }
    }

}