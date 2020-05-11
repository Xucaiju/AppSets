package xcj.appsets.model

class Rating {
    private var average = 0f
    private var stars = IntArray(5)

    fun getStars(starNum: Int) = stars[starNum - 1]

    fun setStars(starNum: Int, ratings: Int) {
        stars[starNum - 1] = ratings
    }

    fun getAverage() = this.average
    fun setAverage(average: Float){
        this.average = average
    }

    override fun toString(): String {
        return "Rating(average=$average, stars=${stars.contentToString()})"
    }

}