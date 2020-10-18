package xcj.appsets.model

import xcj.appsets.Constant

data class FilterModel(
    var systemApps:Boolean = false,
    var appsWithAds:Boolean = true,
    var paidApps:Boolean = true,
    var gsfDependentApps:Boolean = true,
    var category: String = Constant.TOP,
    var rating:Float = 0.0f,
    var downloads:Int = 0
)