package xcj.appsets.model

import androidx.room.ColumnInfo
import androidx.room.ColumnInfo.TEXT
import androidx.room.Entity
import java.sql.Date

@Entity(primaryKeys = ["id"])
data class AppSetsUserReview(
                            var id:Int? = 0,
                             var todayAppId:Int? = 0,
                             var userAvatarUrl:String? = "",
                             var userAccount:String? = "",
                             val userNickName: String? = "",
                            @ColumnInfo(typeAffinity = TEXT)
                             var reviewTime:Date? = null,
                             var reviewDetails:String? = ""
){
    override fun toString(): String {
        return "AppSetsUserReview(id=$id, todayAppId=$todayAppId, userAvatar=$userAvatarUrl, userAccount=$userAccount, userNickName=$userNickName, reviewTime=$reviewTime, reviewDetails=$reviewDetails)"
    }
}