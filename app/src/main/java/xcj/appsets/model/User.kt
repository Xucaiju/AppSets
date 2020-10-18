package xcj.appsets.model

data class User(var id:Int?=0,
                var account:String? = "",
                var password:String? = "",
                var username:String? = "unset",
                var avatar:String? = "",
                var phoneNumber:String? = "12345678900",
                var favoriteAppsId:Int? = -1234567890,
                var todayAppId:Int? = -1234567890,
                var signupTime:String? = "",
                var lastSigninTime:String? = "",
                var email:String? = "") {
    override fun toString(): String {
        return "User(id=$id, account=$account, password=$password, username=$username, avatar=$avatar, phoneNumber=$phoneNumber, favoriteAppsId=$favoriteAppsId, todayAppId=$todayAppId, signupTime=$signupTime, lastSigninTime=$lastSigninTime, email=$email)"
    }

}