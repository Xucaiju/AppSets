package xcj.appsets.ui.fragment


import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.download_appsets_app_dialog.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import xcj.appsets.Constant
import xcj.appsets.R
import xcj.appsets.model.TodayApp
import java.io.File

class DownloadAppSetsAppDialogFragment(var todayApp: TodayApp?): DialogFragment() {
    lateinit var commingSoonText:MaterialTextView
    lateinit var appIcon:AppCompatImageView
    lateinit var appName:MaterialTextView
    lateinit var downloadProgress: ContentLoadingProgressBar
    lateinit var downloadProgressText:MaterialTextView
    lateinit var gobackgroundAction:MaterialButton

    var downloadPercent = 0



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.download_appsets_app_dialog,container,false)
        view?.let {
            commingSoonText = it.comming_soon_to_install_xxx
            appIcon = it.appsets_download_app_icon
            appName = it.appsets_download_app_displayname
            downloadProgress = it.appsets_download_progress_bar
            gobackgroundAction = it.appsets_download_go_background_action
            downloadProgressText = it.appsets_download_progress_text
        }
        return view
    }



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        val dialog = super.onCreateDialog(savedInstanceState)
        //android.R.color.transparent

        //dialog.window?.setBackgroundDrawableResource(R.drawable.down_appsets_app_dialog_fragment_shape)


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)


        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (dialog?.window != null) {
            dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }

    }

    override fun onStart() {
        super.onStart()
        val dm = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        dialog?.window?.setLayout((dm.widthPixels * 0.95).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val imgCacheFile = object : AsyncTask<Unit, Unit, File>() {
            override fun doInBackground(vararg params: Unit?): File {
                return Glide.with(requireContext()).asFile().load(todayApp?.appIcon).submit().get()
            }

        }.execute().get()
        val builder = NotificationCompat.Builder(requireContext(), Constant.NOTIFICATION_CHANNEL_APPSET_DOWNLOAD)
            .setSmallIcon(R.drawable.ic_arrow_downward_black_24dp)
            .setContentTitle("下载 ${todayApp?.appDisplayname}")
            .setContentText("正在下载中...")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOnlyAlertOnce(true)
        val PROGRESS_MAX = 100
        val PROGRESS_CURRENT = 0
        gobackgroundAction.setOnClickListener {
            this.dismiss()

            updateNotificationProgress(builder)

        }
        commingSoonText?.text = "即将为您安装 ${todayApp?.appDisplayname}"
        appName?.text = todayApp?.appDisplayname
            appIcon?.let {
                Glide
                    .with(requireContext())
                    .asBitmap()
                    .load(imgCacheFile)
                    .placeholder(R.color.colorTransparent)
                    .transition(BitmapTransitionOptions().crossFade(100))
                    .transform(CenterCrop(), RoundedCorners(250))
                    .into(it)
            }
        beginDownload()
    }
    private fun updateNotificationProgress(builder: NotificationCompat.Builder?){
        val notificationManagerCompat = NotificationManagerCompat.from(requireContext())
        notificationManagerCompat.apply {
            builder?.setProgress(100, 0, false)

           /* Observable.fromCallable { downloadPercent }.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread()).subscribe{

            }*/
       CoroutineScope(Default).launch {
           for(i in 1..100){
               builder?.setProgress(100, i, false)
               builder?.build()?.let { it1 -> notificationManagerCompat?.notify(99887, it1) }
               delay(500)
           }
           builder?.setContentText("Download complete")?.setProgress(0, 0, false)
       }

            builder?.build()?.let { notify(99887, it) }
        }

    }
    private fun beginDownload(){


        CoroutineScope(Main).launch{
            for(i in 1..100){
                downloadProgress.progress = i
                downloadProgressText.text = i.toString()
                downloadPercent = i
                delay(500)
            }

            gobackgroundAction.text = "已下载, 点击安装"
        }

    }
}