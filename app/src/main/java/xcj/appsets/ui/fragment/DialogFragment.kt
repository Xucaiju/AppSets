package xcj.appsets.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.text.Html
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ViewSwitcher
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat.getColor
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import biz.laenger.android.vpbs.ViewPagerBottomSheetDialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.dragons.aurora.playstoreapiv2.AndroidAppDeliveryData
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
import com.tonyodev.fetch2core.Func
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.apache.commons.io.FilenameUtils
import xcj.appsets.AppSetsApplication
import xcj.appsets.AutoDisposable
import xcj.appsets.Constant
import xcj.appsets.R
import xcj.appsets.algorithm.Sort
import xcj.appsets.download.RequestBuilder.buildObbRequestList
import xcj.appsets.download.RequestBuilder.buildRequest
import xcj.appsets.download.RequestBuilder.buildSplitRequestList
import xcj.appsets.enums.ErrorType
import xcj.appsets.events.Event
import xcj.appsets.exception.AppNotFoundException
import xcj.appsets.exception.NotPurchasedException
import xcj.appsets.manager.DownloadManager.Companion.getFetchInstance
import xcj.appsets.model.App
import xcj.appsets.model.Screenshot
import xcj.appsets.model.TodayApp
import xcj.appsets.notification.GoogleAppDownloadNotification
import xcj.appsets.task.DeliveryDataTask
import xcj.appsets.task.GZipTask
import xcj.appsets.util.*
import xcj.appsets.util.ContextUtil.runOnUiThread
import xcj.appsets.viewmodel.AppDetailsViewModel
import java.io.File
import java.util.*


