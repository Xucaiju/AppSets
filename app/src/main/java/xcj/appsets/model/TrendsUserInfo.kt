package xcj.appsets.model

class TrendsUserInfo{
    var id:Int?=0
    var account:String?=null
        set(account) {
            field = account?.trim{it<=' '}
        }
    var username:String?=null
        set(username) {
            field = username?.trim{it<=' '}
        }
    var avatar:String?=null
        set(avatar) {
            field = avatar?.trim{it<=' '}
        }
    var userType:String?=null
        set(userType) {
            field = userType?.trim{it<=' '}
        }

    override fun toString(): String {
        return "UserInfoLite(id=$id, account=$account, username=$username, avatar=$avatar, userType=$userType)"
    }
}