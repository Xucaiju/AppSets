package xcj.appsets.model

import org.apache.commons.lang3.StringUtils

data class Review(private var rating: Int = 0,
                  private var title: String? = null,
                  private var comment: String? = null,
                  private var userName: String? = null,
                  private var userPhotoUrl: String? = null,
                  private var timeStamp: Long = 0)
{
    fun getUserName() = this.userName
    fun setUserName(userName: String?) {
        this.userName = StringUtils.capitalize(userName)
    }

    fun getComment() = this.comment
    fun setComment(comment: String?) {
        this.comment = comment
    }

    fun getTitle() = this.title
    fun setTitle(title: String?) {
        this.title = title
    }

    fun getRating() = this.rating
    fun setRating(rating: Int) {
        this.rating = rating
    }

    fun getTimeStamp() = this.timeStamp
    fun setTimeStamp(timeStamp: Long) {
        this.timeStamp = timeStamp
    }

    fun getUserPhotoUrl() = this.userPhotoUrl
    fun setUserPhotoUrl(userPhotoUrl: String?) {
        this.userPhotoUrl = userPhotoUrl
    }

    override fun toString(): String {
        return "Review(rating=$rating, title=$title, comment=$comment, userName=$userName, userPhotoUrl=$userPhotoUrl, timeStamp=$timeStamp)"
    }

}