class DialogFragment() : ViewPagerBottomSheetDialogFragment() {
    companion object{
        var eventType:Event.SubType? = null
    }
    private var googleAppHashCode:Int=0
    lateinit var googleApp:App
    lateinit var appsetsApp:TodayApp
    private lateinit var screenshotRecyclerView: RecyclerView
    private lateinit var appDetailsIcon: AppCompatImageView
    private lateinit var appDisplayName: MaterialTextView
    private lateinit var appUpdateLog: MaterialTextView
    private lateinit var appDevWeb: MaterialTextView
    private lateinit var appDevMail: MaterialTextView
    private lateinit var appDevAddr: MaterialTextView
    private lateinit var appViedo: MaterialTextView
    private lateinit var viewSwitcher: ViewSwitcher
    private lateinit var devInfoContainter: LinearLayout
    private lateinit var reviewChart: RadarChart
    private var fetch: Fetch? = null
    private var fetchListener: FetchListener? = null
    private var progress:Int = 0
    private val autoDisposable = AutoDisposable()
    private var appDetailsViewModel: AppDetailsViewModel? = null
    private var fragmentPosition: Int? = null
    private var theApp: Any? = null
    private var mviewLifecyclerOwner: LifecycleOwner? = null
    lateinit var progressBar: ContentLoadingProgressBar
    lateinit var btnCancel: AppCompatImageButton
    lateinit var progressStatus:MaterialTextView
    lateinit var progressTxt:MaterialTextView
    lateinit var actionsButtonLayout :LinearLayout
    lateinit var progressLayout :LinearLayout
    lateinit var btnPositive:MaterialButton
    lateinit var btnNegative:MaterialButton
    private var notification: GoogleAppDownloadNotification? = null
    private var isPaused = false
    private val globalInstallReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.data == null || !TextUtils.equals(
                    arguments?.getString(Constant.INTENT_PACKAGE_NAME),
                    intent.data?.schemeSpecificPart
                )
            ) {
                return
            }
            ContextUtil.runOnUiThread(Runnable { })//drawButtons()
        }
    }

    constructor(fragmentPosition: Int, theApp: Any?, mviewLifecycleOwner: LifecycleOwner) : this() {
        this.fragmentPosition = fragmentPosition
        this.theApp = theApp
        this.mviewLifecyclerOwner = mviewLifecycleOwner
    }

    override fun onStart() {
        super.onStart()
/*        val dm = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        dialog?.window?.findViewById<FrameLayout>(an)?.let {
            BottomSheetBehavior.from(
                it
            )
        }?.peekHeight = (dm.widthPixels*0.44).toInt()*/
    }




    @SuppressLint("RestrictedApi", "ResourceType")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView:View? = View.inflate(requireContext(), R.layout.dialog_bottom_sheet, null)
        val dm = DisplayMetrics()

        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        contentView?.let {
            dialog.setContentView(it)

            screenshotRecyclerView = it.findViewById(R.id.bottom_sheet_screenshot_recycler)
            appDetailsIcon = it.findViewById(R.id.app_details_icon)
            appDisplayName = it.findViewById(R.id.app_details_display_name)
            appUpdateLog = it.findViewById(R.id.app_update_log_textview)
            appDevWeb = it.findViewById(R.id.txt_dev_web_desc)
            appDevMail = it.findViewById(R.id.txt_dev_email_desc)
            appDevAddr = it.findViewById(R.id.txt_dev_addr_desc)
            devInfoContainter = it.findViewById(R.id.devInfo)
            viewSwitcher = it.findViewById(R.id.include2)
            reviewChart = it.findViewById(R.id.app_review_chart)
            btnCancel = it.findViewById(R.id.btn_cancel)
            progressBar = it.findViewById(R.id.progress_download)
            progressStatus = it.findViewById(R.id.progress_status)
            progressTxt = it.findViewById(R.id.progress_txt)
            actionsButtonLayout = it.findViewById(R.id.view1)
            progressLayout = it.findViewById(R.id.view2)
            btnPositive = it.findViewById(R.id.btn_positive)
            btnNegative = it.findViewById(R.id.btn_negative)
        }

        reviewChart.apply {
            webLineWidth = 1f
            webColor = getColor(context, R.color.colorGray)
            webLineWidthInner = 1f
            webColorInner = getColor(context, R.color.colorGray)
            webAlpha = 100
            setBackgroundColor(android.R.attr.colorBackground)
            description = Description().apply {
                text = getString(R.string.rating_from_google_play)
            }
            isRotationEnabled = false
            animateXY(1400, 1400, Easing.EaseInOutQuad)
            xAxis.apply {
                textColor = requireActivity().theme.obtainStyledAttributes(intArrayOf(android.R.attr.textColorPrimary)).getColor(0,Color.BLACK)
                textSize = 16f
                yOffset = 0f
                xOffset = 0f
                valueFormatter = object : ValueFormatter() {
                    private val star = arrayOf("1", "2", "3", "4", "5")

                    override fun getFormattedValue(value: Float): String {
                        return star[value.toInt() % star.size]
                    }
                }
                // textColor = android.R.attr.textColorPrimary
            }
            yAxis.apply {
                setLabelCount(5, true)
                textSize = 9f
                axisMinimum = 0f
                //axisMaximum = 100000f
                setDrawLabels(false)
            }
            legend.apply {
                //isEnabled = false
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
                xEntrySpace = 7f
                yEntrySpace = 5f
                // textColor = android.R.attr.textColorPrimary
            }

        }

        val viewmodel = activity?.application?.let {
            ViewModelProvider.AndroidViewModelFactory.getInstance(
                it
            ).create(AppDetailsViewModel::class.java)
        }
        appDetailsViewModel = viewmodel
        mviewLifecyclerOwner?.lifecycle?.let { autoDisposable.bindTo(it) }
        when (fragmentPosition) {
            0 -> {

            }
            1 -> {
             /*   ConnectionLiveData(requireContext()).observe(requireActivity()) {
                    if(it.isConnected){
                        AppSetsApplication.rxNotify(Event(Event.SubType.NETWORK_AVAILABLE))
                    }else{
                        AppSetsApplication.rxNotify(Event(Event.SubType.NETWORK_UNAVAILABLE))

                    }
                }*/
               // validateApi(requireContext())
                googleApp = (theApp as App)
                drawBasic(googleApp)
                val packageName = googleApp?.getPackageName()
               /* val disposable: Disposable? = AppSetsApplication.getRxBus()?.getBus()?.subscribe( { event ->
                        Log.d("Event Type Is", "${event?.getSubType()}")
                        when (event?.getSubType()) {
                            Event.SubType.INSTALLED, Event.SubType.UNINSTALLED -> { }// drawButtons() Nothing
                            Event.SubType.API_SUCCESS -> appDetailsViewModel?.fetchAppDetails(packageName)
                            else -> { }
                        }
                    }){
                        Log.d("Throwable","${it}")
                    }
                autoDisposable.add(disposable)*/

               Observable.just(eventType).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe({
                   it?.let {event->
                       if(event == Event.SubType.API_SUCCESS){
                           appDetailsViewModel?.fetchAppDetails(packageName)
                       }
                   }
               }){

               }?.let {
                   autoDisposable.add(it)
               }

     /*           AppSetsApplication.getRxBus()?.getBus()?.subscribe { event ->
                            when (event?.getSubType()) {
                                Event.SubType.NETWORK_AVAILABLE -> {
                            //Log.d("Event类型", "NETWORK_AVAILABLE")
                            if (ApiValidateService.isServiceRunning) {
                               // Log.d("API验证服务状态", "正在运行")
                                requireActivity().stopService(Intent(requireContext(), ApiValidateService::class.java))
                            }


                            if (Accountant.isLoggedIn(requireContext())!!) {
                               // Log.d("GoogleAPI登录状态", "已登录")
                                validateApi(requireContext())
                            }

                        }

                        Event.SubType.NETWORK_UNAVAILABLE -> {

                        }

                        Event.SubType.API_SUCCESS -> {
                            appDetailsViewModel?.fetchAppDetails(packageName)

                        }


                        Event.SubType.API_FAILED, Event.SubType.API_ERROR -> {
                        }

                        else -> {
                            // do
                        }
                    }
                }.let {
                    autoDisposable.add(it)
                }*/


                //appDetailsViewModel?.fetchAppDetails(packageName)
                mviewLifecyclerOwner?.let { lifecycleOwner ->
                    appDetailsViewModel?.appDetails?.observe(lifecycleOwner) {

                        if (it != null) {
                            googleApp = it
                            googleAppHashCode = googleApp.getPackageName().hashCode()
                        }

                        draw(googleApp, contentView)
                    }
                }
                appDetailsViewModel?.error?.observe(this, { errorType ->
                    when (errorType) {
                        ErrorType.NO_API, ErrorType.SESSION_EXPIRED -> validateApi(requireContext())
                        ErrorType.NO_NETWORK -> {
                            /*  showSnackBar(
                                  coordinator,
                                  R.string.error_no_network,
                                  -2
                              ) { v -> model.fetchAppDetails(packageName) }*/
                            //nothing
                        }
                        else -> {
                        }
                    }
                })

            }
            else -> {
            }
        }

    }
