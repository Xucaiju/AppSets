package xcj.appsets.model

import java.sql.Timestamp

class UserTrends {
        var id:Int?=0
        var trendsPicA:String? = null
            set(trendsPicA) {
                field = trendsPicA?.trim { it <= ' ' }
            }
        var trendsPicB:String? = null
            set(trendsPicB) {
                field = trendsPicB?.trim { it <= ' ' }
            }
        var trendsPicC:String? = null
            set(trendsPicC) {
                field = trendsPicC?.trim { it <= ' ' }
            }
        var trendsPicD:String? = null
            set(trendsPicD) {
                field = trendsPicD?.trim { it <= ' ' }
            }
        var trendsPicE:String? = null
            set(trendsPicE) {
                field = trendsPicE?.trim { it <= ' ' }
            }
        var trendsPicF:String? = null
            set(trendsPicF) {
                field = trendsPicF?.trim { it <= ' ' }
            }
        var trendsPicG:String? = null
            set(trendsPicG) {
                field = trendsPicG?.trim { it <= ' ' }
            }
        var trendsPicH:String? = null
            set(trendsPicH) {
                field = trendsPicH?.trim { it <= ' ' }
            }
        var trendsPicI:String? = null
            set(trendsPicI) {
                field = trendsPicI?.trim { it <= ' ' }
            }
        var trendsTextContent:String? = null
            set(trendsTextContent) {
                field = trendsTextContent?.trim { it <= ' ' }
            }

        var trendsLiked:Int? = null

        var trendsIssuingTime: Timestamp?=null
        var userAccount:String? = null
            set(userAccount) {
                field = userAccount?.trim { it <= ' ' }
            }

        companion object {
            private const val serialVersionUID = 1L
        }

        override fun toString(): String {
            return "UserTrends(id=$id, trendsPicA=$trendsPicA, trendsPicB=$trendsPicB, trendsPicC=$trendsPicC, trendsPicD=$trendsPicD, trendsPicE=$trendsPicE, trendsPicF=$trendsPicF, trendsPicG=$trendsPicG, trendsPicH=$trendsPicH, trendsPicI=$trendsPicI, trendsTextContent=$trendsTextContent, trendsLiked=$trendsLiked, trendsIssuingTime=$trendsIssuingTime, userAccount=$userAccount)"
        }

}