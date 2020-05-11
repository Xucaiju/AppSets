package xcj.appsets.ui.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.reflect.TypeToken
import com.skydoves.transformationlayout.TransformationCompat
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_today.*
import kotlinx.android.synthetic.main.include_today_app_card.*
import kotlinx.android.synthetic.main.search_view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import xcj.appsets.AlipayDemoActivity
import xcj.appsets.AppSetsApplication.Companion.getAppSetsDB
import xcj.appsets.Constant
import xcj.appsets.R
import xcj.appsets.TestChartActivity
import xcj.appsets.database.repository.AppSetsTodayAppRepository
import xcj.appsets.database.repository.AppSetsUserFavoriteAppsRepository
import xcj.appsets.extendedclass.DownloadAppSetsAppDialogFragment
import xcj.appsets.extendedclass.UserPayAppBottomSheetDailogFragment
import xcj.appsets.manager.LocaleManager
import xcj.appsets.model.AppSetsLoginInfo
import xcj.appsets.model.TodayApp
import xcj.appsets.server.AppSetsServer
import xcj.appsets.server.AppSetsServer.Companion.gson
import xcj.appsets.service.ApiValidateService
import xcj.appsets.ui.TimeLineActivity
import xcj.appsets.ui.TodayDetailsActivity
import xcj.appsets.util.DensityUtil
import xcj.appsets.util.PackageUtil
import xcj.appsets.util.PreferenceUtil
import xcj.appsets.util.setCacheCreateTime
import xcj.appsets.viewmodel.TodayAppViewModel
import xcj.appsets.viewmodel.TodayAppViewModelUpdate
import java.util.*
import kotlin.concurrent.thread

class FragmentToday : Fragment() {

    private lateinit var todayAppViewModelUpdate: TodayAppViewModelUpdate
    val appSetsUserFavoriteAppsRepository = getAppSetsDB()?.appSetsUserFavoriteAppsDao()?.let {
        AppSetsUserFavoriteAppsRepository(
            it
        )
    }
    val appSetsTodayAppRepository = getAppSetsDB()?.todayAppDao()?.let {
        AppSetsTodayAppRepository(it)
    }

    var isInstalled = false

