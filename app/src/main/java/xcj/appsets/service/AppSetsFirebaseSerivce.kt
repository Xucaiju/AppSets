package xcj.appsets.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import xcj.appsets.Constant
import xcj.appsets.util.PreferenceUtil
import xcj.appsets.util.setCacheCreateTime
import java.util.*

class AppSetsFirebaseSerivce : FirebaseMessagingService() {

    companion object {
        val instance: AppSetsFirebaseSerivce by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AppSetsFirebaseSerivce()
        }
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d("AppSetsFireBaseService OnNewToken", "Refreshed token: $token")
        CoroutineScope(Default).launch {
            PreferenceUtil.putString(this@AppSetsFirebaseSerivce, Constant.FIRE_BASE_FCM_TOKEN, token)
            setCacheCreateTime(this@AppSetsFirebaseSerivce, Calendar.getInstance().timeInMillis, "FirebaseTokenGenerateTime")
        }
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(token)
    }

}