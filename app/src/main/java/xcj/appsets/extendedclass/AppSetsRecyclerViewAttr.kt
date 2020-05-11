package xcj.appsets.extendedclass

import android.graphics.drawable.Drawable

data class AppSetsRecyclerViewAttr(
    var sectionBackgroundColor: Int,
    var sectionTitleTextColor: Int,
    var sectionSubTitleTextColor: Int,
    var sectionLineColor: Int,
    var sectionCircleColor: Int,
    var sectionStrokeColor: Int,
    var sectionTitleTextSize: Float,
    var sectionSubTitleTextSize: Float,
    var sectionLineWidth: Float,
    var isSticky: Boolean,
    var customDotDrawable: Drawable?
) {
    override fun toString(): String {
        return "AppSetsRecyclerViewAttr(sectionBackgroundColor=$sectionBackgroundColor, sectionTitleTextColor=$sectionTitleTextColor, sectionSubTitleTextColor=$sectionSubTitleTextColor, sectionLineColor=$sectionLineColor, sectionCircleColor=$sectionCircleColor, sectionStrokeColor=$sectionStrokeColor, sectionTitleTextSize=$sectionTitleTextSize, sectionSubTitleTextSize=$sectionSubTitleTextSize, sectionLineWidth=$sectionLineWidth, isSticky=$isSticky, customDotDrawable=$customDotDrawable)"
    }
}