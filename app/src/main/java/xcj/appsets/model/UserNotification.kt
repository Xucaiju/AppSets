package xcj.appsets.model
typealias datetime = java.sql.Timestamp
class UserNotification {
    var id:Int? = null
    var fromUserAccount:String? = null
        set(fromUserAccount) {
            field = fromUserAccount?.trim{it<=' '}
        }
    var toUserAccount:String? = null
        set(toUserAccount) {
            field = toUserAccount?.trim{it<=' '}
        }
    var notificationContent:String? = null
        set(notificationContent) {
            field = notificationContent?.trim{it<=' '}
        }
    var notificationFromDatetime: datetime? = null
    var isChecked:Int? = null
    companion object {
        private const val serialVersionUID = 1L
    }

    override fun toString(): String {
        return "NotificationKt(id=$id, fromUserAccount=$fromUserAccount, toUserAccount=$toUserAccount, notificationContent=$notificationContent, notificationFromDatetime=$notificationFromDatetime, isChecked=$isChecked)"
    }
}