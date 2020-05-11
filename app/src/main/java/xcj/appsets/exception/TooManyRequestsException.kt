package xcj.appsets.exception

import java.io.IOException

class TooManyRequestsException : IOException {
    var code = 0

    constructor() : super("TooManyRequestsException") {}
    constructor(message: String?, code: Int) : super(message) {
        this.code = code
    }

    constructor(message: String?) : super(message) {}
    constructor(message: String?, cause: Throwable?) : super(message, cause) {}

}