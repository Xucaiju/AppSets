package xcj.appsets.task

import android.content.Context
import android.text.TextUtils
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI
import com.dragons.aurora.playstoreapiv2.ListResponse
import com.google.gson.Gson
import xcj.appsets.Constant
import xcj.appsets.model.CategoryModel
import xcj.appsets.util.PreferenceUtil
import java.util.*

class CategoryListTask(private val context: Context, private val api: GooglePlayAPI) {
    @get:Throws(Exception::class)
    val result: Boolean
        get() {
            api.setLocale(getLocale(context))
            var response = api.categoriesList()
            buildAllCategories(response, Constant.CATEGORY_APPS)
            response = api.categoriesList(Constant.CATEGORY_GAME)
            buildAllCategories(response, Constant.CATEGORY_GAME)
            response = api.categoriesList(Constant.CATEGORY_FAMILY)
            buildAllCategories(response, Constant.CATEGORY_FAMILY)
            return true
        }

    private fun buildAllCategories(
        response: ListResponse,
        categoryPrefId: String
    ) {
        val categoryModels: MutableList<CategoryModel> = ArrayList<CategoryModel>()
        for (categoryCluster in response.getDoc(0).childList) {
            if (categoryCluster.backendDocid != "category_list_cluster") {
                continue
            }
            for (category in categoryCluster.childList) {
                if (!category.hasUnknownCategoryContainer()
                    || !category.unknownCategoryContainer.hasCategoryIdContainer()
                    || !category.unknownCategoryContainer.categoryIdContainer
                        .hasCategoryId()
                ) {
                    continue
                }
                val categoryId =
                    category.unknownCategoryContainer.categoryIdContainer.categoryId
                if (TextUtils.isEmpty(categoryId)) {
                    continue
                }
                val categoryModel = CategoryModel(
                    categoryId,
                    category.title,
                    category.getImage(0).imageUrl
                )
                categoryModels.add(categoryModel)
            }
        }
        val gson = Gson()
        val jsonString = gson.toJson(categoryModels)
        PreferenceUtil.putString(context, categoryPrefId, jsonString)
    }

    private fun getLocale(context: Context): Locale {
        val locale: String? = PreferenceUtil.getString(context, Constant.PREFERENCE_REQUESTED_LANGUAGE)
        return if (TextUtils.isEmpty(locale)) Locale.getDefault() else Locale(
            locale
        )
    }

}