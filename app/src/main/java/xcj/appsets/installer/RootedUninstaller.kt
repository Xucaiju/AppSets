package xcj.appsets.installer

import xcj.appsets.model.App
import xcj.appsets.util.Log.Companion.d
import xcj.appsets.util.Log.Companion.w
import xcj.appsets.util.Root

class RootedUninstaller {
    var root: Root = Root()
    fun uninstall(app: App){
        try {
            if (root.isTerminated() || !root.isAcquired()) {
                root = Root()
                if (!root.isAcquired()) {
                    return
                }
            }
            d(ensureCommandSucceeded(root.exec("pm clear " + app.getPackageName())))
            d(ensureCommandSucceeded(root.exec("pm uninstall " + app.getPackageName())))
        } catch (e: Exception) {
            w(e.message)
        }
    }
    private fun ensureCommandSucceeded(result: String?): String? {
        if (result == null || result.isEmpty()) throw RuntimeException(root.readError())
        return result
    }
}