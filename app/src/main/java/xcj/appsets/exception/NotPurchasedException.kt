package xcj.appsets.exception

import java.io.IOException

class NotPurchasedException : IOException {
    var code = 0

    constructor(message: String?, code: Int) : super(message) {
        this.code = code
    }

    constructor() : super("NotPurchasedException") {}
    constructor(message: String?) : super(message) {}
    constructor(message: String?, cause: Throwable?) : super(message, cause) {}
}