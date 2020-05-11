package xcj.appsets.exception

import java.io.IOException

class MalformedRequestException : IOException {
    var code = 0
    constructor() : super("MalformedRequestException")
    constructor(message: String?, code: Int) : super(message) {
        this.code = code
    }
    constructor(message: String?) : super(message) {}
    constructor(message: String?, cause: Throwable?) : super(message, cause)

}