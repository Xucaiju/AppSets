package xcj.appsets.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.ColorUtils
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView
import com.skydoves.transformationlayout.TransformationCompat
import com.skydoves.transformationlayout.onTransformationStartContainer
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.search_view.*
import xcj.appsets.AppSetsApplication
import xcj.appsets.Constant
import xcj.appsets.R
import xcj.appsets.events.Event
import xcj.appsets.model.AppSetsLoginInfo
import xcj.appsets.service.ApiValidateService
import xcj.appsets.ui.fragment.*
import xcj.appsets.util.*


class MainActivity : BaseActivity() {
    private var fragmentCur: Int = 0
    private lateinit var navigationController: NavController
    lateinit var frameLayout1:FrameLayout
    lateinit var openCategoryActionHintText:MaterialTextView
    lateinit var miniFabActionHintText:MaterialTextView
    lateinit var miniFabAction:FloatingActionButton
    lateinit var openCategoryAction:AppCompatImageView
    lateinit var postTrendsAction:FloatingActionButton
    lateinit var postTrendsActionHintText:MaterialTextView
    override fun onDestroy() {
        disposable.clear()
        disposable.dispose()
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val bundle = intent?.extras
        if (bundle != null)
            fragmentCur = bundle.getInt(Constant.INTENT_FRAGMENT_POSITION)
        else if (intent?.scheme != null && intent.scheme == "favorite") {
            fragmentCur = 2
            if (intent.data != null)
                externalQuery = intent.data?.getQueryParameter("q")
        } else
            fragmentCur = getDefaultTab(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        onTransformationStartContainer()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (NetworkUtil.isConnected(this)) {
            if (isHomeAppCacheObsolete(this))
                clearHomeAppCache(this)
        }
        frameLayout1 = when_fab_clicked_show_framelayout
        openCategoryActionHintText = open_category_action_hint_text
        miniFabActionHintText = download_mini_fab_action_hint_text
        miniFabAction = download_mini_fab_action
        openCategoryAction = open_category_action
        postTrendsAction = post_trends_min_action
        postTrendsActionHintText = post_trends_action_hint_text
        disposable.add(Observable.fromCallable {
            bottomNavigation?.getOrCreateBadge(R.id.fragmentFavorite)?.number
        }.subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it?.apply {
                    if (this > 0) {
                        bottomNavigation?.getOrCreateBadge(R.id.fragmentFavorite)
                            ?.setVisible(true, true)
                    } else {
                        bottomNavigation?.getOrCreateBadge(R.id.fragmentFavorite)
                            ?.setVisible(false, true)
                    }
                }
            }) {}
        )
        fillSearchBarUserAvatar(this, searchbar_user_avatar)
        fragmentCur = getDefaultTab(this)
        val backGroundColor = ViewUtil.getStyledAttribute(this, android.R.attr.colorBackground)
        bottomNavigation?.setBackgroundColor(ColorUtils.setAlphaComponent(backGroundColor, 220))

        navigationController = findNavController(R.id.main_nav_host)

        bottomNavigation?.setOnNavigationItemSelectedListener {
            if (it.isChecked) {
                false
            } else {
                NavigationUI.onNavDestinationSelected(it, navigationController)
                true
            }

        }

