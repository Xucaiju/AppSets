package xcj.appsets.task

import android.content.Context
import android.content.ContextWrapper
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI
import xcj.appsets.api.PlayStoreApiAuthenticator

open class BaseTask(context: Context?) : ContextWrapper(context){
    protected var context: Context? = context
    protected var api: GooglePlayAPI? = null

    @Throws(Exception::class)
    open fun getGooglePlayApi(): GooglePlayAPI? {
        return context?.let { PlayStoreApiAuthenticator.getPlayApi(it) }
    }


}