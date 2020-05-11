package xcj.appsets

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.IntentFilter
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import io.alterac.blurkit.BlurKit
import io.reactivex.plugins.RxJavaPlugins
import xcj.appsets.database.AppSetsDatabase
import xcj.appsets.events.Event
import xcj.appsets.events.RxBus
import xcj.appsets.installer.Installer
import xcj.appsets.installer.InstallerService
import xcj.appsets.model.App
import xcj.appsets.util.Log
import xcj.appsets.util.PreferenceUtil
import xcj.appsets.util.clearOldInstallationSessions
import xcj.appsets.util.setCacheCreateTime
import xcj.appsets.worker.FirebaseToeknCoroutineUploadWorker
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class AppSetsApplication:Application() {
    companion object{

        var appsetsDB:AppSetsDatabase? = null

        @JvmField
        var api: GooglePlayAPI? = null

        private var rxBus: RxBus? = null
        private var bulkUpdateAlive:Boolean = false
        private var ongoingUpdateList:List<App> = ArrayList()
        private var installer: Installer? = null
        fun getRxBus() = rxBus
        fun isBulkUpdateAlive() = bulkUpdateAlive
        @JvmStatic
        fun setBulkUpdateAlive(updating:Boolean) {
            this.bulkUpdateAlive = updating
        }
        fun getOngoingUpdateList() = ongoingUpdateList
        fun setOngoingUpdateList(ongoingUpdateList:List<App>) {
            this.ongoingUpdateList  = ongoingUpdateList
        }

        fun removeFromOngoingUpdateList(packageName:String) {
            var iterator = ongoingUpdateList.iterator() as MutableIterator
            while (iterator.hasNext()){
                if(packageName == iterator.next().getPackageName()){
                    iterator.remove()
                }
            }
            if(ongoingUpdateList.isEmpty()){
                setBulkUpdateAlive(false)
            }
        }
        fun getInstaller()= installer
        fun rxNotify(event: Event){
            rxBus?.getBus()?.accept(event)
        }
        fun getAppSetsDB() = appsetsDB

    }


    override fun onCreate() {
        super.onCreate()
        appsetsDB = AppSetsDatabase.getDatabase(this)
        rxBus = RxBus()
        installer = Installer(this)
        //blurkit
        BlurKit.init(this)
        //Create notification channels
        createNotificationChannel()
        clearOldInstallationSessions(this)

        //Register global install broadcast receiver.
        registerReceiver(
            installer?.getPackageInstaller()?.getBroadcastReceiver(),
            IntentFilter(InstallerService.ACTION_INSTALLATION_STATUS_NOTIFICATION)
        )

        //Global RX-Error handler, just simply logs, I make sure all errors are handled at origin.
        RxJavaPlugins.setErrorHandler {

            if(it?.message != null)
                Log.e(it.message)
            else
                return@setErrorHandler
        }
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    android.util.Log.w("Firebase", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                val msg = getString(R.string.msg_token_fmt, token)
                android.util.Log.d("Firebase", msg)
                PreferenceUtil.putString(this, Constant.FIRE_BASE_FCM_TOKEN, token)
                setCacheCreateTime(this, Calendar.getInstance().timeInMillis, "FirebaseTokenGenerateTime")
                val uploadTokenRequest = PeriodicWorkRequestBuilder<FirebaseToeknCoroutineUploadWorker>(MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MINUTES).build()
                 val enqueue = WorkManager.getInstance(this).enqueue(uploadTokenRequest)

            //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            })
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val alertChannel = NotificationChannel(
                Constant.NOTIFICATION_CHANNEL_ALERT,
                getString(R.string.notification_channel_alert),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            val generalChannel = NotificationChannel(
                Constant.NOTIFICATION_CHANNEL_GENERAL,
                getString(R.string.notification_channel_general),
                NotificationManager.IMPORTANCE_MIN
            ).apply {

                enableLights(false)
                enableVibration(false)
                setShowBadge(false)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE
            }

            val appsetsDownloadChannel = NotificationChannel(
                Constant.NOTIFICATION_CHANNEL_APPSET_DOWNLOAD,
                getString(R.string.notification_channel_appsets_download),
                NotificationManager.IMPORTANCE_NONE
            ).apply {
                enableLights(false)
                enableVibration(false)
                setShowBadge(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }






            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(alertChannel)
                notificationManager.createNotificationChannel(generalChannel)
                notificationManager.createNotificationChannel(appsetsDownloadChannel)
            }
        }
    }
}