        //Check correct BottomNavigation item, if navigation_main is done programmatically
        navigationController?.addOnDestinationChangedListener { _: NavController?, destination: NavDestination?, _: Bundle? ->
            val menu: Menu? = bottomNavigation?.menu
            menu?.size()?.let {
                for (i in 0 until it) {
                    val item = menu?.getItem(i)
                    if (matchDestination(destination, item.itemId)) {
                        item.isChecked = true
                    }
                }
            }

        }
        main_searchbar_search_icon?.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            TransformationCompat.startActivity(transformationLayout, intent)
        }
        search_edit_text?.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            TransformationCompat.startActivity(transformationLayout, intent)
        }
        miniFabAction.setOnClickListener {
            startActivity(Intent(this, DownloadManagerActivity::class.java))
        }
        frameLayout1?.setOnClickListener {
            it
                .animate()
                .alpha(0f)
                .setDuration(150)
                .withEndAction {
                    it.visibility = View.GONE
                }
                .start()
            openCategoryActionHintText
                .animate()
                .setDuration(150)
                .alpha(0f)
                .withEndAction {
                    open_category_action_hint_text.visibility = View.INVISIBLE
                }
                .start()
            miniFabActionHintText
                .animate()
                .setDuration(150)
                .translationY(24f)
                .alpha(0f)
                .withEndAction {
                    download_mini_fab_action_hint_text.visibility = View.GONE
                }
                .start()
            postTrendsActionHintText.animate()
                .setDuration(150)
                .translationY(90f)
                .alpha(0f)
                .withEndAction {
                    post_trends_action_hint_text.visibility = View.GONE
                }.start()
            openCategoryAction
                .animate()
                .setDuration(150)
                .rotation(0f)
                .start()
            postTrendsAction .animate()
                .setDuration(150)
                .translationY(112f)
                .scaleY(0.3f)
                .scaleX(0.3f)
                .alpha(0f)
                .setInterpolator(AccelerateInterpolator()).withEndAction {
                    postTrendsAction.visibility = View.GONE
                }
                .start()
            miniFabAction
                .animate()
                .setDuration(150)
                .translationY(64f)
                .scaleY(0.3f)
                .scaleX(0.3f)
                .alpha(0f)
                .setInterpolator(AccelerateInterpolator()).withEndAction {
                    miniFabAction.visibility = View.GONE
                }
                .start()

        }
        openCategoryAction?.setOnClickListener {
            it.animate().setDuration(150).rotation(-135f).start()

            miniFabAction?.apply {
                if (visibility==View.VISIBLE) {

                    val categoryDialogFragment =
                        CategorySelectBottomSheetDialogFragment()
                    categoryDialogFragment.show(supportFragmentManager, categoryDialogFragment.tag)
                } else {
                        val fragments = supportFragmentManager.fragments
                        for (fragment in fragments) {
                            fragment.view?.isClickable = fragment !is FragmentRecommend
                            fragment.view?.isClickable = fragment !is FragmentGoogleApps
                            fragment.view?.isClickable = fragment !is FragmentMine
                        }

                     miniFabActionHintText
                        .animate()
                        .setDuration(150)
                        .translationY(0f)
                        .alpha(1f)
                        .withStartAction {
                            miniFabActionHintText.visibility = View.VISIBLE
                        }
                        .start()
                    openCategoryActionHintText
                        .animate()
                        .setDuration(150)
                        .alpha(1f)
                        .withStartAction{
                            openCategoryActionHintText.visibility = View.VISIBLE
                        }
                        .start()
                    postTrendsActionHintText .animate()
                        .setDuration(150)
                        .alpha(1f)
                        .translationY(0f)
                        .withStartAction{
                            postTrendsActionHintText.visibility = View.VISIBLE
                        }
                        .start()
                    frameLayout1?.let {l->
                        l.animate()
                        .alpha(1f)
                        .setDuration(150)
                            .withStartAction {
                                l.visibility = View.VISIBLE
                            }
                        .start()
                    }
                    postTrendsAction.animate()
                        .setDuration(150)
                        .translationY(0f)
                        .scaleY(1f)
                        .scaleX(1f)
                        .alpha(1f)
                        .withStartAction {
                            postTrendsAction.visibility = View.VISIBLE
                        }
                        .setInterpolator(DecelerateInterpolator())
                        .withEndAction {
                            clearAnimation()
                        }
                        .start()
                    animate()
                        .setDuration(150)
                        .translationY(0f)
                        .scaleY(1f)
                        .scaleX(1f)
                        .alpha(1f)
                        .withStartAction {
                            visibility = View.VISIBLE
                        }
                        .setInterpolator(DecelerateInterpolator())
                        .withEndAction {
                            clearAnimation()
                        }
                        .start()
                }
            }
        }

        /*when (fragmentCur) {
            0 -> navigationController.navigate(R.id.fragmentRecommend)
            1 ->{
                navigationController.navigate(R.id.fragmentGoogleApps)
            }
            2 -> navigationController.navigate(R.id.fragmentMine)
        }*/

        AppSetsApplication.getRxBus()?.getBus()?.subscribe { event ->
            when (event?.getSubType()) {
                Event.SubType.NETWORK_AVAILABLE -> {
                        Log.d("Event类型", "NETWORK_AVAILABLE")
                    if (ApiValidateService.isServiceRunning) {
                        Log.d("API验证服务状态", "正在运行")
                        stopApiValidateService()
                    } else {
                        Log.d("API验证服务状态", "没有运行")
                    }


                    if (Accountant.isLoggedIn(this)!!) {
                        Log.d("GoogleAPI登录状态", "已登录")
                        validateApi(this)
                    } else {
                        Log.d("GoogleAPI登录状态", "未登录")
                    }

                }

                Event.SubType.NETWORK_UNAVAILABLE -> {
                    Log.d("Event类型", "NETWORK_UNAVAILABLE")
                    Toast.makeText(this, "无法连接到Google Play", Toast.LENGTH_SHORT).show()
                }

                Event.SubType.API_SUCCESS -> {
                    DialogFragment.eventType = Event.SubType.API_SUCCESS
                        Log.d("Event类型", "API_SUCCESS")

                }


                Event.SubType.API_FAILED, Event.SubType.API_ERROR -> {
                    Log.d("Event类型", "API_FAILED")
                }

                else -> {
                    // do
                }
            }
        }?.let {
            disposable.add(it)
        }


    }


    private fun stopApiValidateService() {
        stopService(Intent(this, ApiValidateService::class.java))
    }

    fun myProfiles(view: View) {
        val userProfileBottomSheetDailogFragment = UserProfileBottomSheetDailogFragment()
        userProfileBottomSheetDailogFragment.show(
            supportFragmentManager,
            userProfileBottomSheetDailogFragment.tag
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        return false
    }


    companion object {
        @JvmField
        var externalQuery: String? = null

        @JvmStatic
        fun matchDestination(destination: NavDestination?, @IdRes destId: Int): Boolean {
            var currentDestination: NavDestination? = destination
            while (currentDestination?.id != destId && currentDestination?.parent != null) {
                currentDestination = currentDestination.parent
            }
            return currentDestination?.id == destId
        }

        @JvmStatic
        fun fillSearchBarUserAvatar(context: Context?, userAvatar: AppCompatImageView?) {
            if (context != null) {
                AppSetsLoginInfo.getSavedInstance(context).also {
                    if (userAvatar != null) {
                        Glide
                            .with(context)
                            .asBitmap()
                            .load(it?.avatar)
                            .error(R.drawable.avatar)
                            .transition(BitmapTransitionOptions().crossFade(100))
                            .transform(CenterCrop(), RoundedCorners(250))
                            .into(userAvatar)
                    }
                }
            }
        }
    }
}


