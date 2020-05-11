package xcj.appsets.exception

import com.dragons.aurora.playstoreapiv2.AuthException

class CredentialsEmptyException : AuthException {
    constructor() : super("CredentialsEmptyException")
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}