    private var disposable = CompositeDisposable()
    override fun onStart() {
        super.onStart()
        blurKitLayout.startBlur()
        blurKitLayout.fps = 60
        blurKitLayout.lockView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_today, container, false)
    }
    fun fillTodayFramgmentDataUpdate(todayApp: TodayApp){
        todayApp.let {
            todayAppViewModelUpdate.getIsFavorite(it)
            isInstalled = PackageUtil.isInstalled(requireContext(), it.appPackageName)
            fragmentTodayBuyIcon?.setImageResource(
                if(isInstalled){
                    R.drawable.ic_toys_black_24dp
                }else if(it.appPrice?:0.00 >0.00){
                    R.drawable.ic_local_mall_black_24dp
                }else{
                    R.drawable.ic_toys_black_24dp
                })
            fragmentTodayBuyText?.text = if(isInstalled||it.appPrice==0.00){
                getString(R.string.please_enjoy)
            }else{
                getString(R.string.purchase)
            }
            fragmentTodayBuyContent?.text = it.editorNote



            fragmentTodayBuyAction1?.apply {
                text = if(isInstalled||it.appPrice==0.00){
                    String.format(getString(R.string.open_appname), it.appDisplayname )
                }else{
                    getString(R.string.start_planning)
                }
                setOnClickListener {v->
                    if(isInstalled){
                        startApp(it.appPackageName)
                    }else if(it.appPrice?:0.00>0.00){
                        val userPayAppBottomSheetDailogFragment = UserPayAppBottomSheetDailogFragment(todayApp)
                        userPayAppBottomSheetDailogFragment.show(this@FragmentToday.parentFragmentManager,userPayAppBottomSheetDailogFragment.tag)
                    }else{
                        Toast.makeText(this@FragmentToday.requireContext(),String.format("请先下载 %s", it.appDisplayname),Toast.LENGTH_SHORT).show()
                        val downloadAppSetsAppDialogFragment = DownloadAppSetsAppDialogFragment(it)
                        downloadAppSetsAppDialogFragment.isCancelable = false
                        downloadAppSetsAppDialogFragment.show(requireActivity().supportFragmentManager,downloadAppSetsAppDialogFragment.tag)
                    }
                }
            }

            val searchBarTextView = requireActivity().search_edit_text
            object : CountDownTimer(300, 100) {
                override fun onFinish() {
                    searchBarTextView?.apply {
                        animate().setDuration(350).scaleX(.2f).scaleY(.0f).alpha(0f)
                            .setInterpolator(
                                DecelerateInterpolator()
                            ).start()

                    }
                    object : CountDownTimer(500, 500) {
                        override fun onFinish() {
                            searchBarTextView?.apply {

                                text = context?.getString(R.string.main_search_bar_text)?.let { it1 ->
                                    String.format(
                                        it1,
                                        it.appDisplayname
                                    )
                                }
                                //val set = AnimationSet(false)
                                // set.addAnimation()
                                animate().setDuration(0).scaleX(1f).scaleY(1f).start()
                                animate().setDuration(100).alpha(1f).setStartDelay(100).start()
                            }
                        }

                        override fun onTick(millisUntilFinished: Long) {}
                    }.start()
                }

                override fun onTick(millisUntilFinished: Long) {}
            }.start()
            fragmentTodayAppDisplayName?.text = it.appDisplayname
            fragmentTodayAppFeaturesTextView?.text = it.appFeatures
            fragmentToday_app_type_chip_group.apply {
                removeAllViews()
                val priceChip = Chip(this.context).apply {
                    tag = "today_app_price_chip"
                    text = if (it.appPrice?.toDouble() != 0.00) {
                        val price =
                            it.appPrice?.toDouble()?.let { it1 -> conversionPrice(it1,requireContext()) }
                        "${getString(R.string.app_price)}: $price ${getString(R.string.price_symbol)}"
                    } else {
                        getString(R.string.free)
                    }
                    chipStrokeWidth = DensityUtil.dip2px(requireContext(), 1f)
                    chipStrokeColor = getRandomColorStateList(requireContext())
                    chipBackgroundColor = ColorStateList.valueOf(android.R.attr.colorBackground)
                }
                val favoritesChip = Chip(this.context).apply {
                    tag = "today_app_favorites_chip"
                    text =
                        String.format(getString(R.string.app_favorites_times), it.favorites)
                    chipStrokeWidth = DensityUtil.dip2px(requireContext(), 1f)
                    chipStrokeColor = getRandomColorStateList(requireContext())
                    chipBackgroundColor = ColorStateList.valueOf(android.R.attr.colorBackground)
                }
                addView(priceChip)
                addView(favoritesChip)
                it.appTypes?.split("type_")?.let {it1->
                    for (type in it1) {
                        if (type.isEmpty())
                            continue
                        val chip = Chip(this.context).apply {
                            tag = "type_${type}"
                            text = translate(type, requireContext())
                            chipStrokeWidth = DensityUtil.dip2px(requireContext(), 1f)
                            chipStrokeColor = getRandomColorStateList(requireContext())
                            chipBackgroundColor =
                                ColorStateList.valueOf(android.R.attr.colorBackground)
                        }
                        addView(chip)
                    }
                }
            }
            include3.visibility = View.VISIBLE
            today_choose_pill_today.visibility = View.VISIBLE
            timeline_transformationLayout.visibility = View.VISIBLE
            fragmentTodayBuyCard.visibility = View.VISIBLE
            Glide
                .with(requireContext())
                .asBitmap()
                .load(it.appIcon)
                .placeholder(R.color.colorTransparent)
                .transition(BitmapTransitionOptions().crossFade())
                .transform(CenterCrop(), RoundedCorners(250))
                .into(fragmentTodayAppIconView)
            Glide
                .with(requireContext())
                .asBitmap()
                .load(it.appRecommendedPictureA)
                .placeholder(R.color.colorTransparent)
                .transition(BitmapTransitionOptions().crossFade())
                .transform(CenterCrop())
                .into(fragmentTodayAppRecommentedPicture)
        }

    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

       /* val viewmodel by viewModels<TodayAppViewModelUpdate>()
        todayAppViewModelUpdate = viewmodel
        todayAppViewModelUpdate.app?.observe(viewLifecycleOwner){todayApp->
            todayApp?.let {
                fillTodayFramgmentDataUpdate(it)

            }
        }
        todayAppViewModelUpdate.isFavorited?.observe(viewLifecycleOwner){
            it?.let {
                setFavoriteButton(it)

            }





        }*/
        val todayAppModel by viewModels<TodayAppViewModel>()

        todayAppModel.getTodayAppData().observe(viewLifecycleOwner) {
            fillTodayFramgmentData(it)

        }
        open_alipay_demo_action.setOnClickListener{
            startActivity(Intent(requireContext(), AlipayDemoActivity::class.java))
        }


        /**
         *
         *
         * update-->直接更新本地DB, 然后 TodayAppViewModel 从DB中获取为 LiveData, 最后呈现到 UI
         *
         *
         **/









         /**
         *
         * 1. 先更新 TodayAppModel 的 LiveData, 由 LiveData 自动更新 UI
         *
         * 2. 然后再更新本地 Catch
         *
         * 3. 最后更新服务器数据
         */
        fragmentToday_favorite_button.setOnClickListener {
            var mark = false
            var times = 0
            val key = Constant.FAVORITE_APPSETS
            val jsonString = PreferenceUtil.getString(requireContext(), key)
            val appData = gson.fromJson<MutableList<TodayApp>>(
                jsonString,
                object : TypeToken<MutableList<TodayApp>>() {}.type
            )
            appData?.let {
                for (app in appData) {
                    if (app.id == todayAppModel.getTodayAppData().value?.id) {
                        mark = true
                        times++
                        continue
                    } else {
                        continue
                    }
                }
                Log.d("Time", "$times")
                if (mark) {//已收藏就取消
                    requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
                        .getOrCreateBadge(R.id.fragmentFavorite).apply {
                           setVisible(false, true)
                            --number

                        }
                    fragmentToday_favorite_button.text = getString(R.string.uncollect)
                    // step 1
                    todayAppModel.setUpTodayAppFavoriteTimes(0)
                    requireActivity().findViewById<ChipGroup>(R.id.fragmentToday_app_type_chip_group)
                        ?.findViewWithTag<Chip>("today_app_favorites_chip")?.apply {
                            text = String.format(
                                getString(R.string.app_favorites_times),
                                todayAppModel.getTodayAppData().value?.favorites
                            )
                        }


                    // step 2
                    updateLoggedUserFavoriteAppInCatch(
                        0,
                        todayAppModel.getTodayAppData().value,
                        requireContext()
                    )
                    //fragmentToday_favorite_button.setBackgroundColor(requireContext().getColor(R.color.colorGray))

                    fragmentToday_favorite_button.text = getString(R.string.collect_it)

                } else {
                    requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
                        .getOrCreateBadge(R.id.fragmentFavorite).apply {
                            ++number
                            setVisible(true, true)
                        }
                    fragmentToday_favorite_button.text = getString(R.string.collect_it)
                    // step 1
                    todayAppModel.setUpTodayAppFavoriteTimes(1)
                    requireActivity().findViewById<ChipGroup>(R.id.fragmentToday_app_type_chip_group)
                        ?.findViewWithTag<Chip>("today_app_favorites_chip")?.apply {
                            text = String.format(
                                getString(R.string.app_favorites_times),
                                todayAppModel.getTodayAppData().value?.favorites
                            )
                        }


                    // step 2
                    updateLoggedUserFavoriteAppInCatch(
                        1,
                        todayAppModel.getTodayAppData().value,
                        requireContext()
                    )

                    fragmentToday_favorite_button.text = getString(R.string.uncollect)
                }
            }

        }
        fragmentToday_swip_refresh.setColorSchemeResources(R.color.colorGoogleLightBlue, R.color.colorGoogleLigntRed, R.color.colorGoogleLightGreen, R.color.colorGoogleLightYellow)
        fragmentToday_swip_refresh.setOnRefreshListener {

            CoroutineScope(IO).launch{
                delay(3000)

            }
            requireActivity().include.animate()?.alpha(0f)?.setDuration(450)?.withEndAction {
                requireActivity().include.animate()?.alpha(1f)?.setDuration(450)?.start()
            }?.start()
            this.fragmentToday_nestscrollview?.animate()?.translationY(300f)?.setDuration(600)?.setInterpolator(DecelerateInterpolator())?.withEndAction {
                this.fragmentToday_nestscrollview?.animate()?.translationY(0f)?.setDuration(400)?.setInterpolator(DecelerateInterpolator())?.setStartDelay(100)?.start()
            }?.start()
            todayAppModel.getTodayAppDataForceViaServer{
                fragmentToday_swip_refresh
            }




        }
        fragmentTodayFeatureCard.setOnLongClickListener {
            /* showBottomSheetDialog(todayAppModel.getTodayAppData().value)
             true*/
            false
        }
        fragmentTodayFeatureCard.setOnClickListener {
            val intent = Intent(requireContext(), TodayDetailsActivity::class.java)
            intent.putExtra("app_from", "appsets")
            intent.putExtra("today_app", todayAppModel.getTodayAppData().value)
            TransformationCompat.startActivity(fragmentTodayFeaturedCardtranformationLayout, intent)
        }
        today_choose_pill_timeline.setOnClickListener(openTimelineActivity())
        timeline_logo.setOnClickListener(openTimelineActivity())

        today_choose_pill_today.setOnClickListener {
            startActivity(Intent(requireContext(), TestChartActivity::class.java))
        }

    }

    private fun setFavoriteButton(it: Int) {

        fragmentToday_favorite_button?.apply {
            text = if(it>0)
                getString(R.string.uncollect)
            else
                getString(R.string.collect_it)

            setOnClickListener{v->

                //first 判断用户时候已经收藏
                CoroutineScope(IO).launch {
                    if(it>0){
                        todayAppViewModelUpdate.app?.value?.apply {
                            favorites = favorites?.minus(1)
                            appSetsTodayAppRepository?.updateTodayApp(this)
                            todayAppViewModelUpdate.getTodayAppFromDB()
                        }

                    }else{
                        todayAppViewModelUpdate.app?.value?.apply {
                            favorites = favorites?.plus(1)
                            appSetsTodayAppRepository?.updateTodayApp(this)
                            todayAppViewModelUpdate.getTodayAppFromDB()
                        }
                    }

                }

            }
        }
    }

    private fun openTimelineActivity():View.OnClickListener{
        return View.OnClickListener {
            val intent = Intent(requireContext(), TimeLineActivity::class.java)
            TransformationCompat.startActivity(timeline_transformationLayout, intent)
        }

    }
    private fun fillTodayFramgmentData(todayApp: TodayApp?) {

        var mark = false
        var times = 0
        val key = Constant.FAVORITE_APPSETS
        val jsonString = PreferenceUtil.getString(requireContext(), key)
        val appData = gson.fromJson<MutableList<TodayApp>>(
            jsonString,
            object : TypeToken<MutableList<TodayApp>>() {}.type
        )
        todayApp?.let {
            isInstalled = PackageUtil.isInstalled(requireContext(), it.appPackageName)
            it.appPrice?.let { price ->




                fragmentTodayBuyIcon?.setImageResource(
                    if(isInstalled) {
                        R.drawable.ic_toys_black_24dp
                    }else if(price>0){
                        R.drawable.ic_local_mall_black_24dp
                    }else{
                        R.drawable.ic_toys_black_24dp
                    }
                )
                fragmentTodayBuyText?.text = if(isInstalled||price==0.00){
                    getString(R.string.please_enjoy)
                }else{
                    getString(R.string.purchase)
                }
                fragmentTodayBuyContent?.text = it.editorNote

                fragmentTodayBuyAction1?.apply {
                    text = if(isInstalled||price==0.00){
                        String.format(getString(R.string.open_appname), it.appDisplayname )
                    }else{
                        getString(R.string.start_planning)
                    }
                    setOnClickListener {v->
                        if(isInstalled){
                            startApp(it.appPackageName)
                        }else if(price>0.00){
                            val userPayAppBottomSheetDailogFragment = UserPayAppBottomSheetDailogFragment(todayApp)
                            userPayAppBottomSheetDailogFragment.show(this@FragmentToday.parentFragmentManager,userPayAppBottomSheetDailogFragment.tag)
                        }else{
                            Toast.makeText(this@FragmentToday.requireContext(),String.format("请先下载 %s", it.appDisplayname),Toast.LENGTH_SHORT).show()
                            val downloadAppSetsAppDialogFragment = DownloadAppSetsAppDialogFragment(it)
                            downloadAppSetsAppDialogFragment.isCancelable = false
                            downloadAppSetsAppDialogFragment.show(requireActivity().supportFragmentManager,downloadAppSetsAppDialogFragment.tag)
                        }
                    }
                }
            }

            val appTypes = it.appTypes?.split("type_")
            appData?.let { list ->
                for (app in list) {
                    if (app.id == it.id) {
                        mark = true
                        times++
                        continue
                    } else {
                        continue
                    }
                }
            }

            val searchBarTextView = requireActivity().search_edit_text
            object : CountDownTimer(300, 100) {
                override fun onFinish() {
                    searchBarTextView?.apply {
                        animate().setDuration(350).scaleX(.2f).scaleY(.0f).alpha(0f)
                            .setInterpolator(
                                DecelerateInterpolator()
                            ).start()

                    }
                    object : CountDownTimer(500, 500) {
                        override fun onFinish() {
                            searchBarTextView?.apply {

                                text = context?.getString(R.string.main_search_bar_text)?.let { it1 ->
                                    String.format(
                                        it1,
                                        it.appDisplayname
                                    )
                                }
                                //val set = AnimationSet(false)
                                // set.addAnimation()
                                animate().setDuration(0).scaleX(1f).scaleY(1f).start()
                                animate().setDuration(100).alpha(1f).setStartDelay(100).start()
                            }
                        }

                        override fun onTick(millisUntilFinished: Long) {}
                    }.start()
                }

                override fun onTick(millisUntilFinished: Long) {}
            }.start()


            fragmentTodayAppDisplayName?.text = it.appDisplayname
            fragmentTodayAppFeaturesTextView?.text = it.appFeatures

            if (mark) {
                fragmentToday_favorite_button.text = getString(R.string.uncollect)
            } else {
                fragmentToday_favorite_button.text = getString(R.string.collect_it)
            }
            fragmentToday_app_type_chip_group.apply {
                removeAllViews()
                val priceChip = Chip(this.context).apply {
                    tag = "today_app_price_chip"
                    text = if (it.appPrice?.toDouble() != 0.00) {
                        val price =
                            it.appPrice?.toDouble()?.let { it1 -> conversionPrice(it1,requireContext()) }
                        "${getString(R.string.app_price)}: $price ${getString(R.string.price_symbol)}"
                    } else {
                        getString(R.string.free)
                    }
                    chipStrokeWidth = DensityUtil.dip2px(requireContext(), 1f)
                    chipStrokeColor = getRandomColorStateList(requireContext())
                    chipBackgroundColor = ColorStateList.valueOf(android.R.attr.colorBackground)
                }
                val favoritesChip = Chip(this.context).apply {
                    tag = "today_app_favorites_chip"
                    text =
                        String.format(getString(R.string.app_favorites_times), it.favorites)
                    chipStrokeWidth = DensityUtil.dip2px(requireContext(), 1f)
                    chipStrokeColor = getRandomColorStateList(requireContext())
                    chipBackgroundColor = ColorStateList.valueOf(android.R.attr.colorBackground)
                }

                addView(priceChip)
                addView(favoritesChip)
                if (appTypes != null) {
                    for (type in appTypes) {
                        if (type.isEmpty())
                            continue
                        val chip = Chip(this.context).apply {
                            tag = "type_${type}"
                            text = translate(type, requireContext())
                            chipStrokeWidth = DensityUtil.dip2px(requireContext(), 1f)
                            chipStrokeColor = getRandomColorStateList(requireContext())
                            chipBackgroundColor =
                                ColorStateList.valueOf(android.R.attr.colorBackground)
                        }
                        addView(chip)
                    }
                }
            }
            include3.visibility = View.VISIBLE
            today_choose_pill_today.visibility = View.VISIBLE
            timeline_transformationLayout.visibility = View.VISIBLE
            fragmentTodayBuyCard.visibility = View.VISIBLE
            Glide
                .with(requireContext())
                .asBitmap()
                .load(it.appIcon)
                .placeholder(R.color.colorTransparent)
                .transition(BitmapTransitionOptions().crossFade())
                .transform(CenterCrop(), RoundedCorners(250))
                .into(fragmentTodayAppIconView)
            Glide
                .with(requireContext())
                .asBitmap()
                .load(it.appRecommendedPictureA)
                .placeholder(R.color.colorTransparent)
                .transition(BitmapTransitionOptions().crossFade())
                .transform(CenterCrop())
                .into(fragmentTodayAppRecommentedPicture)
        }
    }

    fun startApp(packagename:String){
        val resolveIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            setPackage(packagename)
        }


        val apps:List<ResolveInfo> = requireActivity().packageManager.queryIntentActivities(resolveIntent, 0)
        var ri:ResolveInfo? = null
        val appsIterator = apps.iterator()
        if(appsIterator.hasNext()){
            ri = appsIterator.next()
        }

        if (ri != null ) {
            val packageName = ri.activityInfo.packageName
            val className = ri.activityInfo.name

            val  intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)

            val cn = ComponentName(packageName, className)

            intent.component = cn
            startActivity(intent)
        }
    }
    private fun stopApiValidateService() {
        requireActivity().stopService(Intent(requireContext(), ApiValidateService::class.java))
    }

    override fun onStop() {
        blurKitLayout.pauseBlur()
        super.onStop()
    }

    override fun onDestroyView() {
        disposable.clear()
        disposable.dispose()
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(
            "Fragment栈的大小为", "[${
            requireActivity().supportFragmentManager.backStackEntryCount
            }]"
        )

    }

    companion object {
        val todayDisposable = CompositeDisposable()
        @JvmStatic
        fun conversionPrice(price:Double, requireContext: Context):Double?{

            return when(LocaleManager(requireContext).locale){
                Locale.CHINA->{price}
                Locale.ENGLISH->{price/6.8}
                Locale.FRENCH->{price/8.8}
                Locale.GERMANY->{price/7.6}
                Locale.JAPAN->{price*1000}
                Locale.KOREA->{price*10000}
                else->{price}
            }

        }
        @JvmStatic
        fun updateLoggedUserFavoriteAppInCatch(
            operationTpe: Int,
            todayApp: TodayApp?,
            context: Context?
        ) {
            val loggedUser = context?.let { AppSetsLoginInfo.getSavedInstance(it) }
            val key = Constant.FAVORITE_APPSETS
            val jsonString = context?.let { PreferenceUtil.getString(it, key) }
            var appData = gson.fromJson<MutableList<TodayApp>>(
                jsonString,
                object : TypeToken<MutableList<TodayApp>>() {}.type
            )
            when (operationTpe) {
                0 -> {//取消收藏
                    //传递用户account和today appid 更新 appsets_today_favorite_apps表
                    //同时today_app表也要更新
                    for (app in appData) {
                        if (app.id == todayApp?.id) {
                            Log.d("是否存在", "是")
                            appData.remove(app)
                            break
                        } else {
                            Log.d("是否存在", "否")
                            continue
                        }
                    }
                    context?.let {
                        PreferenceUtil.putString(context, key, gson.toJson(appData))
                        setCacheCreateTime(
                            context,
                            Calendar.getInstance().timeInMillis,
                            "Favorite"
                        )
                    }


                    //step 3
                    //updateLoggedUserFavoriteAppInServer(loggedUser,todayApp,operationTpe)

                }
                1 -> {//添加收藏
                    if (todayApp != null)
                        if(appData==null){
                            appData = mutableListOf(todayApp)
                        }else{
                            appData.add(todayApp)
                        }
                    context?.let {
                        PreferenceUtil.putString(it, key, gson.toJson(appData))
                        setCacheCreateTime(
                            context,
                            Calendar.getInstance().timeInMillis,
                            "Favorite"
                        )
                    }


                    //step 3
                    //updateLoggedUserFavoriteAppInServer(loggedUser,todayApp,operationTpe)
                }
            }
        }

        @JvmStatic
        fun updateLoggedUserFavoriteAppInServer(
            loggedUser: AppSetsLoginInfo?,
            todayApp: TodayApp?,
            operationTpe: Int,
            context: Context?
        ) {
            thread(true) {
                todayDisposable.add(Observable.fromCallable {
                    context?.let {
                        AppSetsServer.updateTodayAppFavoriteTimes(
                            it,
                            loggedUser?.account,
                            loggedUser?.uic,
                            todayApp?.id,
                            operationTpe
                        )
                    }
                }.subscribe({
                    when (it) {
                        0 -> {
                            Log.d("更新AppSetsTodayFavoriteApps", "失败")
                        }
                        1 -> {
                            Log.d("更新AppSetsTodayFavoriteApps", "成功")

                        }
                    }
                }) {

                }
                )
                todayDisposable.add(Observable.fromCallable {
                    context?.let {
                        AppSetsServer.updateAppSetsTodayFavoriteApps(
                            it,
                            loggedUser?.account,
                            loggedUser?.uic,
                            todayApp?.id,
                            operationTpe
                        )
                    }
                }.subscribe({
                    when (it) {
                        0 -> {
                            Log.d("更新AppSetsTodayFavoriteApps", "失败")
                        }
                        1 -> {
                            Log.d("更新AppSetsTodayFavoriteApps", "成功")

                        }
                    }
                }) {

                })
            }
        }


        @JvmStatic
        fun translate(rawData: String?, context: Context?): String? {
            return when (rawData) {
                "music" -> context?.getString(R.string.type_music)
                "game" -> context?.getString(R.string.type_game)
                "tools" -> context?.getString(R.string.type_tools)
                "family" -> context?.getString(R.string.type_family)
                "knowledge" -> context?.getString(R.string.type_knowledge)
                "photography" -> context?.getString(R.string.type_photography)
                "shopping" -> context?.getString(R.string.type_shopping)
                "im" -> context?.getString(R.string.type_im)
                "news" -> context?.getString(R.string.type_news)
                "freetime" -> context?.getString(R.string.type_freetime)
                "healthy" -> context?.getString(R.string.type_healthy)
                "office" -> context?.getString(R.string.type_office)
                "parentchild" -> context?.getString(R.string.type_parentchild)
                else -> {
                    "others"
                }
            }
        }

        @JvmStatic
        fun getRandomColorStateList(context: Context?): ColorStateList? {
            val colorList: MutableList<ColorStateList?> = ArrayList()
            context?.let {
                colorList.add(it.getColorStateList(R.color.suggestion_color_selector_red))
                colorList.add(it.getColorStateList(R.color.suggestion_color_selector_blue))
                colorList.add(it.getColorStateList(R.color.suggestion_color_selector_green))
                colorList.add(it.getColorStateList(R.color.suggestion_color_selector_yellow))
            }
            return colorList.random()
        }
    }
}
