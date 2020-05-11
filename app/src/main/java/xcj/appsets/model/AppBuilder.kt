package xcj.appsets.model

import android.text.TextUtils
import com.dragons.aurora.playstoreapiv2.*
import xcj.appsets.util.parseLong
import java.util.regex.Pattern

class AppBuilder {
    companion object{
        @JvmStatic
        fun build(detailsResponse: DetailsResponse): App {
        val app = build(detailsResponse.docV2)
        app.setFooterHtml(if (detailsResponse.hasFooterHtml()) detailsResponse.footerHtml else "")
        app.setFeatures(detailsResponse.features)
        if (TextUtils.isEmpty(app.getCategoryIconUrl()) || app.getRelatedLinks()!!.isEmpty()) {
            walkBadges(app, detailsResponse.badgeList)
        }
        if (detailsResponse.hasUserReview()) {
            app.setUserReview(ReviewBuilder.build(detailsResponse.userReview))
        }
        return app
    }
    @JvmStatic
    fun build(details: DocV2): App {
        val app = App()

        app.setDisplayName(details.title)

        app.setDescription(details.descriptionHtml)

        app.setShortDescription(details.descriptionShort)

        app.setCategoryId(details.relatedLinks.categoryInfo.appCategory)

        app.setRestriction(App.Restriction.forInt(details.availability.restriction))

        if (details.offerCount > 0) {
            app.setOfferType(details.getOffer(0).offerType)
            app.setFree(details.getOffer(0).micros == 0L)
            app.setPrice(details.getOffer(0).formattedAmount)
        }

        fillOfferDetails(app, details)

        fillAggregateRating(app, details.aggregateRating)

        fillRelatedLinks(app, details)

        val appDetails = details.details.appDetails

        app.setFileMetadataList(appDetails.fileList)

        app.setPackageName(appDetails.packageName)

        app.setVersionName(appDetails.versionString)

        app.setVersionCode(appDetails.versionCode)

        app.setCategoryName(appDetails.categoryName)

        app.setDeveloperName(appDetails.developerName)

        app.setSize(appDetails.installationSize)

        app.setInstalls(getInstallsNum(appDetails.numDownloads))

        app.setDownloadString(appDetails.numDownloads)

        app.setUpdated(appDetails.uploadDate)

        app.setChanges(appDetails.recentChangesHtml)

        app.setPermissions(appDetails.permissionList)

        app.setContainsAds(appDetails.hasContainsAds() && !TextUtils.isEmpty(appDetails.containsAds))

        app.setInPlayStore(true)
        app.setEarlyAccess(appDetails.hasEarlyAccessInfo())

        app.setTestingProgramAvailable(appDetails.hasTestingProgramInfo())

        app.setLabeledRating(details.relatedLinks.rated.label)

        app.setAd(details.detailsUrl.contains("nocache_isad=1"))

        app.setDeveloperName(appDetails.developerName)

        app.setDeveloperEmail(appDetails.developerEmail)

        app.setDeveloperAddress(appDetails.developerAddress)

        app.setDeveloperWebsite(appDetails.developerWebsite)

        if (appDetails.hasInstantLink && !TextUtils.isEmpty(appDetails.instantLink)) {
            app.setInstantAppLink(appDetails.instantLink)
        }

        if (app.isTestingProgramAvailable()!!) {
            app.setTestingProgramOptedIn(appDetails.testingProgramInfo.hasSubscribed() && appDetails.testingProgramInfo.subscribed)
            app.setTestingProgramEmail(appDetails.testingProgramInfo.testingProgramEmail)
        }
        fillImages(app, details.imageList)

        fillDependencies(app, appDetails)

        fillOfferDetails(app, details)

        return app
    }
    @JvmStatic
    private fun getInstallsNum(installsRaw: String): Long {
        val matcher = Pattern.compile("[\\d]+")
            .matcher(installsRaw.replace("[,.\\s]+".toRegex(), ""))
        return if (matcher.find()) {
            parseLong(matcher.group(0), 0)
        } else 0
    }
    @JvmStatic
    private fun fillAggregateRating(app: App, aggregateRating: AggregateRating) {
        val rating = app.getRating()
        rating!!.setAverage(aggregateRating.starRating)
        rating.setStars(1, aggregateRating.oneStarRatings.toInt())
        rating.setStars(2, aggregateRating.twoStarRatings.toInt())
        rating.setStars(3, aggregateRating.threeStarRatings.toInt())
        rating.setStars(4, aggregateRating.fourStarRatings.toInt())
        rating.setStars(5, aggregateRating.fiveStarRatings.toInt())
    }
    @JvmStatic
    private fun fillDependencies(app: App, appDetails: AppDetails) {
        if (!appDetails.hasDependencies() || appDetails.dependencies.dependencyCount == 0) {
            return
        }
        for (dep in appDetails.dependencies.dependencyList) {
            app.getDependencies()!!.add(dep.packageName)
        }
    }
    @JvmStatic
    private fun fillOfferDetails(app: App, details: DocV2) {
        if (!details.hasUnknown25() || details.unknown25.sectionCount == 0) {
            return
        }
        for (item in details.unknown25.sectionList) {
            if (!item.hasContainer()) {
                continue
            }
            app.getOfferDetails()!![item.label] = item.container.description
        }
    }
    @JvmStatic
    private fun fillRelatedLinks(app: App, details: DocV2) {
        if (!details.hasRelatedLinks()) {
            return
        }
        for (link in details.relatedLinks.relatedLinksList) {
            if (!link.hasLabel() || !link.hasUrl1()) {
                continue
            }
            app.getRelatedLinks()!![link.label] = link.url1
        }
    }
    @JvmStatic
    private fun fillImages(
        app: App,
        images: List<Image>
    ) {
        for (image in images) {
            when (image.imageType) {
                GooglePlayAPI.IMAGE_TYPE_CATEGORY_ICON -> app.setCategoryIconUrl(image.imageUrl)
                GooglePlayAPI.IMAGE_TYPE_APP_ICON -> app.setIconUrl(image.imageUrl)
                GooglePlayAPI.IMAGE_TYPE_YOUTUBE_VIDEO_LINK -> app.setVideoUrl(image.imageUrl)
                GooglePlayAPI.IMAGE_TYPE_PLAY_STORE_PAGE_BACKGROUND -> {
                    val imageSource = ImageSource()
                    imageSource.setUrl(image.imageUrl)
                    app.setPageBackgroundImage(imageSource)
                }
                GooglePlayAPI.IMAGE_TYPE_APP_SCREENSHOT -> app.getScreenshotUrls()?.add(image.imageUrl)
            }
        }
    }
    @JvmStatic
    private fun walkBadges(app: App, badges: List<Badge>) {
        for (badge in badges) {
            val link = getLink(badge)
            if (TextUtils.isEmpty(link)) {
                continue
            }
            if (app.getRelatedLinks()!!.isEmpty() && link!!.startsWith("browse")) { // That's similar apps
                app.getRelatedLinks()!![badge.label] = link
            } else if (link!!.startsWith("homeV2?cat=")) { // That's category badge
                app.setCategoryIconUrl(badge.image.imageUrl)
            }
        }
    }
    @JvmStatic
    private fun getLink(badge: Badge?): String? {
        return if (null != badge && badge.hasBadgeContainer1()
            && badge.badgeContainer1.hasBadgeContainer2()
            && badge.badgeContainer1.badgeContainer2.hasBadgeLinkContainer()
        ) {
            badge.badgeContainer1.badgeContainer2.badgeLinkContainer.link
        } else null
    }
    }


}