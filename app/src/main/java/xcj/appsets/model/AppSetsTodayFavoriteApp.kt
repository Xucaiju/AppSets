package xcj.appsets.model

import androidx.room.Entity

@Entity(primaryKeys = ["todayAppId", "userAccount"])
data class AppSetsTodayFavoriteApp(var id:Int,
                                   var todayAppId:Int,
                                   var userAccount:String)    {
    override fun toString(): String {
        return "AppSetsTodayFavoriteApps(id=$id, todayAppId=$todayAppId, userAccount='$userAccount')"
    }
}