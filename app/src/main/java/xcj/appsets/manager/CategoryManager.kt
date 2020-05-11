package xcj.appsets.manager

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.commons.lang3.StringUtils
import xcj.appsets.Constant
import xcj.appsets.R
import xcj.appsets.model.CategoryModel
import xcj.appsets.util.PreferenceUtil
import java.util.*

class CategoryManager(private val context: Context) {
    private val gson: Gson = Gson()
    fun getCategoryName(categoryId: String?): String {
        if (null == categoryId) {
            return ""
        }
        return if (categoryId == Constant.TOP) {
            context.getString(R.string.title_all_apps)
        } else StringUtils.EMPTY
    }

    fun fits(appCategoryId: String, chosenCategoryId: String?): Boolean {
        return null == chosenCategoryId || chosenCategoryId == Constant.TOP || appCategoryId == chosenCategoryId
    }

    fun categoryListEmpty(): Boolean? {
        return PreferenceUtil.getString(context, Constant.CATEGORY_APPS)?.isEmpty()
    }

    fun getCategories(categoryId: String?): List<CategoryModel> {
        return getCategoryById(categoryId)
    }

    fun getCategoryById(categoryId: String?): List<CategoryModel> {
        val type = object : TypeToken<List<CategoryModel?>?>() {}.type
        val jsonString: String? = PreferenceUtil.getString(context, categoryId)
        val categoryList: List<CategoryModel>? = gson.fromJson<List<CategoryModel>>(jsonString, type)
        return if (categoryList == null || categoryList.isEmpty()) ArrayList<CategoryModel>() else categoryList
    }

    companion object {
        fun clear(context: Context?) {
            PreferenceUtil.remove(context, Constant.CATEGORY_APPS)
            PreferenceUtil.remove(context, Constant.CATEGORY_GAME)
            PreferenceUtil.remove(context, Constant.CATEGORY_FAMILY)
        }
    }

}