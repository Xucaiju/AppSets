package xcj.appsets.model

import com.dragons.aurora.playstoreapiv2.GooglePlayAPI

object ReviewBuilder {
    fun build(reviewProto: com.dragons.aurora.playstoreapiv2.Review): Review {
        val review = Review()
        review.setComment(reviewProto.comment)
        review.setTitle(reviewProto.title)
        review.setRating(reviewProto.starRating)
        review.setUserName(reviewProto.userProfile.name)
        review.setTimeStamp(reviewProto.timestampMsec)
        for (image in reviewProto.userProfile.imageList) {
            if (image.imageType == GooglePlayAPI.IMAGE_TYPE_APP_ICON) {
                review.setUserPhotoUrl(image.imageUrl)
            }
        }
        return review
    }
}