package xcj.appsets.exception

import com.dragons.aurora.playstoreapiv2.AuthException

open class InvalidApiException(message:String) : AuthException(message) {
    constructor():this("InvalidApiException")
/*    private var code = 0

    constructor() : super("InvalidApiException")
    constructor(message: String?, code: Int) : super(message) {
        this.code = code
    }

    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)

    fun getCode(): Int {
        return code
    }

    fun setCode(code: Int) {
        this.code = code
    }*/
}