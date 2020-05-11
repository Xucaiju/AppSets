package xcj.appsets.view

import android.view.View
import xcj.appsets.model.AbstractDetails
import xcj.appsets.model.App

class ActionButton(var mview: View?, var mapp: App?) : AbstractDetails(mview, mapp) {
    override fun draw() {

    }
/*
    var btnPositive: MaterialButton? = null
    var btnNegative: MaterialButton? = null
    var viewSwitcher: ViewSwitcher? = null
    var actions_layout: LinearLayout? = null
    var progress_layout: LinearLayout? = null
    var progressBar: ProgressBar? = null
    var progressTxt: TextView? = null
    var progressStatus: TextView? = null
    var btnCancel: ImageButton? = null

    private var isPaused = false
    private var hashCode = 0
    private val compositeDisposable = CompositeDisposable()
    private var fetch: Fetch? = null
    private var fetchListener: FetchListener? = null
    private var notification: GeneralNotification? = null
    private var progress = 0
    override fun draw() {
        val isInstalled: Boolean? = context?.let {
                app?.let { it1 -> PackageUtil.isInstalled(it, it1) }
        }?.apply {
            if(this){
                runOrUpdate()
            }
        }
        hashCode = app?.getPackageName().hashCode()
        btnNegative?.let {
            if (isInstalled != null) {
                ViewUtil.setVisibility(it, isInstalled)
            }
        }
        btnNegative?.setOnClickListener(uninstallAppListener())
        btnPositive?.setOnClickListener(downloadAppListener())
        btnCancel?.setOnClickListener(cancelDownloadListener())
        if (!app?.getIsFree()!!) {
            checkPurchased()
        }
        *//*if (isInstalled!!) runOrUpdate()*//*
       // setupFetch()
    }

   *//* private fun setupFetch() {
        fetch = DownloadManager.getFetchInstance(context)
        notification = GeneralNotification(context, app)
        fetch.getFetchGroup(hashCode, { fetchGroup ->
            if (fetchGroup.getGroupDownloadProgress() === 100) {
                if (!app.isInstalled() && PathUtil.fileExists(
                        context,
                        app
                    )
                ) btnPositive!!.setOnClickListener(installAppListener())
            } else if (fetchGroup.getDownloadingDownloads().size() > 0) {
                switchViews(true)
                fetchListener = getFetchListener()
                fetch.addListener(fetchListener)
            } else if (fetchGroup.getPausedDownloads().size() > 0) {
                isPaused = true
                btnPositive!!.setOnClickListener(resumeAppListener())
            }
        })
    }*//*

    private fun switchViews(showDownloads: Boolean) {
        viewSwitcher?.let {
            if (it.currentView === actions_layout && showDownloads)
                it.showNext()
            else if (it.currentView === progress_layout && !showDownloads)
                it.showPrevious()
        }

    }

    private fun runOrUpdate() {
        val versionName: String? = app?.getVersionName()
        if (TextUtils.isEmpty(versionName)) {
            return
        }
        try {
           context?.let{    it1->
               val info: PackageInfo? = app?.getPackageName()?.let {
                  it1.packageManager?.getPackageInfo(it, 0)
               }?.apply {
                   val currentVersion = this.versionName
                   btnPositive?.setText(R.string.details_update)
                   if (this.longVersionCode == app?.getVersionCode()?.toLong() || null == currentVersion) {
                       btnPositive?.setText(R.string.details_run)
                       btnPositive?.setOnClickListener(openAppListener())
                       btnPositive?.visibility = if (it1.let { it2->
                               PackageUtil.isPackageLaunchable(
                                   it2,
                                   app?.getPackageName()
                               )
                           }
                       ) View.VISIBLE else View.GONE
                   } else if ( File(getLocalApkPath(context!!, app?.getPackageName()!!, app?.getVersionCode()!!)).exists()) {
                       btnPositive!!.setOnClickListener(installAppListener())
                       btnPositive!!.visibility = View.VISIBLE
                   }
               }
           }



        } catch (ignored: PackageManager.NameNotFoundException) {
        }
    }

    private fun uninstallAppListener(): View.OnClickListener {
        return View.OnClickListener {
            context?.let { it1 ->
                Uninstaller(it1).uninstall(
                    app
                )
            }
        }
    }

    private fun installAppListener(): View.OnClickListener {
        btnPositive?.setText(R.string.details_install)
        return View.OnClickListener {
            btnPositive?.setText(R.string.details_installing)
            btnPositive?.isEnabled = false
            notification.notifyInstalling()
            AppSetsApplication.getInstaller().install(app)
        }
    }

    private fun downloadAppListener(): View.OnClickListener {
        if (shouldAutoInstallApk(context)) btnPositive.setText(R.string.details_install) else btnPositive.setText(
            R.string.details_download
        )
        btnPositive!!.visibility = View.VISIBLE
        btnPositive!!.isEnabled = true
        return View.OnClickListener { v: View? ->
            switchViews(true)
            //Remove any previous requests
            if (!isPaused) {
                fetch.deleteGroup(hashCode)
            }
            compositeDisposable.add(
                Observable.fromCallable(Callable<T> {
                    DeliveryData(context)
                        .getDeliveryData(app)
                })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        Consumer<T> { deliveryData: T ->
                            initiateDownload(
                                deliveryData
                            )
                        },
                        Consumer { err: Throwable? ->
                            runOnUiThread {
                                if (err is NotPurchasedException) {
                                    Log.d("%s not purchased", app.getDisplayName())
                                    showPurchaseDialog()
                                }
                                if (err is AppNotFoundException) {
                                    Log.d("%s not not found", app.getDisplayName())
                                    showDialog(
                                        R.string.dialog_unavailable_title,
                                        R.string.dialog_unavailable_desc
                                    )
                                }
                                if (err is NullPointerException) {
                                    if (App.Restriction.RESTRICTED_GEO === app.getRestriction()) showDialog(
                                        R.string.dialog_geores_title,
                                        R.string.dialog_geores_desc
                                    )
                                    if (App.Restriction.INCOMPATIBLE_DEVICE === app.getRestriction()) showDialog(
                                        R.string.dialog_incompat_title,
                                        R.string.dialog_incompat_desc
                                    )
                                }
                                draw()
                                switchViews(false)
                            }
                        }
                    )
            )
        }
    }

    private fun checkPurchased() {
        compositeDisposable.add(
            Observable.fromCallable(Callable<T> {
                DeliveryData(context)
                    .getDeliveryData(app)
            })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    Consumer<T> { androidAppDeliveryData: T? ->
                        btnPositive.setText(
                            R.string.details_install
                        )
                    },
                    Consumer { err: Throwable? ->
                        btnPositive.setText(
                            R.string.details_purchase
                        )
                    }
                )
        )
    }

    private fun resumeAppListener(): View.OnClickListener {
        fetchListener = getFetchListener()
        fetch.addListener(fetchListener)
        btnPositive.setText(R.string.download_resume)
        return View.OnClickListener { v: View? ->
            switchViews(true)
            fetch.resumeGroup(hashCode)
        }
    }

    private fun openAppListener(): View.OnClickListener {
        btnPositive.setText(R.string.details_run)
        return View.OnClickListener { v: View? ->
            val i = launchIntent
            if (null != i) {
                try {
                    context.startActivity(i)
                } catch (e: ActivityNotFoundException) {
                    Log.e(e.message)
                }
            }
        }
    }

    private fun cancelDownloadListener(): View.OnClickListener {
        return View.OnClickListener { v: View? ->
            fetch.cancelGroup(hashCode)
            if (notification != null) notification.notifyCancelled()
            switchViews(false)
        }
    }

    private val launchIntent: Intent?
        private get() {
            var mIntent: Intent? =
                context.getPackageManager().getLaunchIntentForPackage(app.getPackageName())
            val isTv = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isTv
            if (isTv) {
                val l: Intent = context.getPackageManager()
                    .getLeanbackLaunchIntentForPackage(app.getPackageName())
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

    private val isTv: Boolean
        private get() {
            val uiMode: Int = context.getResources().getConfiguration().uiMode
            return uiMode and Configuration.UI_MODE_TYPE_MASK == Configuration.UI_MODE_TYPE_TELEVISION
        }

    private fun initiateDownload(deliveryData: AndroidAppDeliveryData) {
        val request: Request = RequestBuilder
            .buildRequest(context, app, deliveryData.downloadUrl)
        val splitList: List<Request> = RequestBuilder
            .buildSplitRequestList(context, app, deliveryData)
        val obbList: List<Request> = RequestBuilder
            .buildObbRequestList(context, app, deliveryData)
        val requestList: MutableList<Request> = ArrayList<Request>()
        requestList.add(request)
        requestList.addAll(splitList)
        requestList.addAll(obbList)
        fetchListener = getFetchListener()
        fetch.addListener(fetchListener)
        fetch.enqueue(
            requestList,
            { updatedRequestList -> Log.i("Downloading Splits : %s", app.getPackageName()) })

        //Add <PackageName,DisplayName> and <PackageName,IconURL> to PseudoMaps
        PackageUtil.addToPseudoPackageMap(context, app.getPackageName(), app.getDisplayName())
        PackageUtil.addToPseudoURLMap(context, app.getPackageName(), app.getIconUrl())
    }

    private fun getFetchListener(): FetchListener {
        return object : AbstractFetchGroupListener() {
            fun onQueued(
                groupId: Int,
                download: Download,
                waitingNetwork: Boolean,
                fetchGroup: FetchGroup
            ) {
                if (groupId == hashCode) {
                    ContextUtil.runOnUiThread({
                        progressBar!!.isIndeterminate = true
                        progressStatus.setText(R.string.download_queued)
                    })
                    notification.notifyQueued()
                }
            }

            fun onStarted(
                groupId: Int,
                download: Download,
                downloadBlocks: List<DownloadBlock?>,
                totalBlocks: Int,
                fetchGroup: FetchGroup
            ) {
                if (groupId == hashCode) {
                    ContextUtil.runOnUiThread({
                        progressBar!!.isIndeterminate = true
                        progressStatus.setText(R.string.download_waiting)
                        switchViews(true)
                    })
                }
            }

            fun onResumed(groupId: Int, download: Download, fetchGroup: FetchGroup) {
                if (groupId == hashCode) {
                    progress = fetchGroup.getGroupDownloadProgress()
                    if (progress < 0) progress = 0
                    notification.notifyProgress(progress, 0, hashCode)
                    ContextUtil.runOnUiThread({
                        progressStatus.setText(R.string.download_progress)
                        progressBar!!.isIndeterminate = false
                    })
                }
            }

            fun onProgress(
                groupId: Int,
                download: Download,
                etaInMilliSeconds: Long,
                downloadedBytesPerSecond: Long,
                fetchGroup: FetchGroup
            ) {
                if (groupId == hashCode) {
                    progress = fetchGroup.getGroupDownloadProgress()
                    if (progress < 0) progress = 0
                    ContextUtil.runOnUiThread({
                        btnCancel!!.visibility = View.VISIBLE
                        //Set intermediate to false, just in case xD
                        if (progressBar!!.isIndeterminate) progressBar!!.isIndeterminate = false
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            progressBar!!.setProgress(progress, true)
                        } else progressBar!!.progress = progress
                        progressStatus.setText(R.string.download_progress)
                        progressTxt!!.text = StringBuilder().append(progress).append("%")
                    })
                    notification.notifyProgress(progress, downloadedBytesPerSecond, hashCode)
                }
            }

            fun onPaused(groupId: Int, download: Download, fetchGroup: FetchGroup) {
                if (groupId == hashCode) {
                    notification.notifyResume(hashCode)
                    ContextUtil.runOnUiThread({
                        switchViews(false)
                        progressStatus.setText(R.string.download_paused)
                    })
                }
            }

            fun onError(
                groupId: Int,
                download: Download,
                error: Error,
                throwable: Throwable?,
                fetchGroup: FetchGroup
            ) {
                if (groupId == hashCode) {
                    notification.notifyFailed()
                }
            }

            fun onCompleted(groupId: Int, download: Download, fetchGroup: FetchGroup) {
                if (groupId == hashCode && fetchGroup.getGroupDownloadProgress() === 100) {
                    notification.notifyCompleted()
                    ContextUtil.runOnUiThread({
                        switchViews(false)
                        progressStatus.setText(R.string.download_completed)
                        btnPositive!!.setOnClickListener(installAppListener())
                    })
                    if (Util.shouldAutoInstallApk(context)) {
                        ContextUtil.runOnUiThread({
                            btnPositive.setText(R.string.details_installing)
                            btnPositive!!.isEnabled = false
                        })
                        notification.notifyInstalling()
                        //Call the installer
                        AuroraApplication.getInstaller().install(app)
                    }
                    if (fetchListener != null) {
                        fetch.removeListener(fetchListener)
                        fetchListener = null
                    }
                }
                if (groupId == hashCode && download.getProgress() === 100) {
                    if (FilenameUtils.getExtension(download.getFile()) == "gzip") {
                        compositeDisposable.add(
                            Observable.fromCallable(
                                Callable<T> {
                                    GZipTask(context)
                                        .extract(File(download.getFile()))
                                }
                            )
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnSubscribe(Consumer { disposable: Disposable? ->
                                    notification.notifyExtractionProgress()
                                    ContextUtil.runOnUiThread({ btnPositive!!.isEnabled = false })
                                })
                                .doOnTerminate(Action {
                                    ContextUtil.runOnUiThread({ btnPositive!!.isEnabled = true })
                                })
                                .subscribe(Consumer<T> { success: T -> if (success) notification.notifyExtractionFinished() else notification.notifyExtractionFailed() })
                        )
                    }
                }
            }

            fun onCancelled(
                groupId: Int, download: Download,
                fetchGroup: FetchGroup
            ) {
                if (groupId == hashCode) {
                    notification.notifyCancelled()
                    ContextUtil.runOnUiThread({
                        switchViews(false)
                        progressBar!!.isIndeterminate = true
                        progressStatus.setText(R.string.download_canceled)
                    })
                }
            }
        }
    }
    init {
        activity?.apply {
            btnPositive = findViewById(R.id.btn_positive)
            btnNegative = findViewById(R.id.btn_negative)
            viewSwitcher = findViewById(R.id.view_switcher_action)
            actions_layout = findViewById(R.id.view1)
            progress_layout = findViewById(R.id.view2)
            progressBar = findViewById(R.id.progress_download)
            progressTxt = findViewById(R.id.progress_txt)
            progressStatus = findViewById(R.id.progress_status)
            btnCancel = findViewById(R.id.btn_cancel)
        }
    }*/
}

