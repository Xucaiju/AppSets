package xcj.appsets.manager

import android.content.Context
import com.google.gson.Gson
import xcj.appsets.Constant
import xcj.appsets.model.FilterModel
import xcj.appsets.util.PreferenceUtil

object FilterManager {
    fun getFilterPreferences(context: Context?): FilterModel {
        val gson = Gson()
        val filterModel: FilterModel = gson.fromJson(
            context?.let { PreferenceUtil.getString(it, Constant.PREFERENCE_FILTER_APPS) }, FilterModel::class.java
        )
        return if (filterModel == null) {
            val defaultModel = FilterModel()
            context?.let{PreferenceUtil.putString(it, Constant.PREFERENCE_FILTER_APPS, gson.toJson(defaultModel))}
            defaultModel
        } else filterModel
    }

    fun saveFilterPreferences(
        context: Context?,
        filterModel: FilterModel?
    ) {
        val gson = Gson()
        val filterJSON = gson.toJson(filterModel)
        context?.let{PreferenceUtil.putString(it, Constant.PREFERENCE_FILTER_APPS, filterJSON)}
    }
}