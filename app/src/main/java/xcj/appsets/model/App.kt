package xcj.appsets.model

import com.dragons.aurora.playstoreapiv2.Features
import com.dragons.aurora.playstoreapiv2.FileMetadata
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI
import xcj.appsets.R

data class App(
    @Transient
    private var features: Features?=null,
    private var pageBackgroundImage: ImageSource?=null,
    private var screenshotUrls: MutableList<String>? = mutableListOf(),
    @Transient
    private var fileMetadataList: MutableList<FileMetadata>? = mutableListOf(),
    private var offerDetails: MutableMap<String, String>? = mutableMapOf(),
    private var relatedLinks: MutableMap<String, String>? = mutableMapOf(),
    private var rating: Rating? = Rating(),
    private var restriction: Restriction?=null,//限制
    private var userReview: Review?=null,
    private var dependencies: MutableSet<String>? = mutableSetOf(),
    private var permissions: MutableSet<String>? = mutableSetOf(),
    private var categoryIconUrl: String?=null,
    private var categoryId: String?=null,
    private var categoryName: String?=null,
    private var changes: String?=null,
    private var description: String?=null,
    private var developerName: String?=null,
    private var developerEmail: String?=null,
    private var developerAddress: String?=null,
    private var developerWebsite: String?=null,
    private var displayName: String?=null,
    private var downloadString: String?=null,
    private var footerHtml: String?=null,
    private var iconUrl: String?=null,
    private var instantAppLink: String?=null,
    private var labeledRating: String?=null,
    private var packageName: String? = "Unknow",
    private var price: String?=null,
    private var shortDescription: String?=null,
    private var testingProgramEmail: String?=null,
    private var updated: String?=null,
    private var versionName: String?=null,
    private var videoUrl: String?=null,
    private var containsAds: Boolean?=false,
    private var earlyAccess: Boolean?=false,
    private var inPlayStore: Boolean?=false,
    private var isAd: Boolean?=false,
    private var isFree: Boolean?=false,
    private var isInstalled: Boolean?=false,
    private var system: Boolean?=false,
    private var testingProgramAvailable: Boolean?=false,
    private var testingProgramOptedIn: Boolean?=false,
    private var offerType: Int?=-623,
    private var versionCode: Int? = -623,
    private var installs: Long?=-623L,
    private var size: Long?=-623L
    ){
    var appPrice:String? = null
        get() = this.price
    var isSystem:Boolean? = false
        get() = this.system
    fun getIsInstalled() = this.isInstalled
    fun getIsContainsAds() = this.containsAds
    fun getIsFree() = this.isFree
    fun getSize() = this.size
    fun getPackageName() = this.packageName
    fun getPermissions() = this.permissions
    fun setPermissions(permissions: Collection<String>){
        this.permissions = HashSet(permissions)
    }
    fun getInstalledVersionCode() = this.versionCode
    fun getInstalledVersionName() = this.versionName
    fun getDisplayName() = this.displayName
    fun getPageBackgroundImage() = this.pageBackgroundImage
    fun setPackageName(packageName: String?) {
        this.packageName = packageName
    }
    override fun equals(other: Any?): Boolean =
        if(other is App ) other.getPackageName().equals(this.getPackageName())
        else false

    fun setDisplayName(displayName: String) {
        this.displayName = displayName
    }

    fun setVersionName(versionName: String?) {
        this.versionName = versionName
    }

    fun setVersionCode(versionCode: Int) {
        this.versionCode = versionCode
    }

    fun setSystem(system: Boolean) {
        this.system = system
    }
    fun getDescription() = this.description
    fun getIconUrl() = iconUrl
    fun getDependencies() = dependencies
    fun getRating() = rating
    fun getInstalls() = installs
    fun setDescription(descriptionHtml: String?) {

    }

    fun setFooterHtml(footerHtml: String?) {
        this.footerHtml = footerHtml
    }

    fun setFeatures(features: Features?) {
        this.features = features
    }

    fun getCategoryIconUrl() = this.categoryIconUrl
    fun getRelatedLinks() = this.relatedLinks
    fun setUserReview(userReview: Review?) {
        this.userReview = userReview
    }

    fun setShortDescription(descriptionShort: String?) {
        this.shortDescription = descriptionShort
    }

    fun setCategoryId(appCategory: String?) {
        this.categoryId = appCategory
    }

    fun setRestriction(restriction: Restriction) {
        this.restriction = restriction
    }

    fun setOfferType(offerType: Int) {
        this.offerType = offerType
    }

    fun setFree(isfree: Boolean) {
        this.isFree = isfree
    }

    fun setPrice(price: String?) {
        this.price = price
    }

    fun setFileMetadataList(fileList: List<FileMetadata>) {
        this.fileMetadataList = fileList.toMutableList()
    }

    fun setCategoryName(categoryName: String?) {
        this.categoryName = categoryName
    }

    fun setDeveloperName(developerName: String?) {
        this.developerName = developerName
    }

    fun setSize(installSize: Long) {
        this.size = installSize
    }

    fun setInstalls(installsNum: Long) {
        this.installs = installsNum
    }

    fun setDownloadString(downloadString: String?) {
        this.downloadString = downloadString
    }

    fun setUpdated(uploadDate: String?) {
        this.updated = updated
    }

    fun setChanges(recentChangesHtml: String?) {
        this.changes = recentChangesHtml
    }

    fun setContainsAds(containsAds: Boolean) {
        this.containsAds = containsAds
    }

    fun setInPlayStore(inPlayStore: Boolean) {
        this.inPlayStore = inPlayStore
    }

    fun setEarlyAccess(hasEarlyAccessInfo: Boolean) {
        this.earlyAccess = hasEarlyAccessInfo
    }

    fun setTestingProgramAvailable(hasTestingProgramInfo: Boolean) {
        this.testingProgramAvailable = hasTestingProgramInfo
    }

    fun setLabeledRating(label: String?) {
        this.labeledRating = label
    }

    fun setAd(contains: Boolean) {
        this.isAd = contains
    }

    fun setDeveloperEmail(developerEmail: String?) {
        this.developerEmail = developerEmail
    }

    fun setDeveloperAddress(developerAddress: String?) {
        this.developerAddress = developerAddress
    }

    fun setDeveloperWebsite(developerWebsite: String?) {
        this.developerWebsite = developerWebsite
    }

    fun setInstantAppLink(instantLink: String?) {

    }

    fun isTestingProgramAvailable() = this.testingProgramAvailable
    fun setTestingProgramOptedIn(b: Boolean) {
        this.testingProgramOptedIn = b
    }

    fun setTestingProgramEmail(testingProgramEmail: String?) {
        this.testingProgramEmail  = testingProgramEmail
    }

    fun getOfferDetails() = this.offerDetails
    fun setCategoryIconUrl(imageUrl: String?) {
        this.categoryIconUrl = imageUrl
    }

    fun setIconUrl(imageUrl: String?) {
        this.iconUrl = imageUrl
    }

    fun setVideoUrl(videoUrl: String?) {
        this.videoUrl = videoUrl
    }

    fun setPageBackgroundImage(imageSource: ImageSource?) {
        this.pageBackgroundImage = imageSource
    }
    fun setScreenshotUrls(urls:MutableList<String>?){
        this.screenshotUrls = urls
    }
    fun getScreenshotUrls() = this.screenshotUrls
    override fun toString(): String {
        return "App(features=$features, pageBackgroundImage=$pageBackgroundImage, screenshotUrls=$screenshotUrls, fileMetadataList=$fileMetadataList, offerDetails=$offerDetails, relatedLinks=$relatedLinks, rating=$rating, restriction=$restriction, userReview=$userReview, dependencies=$dependencies, permissions=$permissions, categoryIconUrl=$categoryIconUrl, categoryId=$categoryId, categoryName=$categoryName, changes=$changes, description=$description, developerName=$developerName, developerEmail=$developerEmail, developerAddress=$developerAddress, developerWebsite=$developerWebsite, displayName=$displayName, downloadString=$downloadString, footerHtml=$footerHtml, iconUrl=$iconUrl, instantAppLink=$instantAppLink, labeledRating=$labeledRating, packageName=$packageName, price=$price, shortDescription=$shortDescription, testingProgramEmail=$testingProgramEmail, updated=$updated, versionName=$versionName, videoUrl=$videoUrl, containsAds=$containsAds, earlyAccess=$earlyAccess, inPlayStore=$inPlayStore, isAd=$isAd, isFree=$isFree, isInstalled=$isInstalled, system=$system, testingProgramAvailable=$testingProgramAvailable, testingProgramOptedIn=$testingProgramOptedIn, offerType=$offerType, versionCode=$versionCode, installs=$installs, size=$size)"
    }

    fun getDeveloperName() = developerName
    fun getVersionName() = versionName
    fun getVersionCode() = versionCode
    fun getDeveloperWebsite() = developerWebsite
    fun getDeveloperEmail() = developerEmail
    fun getDeveloperAddress() = developerAddress
    fun getChanges() = changes
    fun getShortDescription() = shortDescription
    fun isEarlyAccess() = earlyAccess
    fun isContainsAds() = containsAds
    fun getUpdated() = updated
    fun setInstalled(b: Boolean) {
        this.isInstalled = b
    }

    fun getPrice(): String? = price
    fun getOfferType(): Int? = offerType

    enum class Restriction(private val restriction: Int) {
        GENERIC(-1),
        NOT_RESTRICTED(GooglePlayAPI.AVAILABILITY_NOT_RESTRICTED),
        RESTRICTED_GEO(GooglePlayAPI.AVAILABILITY_RESTRICTED_GEO),
        INCOMPATIBLE_DEVICE(GooglePlayAPI.AVAILABILITY_INCOMPATIBLE_DEVICE_APP);

        val stringResId: Int
            get() = when (restriction) {
                GooglePlayAPI.AVAILABILITY_NOT_RESTRICTED -> 0
                GooglePlayAPI.AVAILABILITY_RESTRICTED_GEO -> R.string.availability_restriction_country
                GooglePlayAPI.AVAILABILITY_INCOMPATIBLE_DEVICE_APP -> R.string.availability_restriction_hardware_app
                else -> R.string.availability_restriction_generic
            }

        companion object {
            fun forInt(restriction: Int): Restriction {
                return when (restriction) {
                    GooglePlayAPI.AVAILABILITY_NOT_RESTRICTED -> NOT_RESTRICTED
                    GooglePlayAPI.AVAILABILITY_RESTRICTED_GEO -> RESTRICTED_GEO
                    GooglePlayAPI.AVAILABILITY_INCOMPATIBLE_DEVICE_APP -> INCOMPATIBLE_DEVICE
                    else -> GENERIC
                }
            }
        }

    }

}
