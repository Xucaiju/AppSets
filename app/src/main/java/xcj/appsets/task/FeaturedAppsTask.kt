package xcj.appsets.task

import android.content.Context
import android.util.Log
import com.dragons.aurora.playstoreapiv2.CategoryAppsIterator
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI.SUBCATEGORY
import xcj.appsets.iterator.CustomAppListIterator
import xcj.appsets.model.App

class FeaturedAppsTask(context: Context):BaseTask(context) {
    fun getApps(
        api: GooglePlayAPI?,
        categoryId: String?,
        subCategory: SUBCATEGORY?
    ): MutableList<App>? {
        Log.d("FeaturedAppsTask","进入了FeaturedAppsTask")
        val iterator = CustomAppListIterator(CategoryAppsIterator(api, categoryId, subCategory))
        return iterator.next()
    }
}