/*    companion object {
        val instance:DialogFragment by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            DialogFragment()
        }
    }*/

    private fun drawBasic(app: App?) {
        Glide.with(this)
            .asBitmap()
            .load(app?.getIconUrl())
            .transition(BitmapTransitionOptions().crossFade())
            .transform(CenterCrop(), RoundedCorners(250))
            .into(appDetailsIcon)

        appDisplayName.apply {
            alpha = 0f
            text = app?.getDisplayName()
            animate().alpha(1f).setDuration(450).start()

        }

    }

    private fun draw(app: App?, fragmentView: View?) {
        val disposable = Observable.just(
            // GeneralDetails(this, com.aurora.store.ui.details.DetailsActivity.app),
            Screenshot(fragmentView, app)
            /* Reviews(this, com.aurora.store.ui.details.DetailsActivity.app),
             ExodusPrivacy(this, com.aurora.store.ui.details.DetailsActivity.app),
             Video(this, com.aurora.store.ui.details.DetailsActivity.app),
             Beta(this, com.aurora.store.ui.details.DetailsActivity.app),
             AppLinks(this, com.aurora.store.ui.details.DetailsActivity.app),
             ActionButton(
                 this,
                 com.aurora.store.ui.details.DetailsActivity.app
             ).also({
                 actionButton = it
             })*/
        )
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(Screenshot::draw)
            .subscribe()
        autoDisposable.add(disposable)

        app?.apply {
            if(!getIsFree()!!){
                checkPurchased(this)
            }
            if(getIsInstalled()!!){
                runOrUpdate(this)
            }

            getChanges()?.also {
                if (it.isEmpty()) {
                    appUpdateLog.text = getString(R.string.developer_not_provide_change_log)
                } else {
                    appUpdateLog.text = Html.fromHtml(it, Html.FROM_HTML_MODE_COMPACT).toString()
                }
            }
            getDeveloperWebsite()?.also {
                if (it.isEmpty()) {
                    appDevWeb.text = getString(R.string.action_unknown)
                } else {
                    appDevWeb.text = it
                }
            }
            getDeveloperEmail()?.also {
                if (it.isEmpty()) {
                    appDevMail.text = getString(R.string.action_unknown)
                } else {
                    appDevMail.text = it
                }
            }
            getDeveloperAddress()?.also {
                if (it.isEmpty()) {
                    appDevAddr.text = getString(R.string.action_unknown)
                } else {
                    appDevAddr.text = it
                }
            }
            setData(this)
            reviewChart.apply {
                visibility = View.VISIBLE
                alpha = 0f
                animate().alpha(1f).setDuration(350).start()
            }

            appUpdateLog.apply {
                visibility = View.VISIBLE
                alpha = 0f
                animate().alpha(1f).setDuration(350).translationY(0f).start()
            }
            devInfoContainter.apply {
                visibility = View.VISIBLE
                alpha = 0f
                animate().alpha(1f).setDuration(350).setStartDelay(250).translationY(0f).start()

            }
            viewSwitcher.visibility = View.VISIBLE
            btnPositive.apply {
                alpha = 0f
                animate().alpha(1f).setDuration(350).setStartDelay(950).start()
                setOnClickListener(downloadAppListener())
            }
        }

    }

    private fun checkPurchased(app:App) {
        autoDisposable.add(
            Observable.fromCallable{
                DeliveryDataTask(context).getDeliveryData(app)
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                        btnPositive.setText(R.string.details_install)
                    }
                ){
                    btnPositive.setText(
                        R.string.details_purchase
                    )
                }
        )
    }
    private fun runOrUpdate(app:App) {
        val versionName: String? = app.getVersionName()
        if (TextUtils.isEmpty(versionName)) {
            return
        }
        try {
            val info: PackageInfo? = requireContext().packageManager?.getPackageInfo(app.getPackageName(), 0)
            val currentVersion = info?.versionName
            btnPositive.setText(R.string.details_update)
            if (info?.longVersionCode == app.getVersionCode()?.toLong() || null == currentVersion) {
                btnPositive.setText(R.string.details_run)
                btnPositive.setOnClickListener(openAppListener())
                btnPositive.visibility = if (PackageUtil.isPackageLaunchable(
                        requireContext(),
                        app.getPackageName()
                    )
                ) View.VISIBLE else View.GONE
            } else if ((app.getPackageName()?.let {
                    app.getVersionCode()?.let { it1 ->
                        PathUtil.getLocalApkPath(
                            requireContext(), it,
                            it1
                        )
                    }
                }).let {File(it).exists()  }
            ) {
                btnPositive.setOnClickListener(installAppListener())
                btnPositive.visibility = View.VISIBLE
            }
        } catch (ignored: PackageManager.NameNotFoundException) {
        }
    }
    private fun openAppListener(): View.OnClickListener? {
        btnPositive.setText(R.string.details_run)
        return View.OnClickListener {
            val i: Intent? = getLaunchIntent()
            try {
                it.context.startActivity(i)
            } catch (e: ActivityNotFoundException) {
                xcj.appsets.util.Log.e(e.message)
            }
        }
    }
    private fun getLaunchIntent(): Intent? {
        var mIntent = googleApp.getPackageName()?.let {
            requireContext().packageManager.getLaunchIntentForPackage(
                it
            )
        }
        val isTv = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isTv()
        if (isTv) {
            val l = googleApp.getPackageName()?.let {
                requireContext().packageManager
                    .getLeanbackLaunchIntentForPackage(it)
            }
            if (null != l) {
                mIntent = l
            }
        }
        if (mIntent == null) {
            return null
        }
        mIntent.addCategory(if (isTv) Intent.CATEGORY_LEANBACK_LAUNCHER else Intent.CATEGORY_LAUNCHER)
        return mIntent
    }
    private fun isTv(): Boolean {
        val uiMode = requireContext().resources.configuration.uiMode
        return uiMode and Configuration.UI_MODE_TYPE_MASK == Configuration.UI_MODE_TYPE_TELEVISION
    }

    private fun resumeAppListener(): View.OnClickListener? {
        fetchListener = getFetchListener()
        fetch?.addListener(fetchListener!!)
        btnPositive.setText(R.string.download_resume)
        return View.OnClickListener { v: View? ->
            switchViews(true)
            fetch!!.resumeGroup(googleAppHashCode)
        }
    }

    private fun installAppListener(): View.OnClickListener? {
        btnPositive.setText(R.string.details_install)
        return View.OnClickListener {
            btnPositive.setText(R.string.details_installing)
            btnPositive.isEnabled = false
            notification.notifyInstalling()
            AppSetsApplication.getInstaller()?.install(googleApp)
        }
    }

    private fun downloadAppListener():View.OnClickListener{
        return View.OnClickListener {
            Observable.fromCallable {
                DeliveryDataTask(context).getDeliveryData(googleApp)
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it?.let {
                        initiateDownload(it)
                    }
                }) {
                    runOnUiThread(
                        Runnable {
                            if (it is NotPurchasedException) {
                                Log.d("%s not purchased", googleApp.getDisplayName())
                                //showPurchaseDialog()
                            }
                            if (it is AppNotFoundException) {
                                Log.d("%s not not found", googleApp.getDisplayName())
                                /*showDialog(
                                    R.string.dialog_unavailable_title,
                                    R.string.dialog_unavailable_desc
                                )*/
                            }
                            if (it is NullPointerException) {
                                /*if (App.Restriction.RESTRICTED_GEO === app.getRestriction()) showDialog(
                                    R.string.dialog_geores_title,
                                    R.string.dialog_geores_desc
                                )
                                if (App.Restriction.INCOMPATIBLE_DEVICE === app.getRestriction()) showDialog(
                                    R.string.dialog_incompat_title,
                                    R.string.dialog_incompat_desc
                                )*/
                            }
                            //draw()
                            //switchViews(false)
                        }

                    )


                }?.let { autoDisposable.add(it) }
        }
    }
    private fun initiateDownload(
        deliveryData: AndroidAppDeliveryData
    ) {
        val request: Request? = buildRequest(context, googleApp, deliveryData.downloadUrl)
        val splitList: List<Request> = buildSplitRequestList(context, googleApp, deliveryData)
        val obbList: List<Request> = buildObbRequestList(context, googleApp, deliveryData)
        val requestList: MutableList<Request> = ArrayList<Request>()
        request?.let {
            requestList.add(it)
        }
        requestList.addAll(splitList)
        requestList.addAll(obbList)
        fetchListener = getFetchListener()
        fetchListener?.let { fetch?.addListener(it) }
        fetch?.enqueue(
            requestList,
            Func<List<Pair<Request, Error>>> {
                Log.i("Downloading Splits : %s", googleApp.getPackageName())
            }
        ) /*{ updatedRequestList:List<Pair<Request, Error>>->
            for(i in updatedRequestList){
                if(i.second == Error.NONE){

                }
            }
        }*//*{
            Log.i("Downloading Splits : %s", googleApp.getPackageName())
        }*/

        //Add <PackageName,DisplayName> and <PackageName,IconURL> to PseudoMaps
        context?.let {
            PackageUtil.addToPseudoPackageMap(it, googleApp.getPackageName()?:"", googleApp.getDisplayName()?:"")
            PackageUtil.addToPseudoURLMap(it, googleApp.getPackageName()?:"", googleApp.getIconUrl()?:"")
        }

    }

    private fun setData(app:App?) {
        var minStar = 0f
        var maxStar = 0f
        val starList = arrayListOf<Float>()
        val entries2 = ArrayList<RadarEntry>().also {
            app?.getRating()?.apply {
                val star1 = getStars(1).toFloat()
                val star2 = getStars(2).toFloat()
                val star3 = getStars(3).toFloat()
                val star4 = getStars(4).toFloat()
                val star5 = getStars(5).toFloat()
                starList.add(star1)
                starList.add(star2)
                starList.add(star3)
                starList.add(star4)
                starList.add(star5)
                it.add(RadarEntry(star1))
                it.add(RadarEntry(star2))
                it.add(RadarEntry(star3))
                it.add(RadarEntry(star4))
                it.add(RadarEntry(star5))
            }
        }
        Sort.bubbleSort(starList)

        maxStar = starList[4]
        val set2 = RadarDataSet(entries2, getString(R.string.rating_overview)).apply {
            setDrawIcons(false)
            color = Color.rgb(12, 188, 105)
            fillColor = Color.rgb(12, 188, 105)
            setDrawFilled(true)
            lineWidth = 1f
            fillAlpha = 60
            isDrawHighlightCircleEnabled = true
            setDrawHighlightIndicators(true)

        }

        val sets = ArrayList<IRadarDataSet>()
        /* sets.add(set1)*/
        sets.add(set2)
        val data = RadarData(sets).apply {
            setValueTextSize(8f)
            setDrawValues(false)
            // setValueTextColor(android.R.attr.textColorSecondary)
        }
        reviewChart.yAxis.apply {
            resetAxisMaximum()
            axisMaximum = maxStar+(maxStar*0.1).toFloat()//app?.getRating()?.getStars(5)?.toFloat()?.plus(666f) ?: 0f

        }
        reviewChart.data = data
        reviewChart.invalidate()
        //val fiveStartCount = app?.getRating()?.getStars(5).apply {
            /* val mul = fiveStartCount?.toFloat()?.plus(999999f)
        val min = 20f
        val cnt = 5*/


            // fiveStartCount?.toFloat()?.let { entries2.add(RadarEntry(it)) }
            // NOTE: The order of the entries when being added to the entries array determines their position around the center of
            // the chart.
            /*for (i in 0 until cnt) {

            val val2 = (Math.random() * mul).toFloat() + min
            entries2.add(RadarEntry(val2))
        }*/
      /*      var maxStar = 0f
            var minStar = 0f
        var temp = 0f
      for(i in starList.indices){
          for(j in starList.indices){
              if(starList[j]>starList[i]){
                  temp = starList[j]
                  starList[j] = starList[i]
                  starList[i] = temp
              }
          }
      }*/
          /*  if(n.value>maxStar){
                maxStar = n.value
            }else{
                minStar = n.value
            }*/



        }

       // }

    private fun setupFetch() {
        context?.let {
            fetch = getFetchInstance(it)
            notification = GoogleAppDownloadNotification(it)


            fetch?.getFetchGroup(googleAppHashCode, Func { fetchGroup: FetchGroup ->
                if (fetchGroup.groupDownloadProgress == 100) {
                    if (!googleApp.getIsInstalled()!! && PathUtil.fileExists(it, googleApp))
                        btnPositive.setOnClickListener(installAppListener())
                } else if (fetchGroup.downloadingDownloads.isNotEmpty()) {
                    switchViews(true)
                    fetchListener = getFetchListener()
                    fetch?.addListener(fetchListener!!)
                } else if (fetchGroup.pausedDownloads.isNotEmpty()) {
                    isPaused = true
                    btnPositive.setOnClickListener(resumeAppListener())
                }
            })
        }

    }
    private fun switchViews(showDownloads: Boolean) {
        if (viewSwitcher.currentView === actionsButtonLayout && showDownloads)
            viewSwitcher.showNext()
        else if (viewSwitcher.currentView === progressLayout && !showDownloads)
            viewSwitcher.showPrevious()
    }
    private fun getFetchListener(): FetchListener? {
        return object : AbstractFetchGroupListener() {
            override fun onQueued(
                groupId: Int,
                download: Download,
                waitingNetwork: Boolean,
                fetchGroup: FetchGroup
            ) {
                if (groupId == googleAppHashCode) {

                    runOnUiThread(Runnable {
                        progressBar.isIndeterminate = true
                        progressStatus.setText(R.string.download_queued)
                    })
                    notification?.notifyQueued()
                }
            }

            override fun onStarted(
                groupId: Int,
                download: Download,
                downloadBlocks: List<DownloadBlock>,
                totalBlocks: Int,
                fetchGroup: FetchGroup
            ) {
                if(groupId==googleAppHashCode){
                    runOnUiThread(Runnable {
                      progressBar.isIndeterminate = true
                        progressStatus.setText(R.string.download_waiting)
                        switchViews(true)
                    })
                }
            }

            override fun onResumed(
                groupId: Int,
                download: Download,
                fetchGroup: FetchGroup
            ) {
                if (groupId == googleAppHashCode) {
                    progress = fetchGroup.groupDownloadProgress
                            if (progress < 0) progress = 0
                    notification?.notifyProgress(progress, 0, googleAppHashCode)
                    runOnUiThread (Runnable{
                        progressStatus.setText(R.string.download_progress)
                        progressBar.setIndeterminate(false)
                    })
                }
            }

            override fun onProgress(
                groupId: Int,
                download: Download,
                etaInMilliSeconds: Long,
                downloadedBytesPerSecond: Long,
                fetchGroup: FetchGroup
            ) {
                if (groupId == googleAppHashCode) {
                    progress = fetchGroup.groupDownloadProgress
                    if (progress < 0)
                        progress = 0
                    runOnUiThread (Runnable{
                        btnCancel.visibility = View.VISIBLE
                        //Set intermediate to false, just in case xD
                        if (progressBar.isIndeterminate)
                            progressBar.isIndeterminate = false
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            progressBar.setProgress(progress, true)
                        } else
                            progressBar.progress = progress
                        progressStatus.setText(R.string.download_progress)
                        progressTxt.text = String.format("%s\\%",progress )
                    })
                    notification?.notifyProgress(progress, downloadedBytesPerSecond, googleAppHashCode)
                }
            }

            override fun onPaused(
                groupId: Int,
                download: Download,
                fetchGroup: FetchGroup
            ) {
                if (groupId == googleAppHashCode) {
                    notification?.notifyResume(googleAppHashCode)
                    runOnUiThread(Runnable {
                        switchViews(false)
                        progressStatus.setText(R.string.download_paused)
                    })
                }
            }

            override fun onError(
                groupId: Int,
                download: Download,
                error: Error,
                throwable: Throwable?,
                fetchGroup: FetchGroup
            ) {
                if (groupId == googleAppHashCode) {
                    notification?.notifyFailed()
                }
            }

            override fun onCompleted(
                groupId: Int,
                download: Download,
                fetchGroup: FetchGroup
            ) {
                if (groupId == googleAppHashCode && fetchGroup.groupDownloadProgress == 100) {
                    notification?.notifyCompleted()
                    runOnUiThread(Runnable {
                        switchViews(false)
                        progressStatus.setText(R.string.download_completed)
                        btnPositive.setOnClickListener(installAppListener())
                    })
                    if (shouldAutoInstallApk(context)) {
                        runOnUiThread(Runnable {
                            btnPositive.setText(R.string.details_installing)
                            btnPositive.setEnabled(false)
                        })
                        notification.notifyInstalling()
                        //Call the installer
                        AppSetsApplication.getInstaller()?.install(googleApp)
                    }
                    if (fetchListener != null) {
                        fetchListener?.let {
                            fetch?.removeListener(it)
                        }
                        fetchListener = null
                    }
                }
                if (groupId == googleAppHashCode && download.progress == 100) {
                    if (FilenameUtils.getExtension(download.file) == "gzip") {
                        autoDisposable.add(
                            Observable.fromCallable{
                                    GZipTask(context).extract(File(download.file))
                                }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnSubscribe {
                                    notification?.notifyExtractionProgress()
                                    runOnUiThread(Runnable { btnPositive.isEnabled = false })
                                }
                                .doOnTerminate{
                                    runOnUiThread (Runnable{ btnPositive.isEnabled = true })
                                }
                                .subscribe{
                                    if (it)
                                        notification?.notifyExtractionFinished()
                                    else
                                        notification?.notifyExtractionFailed()
                                }
                        )
                    }
                }
            }

            override fun onCancelled(
                groupId: Int, download: Download,
                fetchGroup: FetchGroup
            ) {
                if (groupId == googleAppHashCode) {
                    notification?.notifyCancelled()
                    runOnUiThread (Runnable{
                        switchViews(false)
                        progressBar.isIndeterminate = true
                        progressStatus.setText(R.string.download_canceled)
                    })
                }
            }
        }
    }
}

@SuppressLint("RestrictedApi")
private fun GoogleAppDownloadNotification?.notifyInstalling() {
    this?.builder?.mActions?.clear()
    this?.builder?.setContentText(context.getString(R.string.installer_status_ongoing))
    this?.show()
}
