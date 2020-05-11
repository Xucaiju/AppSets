package xcj.appsets.events

class Event(subType:SubType) {
    private var subType: SubType? = subType
    private var packageName: String? = null
    private var status = 0
    constructor(subType:SubType, packageName:String, status:Int):this(subType,packageName){
        this.status = status
    }
    constructor(subType:SubType, packageName:String):this(subType){
        this.packageName = packageName
    }
    fun getSubType() = this.subType
        enum class SubType{
        API_SUCCESS,
        API_FAILED,
        API_ERROR,
        BLACKLIST,
        WHITELIST,
        INSTALLED,
        UNINSTALLED,
        NETWORK_UNAVAILABLE,
        NETWORK_AVAILABLE,
        BULK_UPDATE_NOTIFY
    }
}
