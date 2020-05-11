package xcj.appsets.model

import xcj.appsets.Constant

class FilterModel {
    private val systemApps = false
    private val appsWithAds = true
    private val paidApps = true
    private val gsfDependentApps = true
    private val category: String = Constant.TOP
    private val rating = 0.0f
    private val downloads = 0
    fun isPaidApps() = this.paidApps
    fun isSystemApps() = this.systemApps
    fun isAppsWithAds() = this.appsWithAds
    fun isGsfDependentApps() = this.gsfDependentApps
    fun getCategory() = this.category
    fun getRating() = this.rating
    fun getDownloads() = this.downloads
}