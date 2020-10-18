package xcj.appsets.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.ColumnInfo.TEXT
import androidx.room.Entity
import java.io.Serializable
import java.sql.Date

@Entity(primaryKeys = ["appPackageName"])
data class TodayApp(var id:Int?=0,
                    var appDisplayname:String?="",
                    var appDevelopername:String?="",
                    var appRecommendedPictureA:String?="",
                    var appRecommendedPictureB:String?="",
                    var appScreenshotA:String?="",
                    var appScreenshotB:String?="",
                    var appScreenshotC:String?="",
                    var appScreenshotD:String?="",
                    var appScreenshotE:String?="",
                    var appShortDescription:String?="",
                    var appFullDescription:String?="",
                    var appFeatures:String?="",
                    var appPrice:Double?=0.00,
                    var appIcon:String?="",
                    @ColumnInfo(typeAffinity = TEXT)
                    var showedDate:Date?=Date(2000,1,1),
                    var downloadLink:String?="",
                    var favorites:Int?=0,
                    var appTypes:String?="",
                    var editorNote:String?="",
                    @NonNull
                    var appPackageName:String,
                    var devAccount:String?=""

):Serializable{
    override fun toString(): String {
        return "TodayApp(id=$id, appDisplayname=$appDisplayname, appDevelopername=$appDevelopername, appRecommendedPictureA=$appRecommendedPictureA, appRecommendedPictureB=$appRecommendedPictureB, appScreenshotA=$appScreenshotA, appScreenshotB=$appScreenshotB, appScreenshotC=$appScreenshotC, appScreenshotD=$appScreenshotD, appScreenshotE=$appScreenshotE, appShortDescription=$appShortDescription, appFullDescription=$appFullDescription, appFeatures=$appFeatures, appPrice=$appPrice, appIcon=$appIcon, showedDate=$showedDate, downloadLink=$downloadLink, favorites=$favorites, appTypes=$appTypes, editorNote=$editorNote, appPackageName='$appPackageName', devAccount=$devAccount)"
    }
}