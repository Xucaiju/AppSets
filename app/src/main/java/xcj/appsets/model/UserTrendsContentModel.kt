package xcj.appsets.model

import java.sql.Date

data class UserTrendsContentModel (var id:Int? = 0,
                                   var trendsPicA:String = "",
                                   var trendsPicB:String = "",
                                   var trendsPicC:String = "",
                                   var trendsPicD:String = "",
                                   var trendsPicE:String = "",
                                   var trendsPicF:String = "",
                                   var trendsPicG:String = "",
                                   var trendsPicH:String = "",
                                   var trendsPicI:String = "",
                                   var trendsTextContent:String = "",
                                   var trendsLiked:Int=0,
                                   var trendsIsuuingTime:Date? = null,
                                   var userAccount:String?=null){
    override fun toString(): String {
        return "UserTrendsContentModel(id=$id, trendsPicA='$trendsPicA', trendsPicB='$trendsPicB', trendsPicC='$trendsPicC', trendsPicD='$trendsPicD', trendsPicE='$trendsPicE', trendsPicF='$trendsPicF', trendsPicG='$trendsPicG', trendsPicH='$trendsPicH', trendsPicI='$trendsPicI', trendsTextContent='$trendsTextContent', trendsLiked=$trendsLiked, trendsIsuuingTime=$trendsIsuuingTime, userAccount=$userAccount)"
    }
}