package xcj.appsets.ui

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.reflect.TypeToken
import com.skydoves.transformationlayout.onTransformationEndContainer
import kotlinx.android.synthetic.main.activity_today_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.lang3.StringUtils
import xcj.appsets.AppSetsApplication
import xcj.appsets.Constant.FAVORITE_GOOGLE_PLAY
import xcj.appsets.R
import xcj.appsets.adapter.AppSetsUserReviewAdapter
import xcj.appsets.adapter.PermissionAdapter
import xcj.appsets.adapter.SmallScreenShotAdapter
import xcj.appsets.extendedclass.UserReviewBottomSheetFragment
import xcj.appsets.model.App
import xcj.appsets.model.TodayApp
import xcj.appsets.server.AppSetsServer.Companion.gson
import xcj.appsets.task.AppDetailTask
import xcj.appsets.util.PreferenceUtil
import xcj.appsets.util.setCacheCreateTime
import xcj.appsets.viewmodel.UserReviewViewModel
import java.util.*


class TodayDetailsActivity : BaseActivity() {
    lateinit var reviewModel:UserReviewViewModel
    lateinit var userReviewAdapter:AppSetsUserReviewAdapter
    override fun onStart() {
        super.onStart()
        app_details_blurkitlayout.fps=60
        app_details_blurkitlayout.startBlur()
    }

    override fun onStop() {
        app_details_blurkitlayout.pauseBlur()

        super.onStop()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        onTransformationEndContainer(intent.getParcelableExtra("com.skydoves.transformationlayout"))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_today_details)
        userReviewAdapter = AppSetsUserReviewAdapter(this)
        today_app_user_review_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        today_app_user_review_recycler.adapter = userReviewAdapter

        val userReviewViewModel by viewModels<UserReviewViewModel>()
        reviewModel = userReviewViewModel
        userReviewViewModel.reviews?.observe(this) {
            it?.let {
                if(it.isEmpty()){
                    no_user_reviews.visibility = View.VISIBLE
                    today_app_user_review_recycler.visibility = View.GONE
                }
                else{
                    userReviewAdapter.setReviewList(it)
                    no_user_reviews.visibility = View.GONE
                    today_app_user_review_recycler.visibility = View.VISIBLE
                }
            }
        }

        fillContent()
        app_details_bottom_bar.setNavigationOnClickListener {
            onBackPressed()
        }
        app_details_bottom_fab.setOnClickListener{
            var shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "http://49.234.61.225/")
            shareIntent = Intent.createChooser(shareIntent, "Share it")
            startActivity(shareIntent)
        }

    }
    private fun fillContent() {

        val tempStr = "\t\t\t\t%s%s"

        if (intent.getStringExtra("app_from") == "google") {
            user_review_text_holder.visibility = View.GONE

            val appPackageName = intent.getStringExtra("app_packagename")
            var tempApp: App?=null
/*
            val appDetailsViewModel = this?.application?.let {
                ViewModelProvider.AndroidViewModelFactory.getInstance(it).create(AppDetailsViewModel::class.java)
            }
            appDetailsViewModel?.fetchAppDetails(appPackageName)
            appDetailsViewModel?.appDetails?.observe(this) {

                if (it != null) {
                    tempApp = it
                }
                Log.d("theApp", tempApp.toString())
                draw(tempApp)
            }
*/
            CoroutineScope(IO).launch{
                tempApp = AppSetsApplication.api?.let { AppDetailTask(this@TodayDetailsActivity, it).getInfo(appPackageName) }
                withContext(Main){
                    draw(tempApp)
                }
            }
            /*AsyncTask.execute {
                tempApp = AppSetsApplication.api?.let { AppDetailTask(this@TodayDetailsActivity, it).getInfo(appPackageName) }
               runOnUiThread( Runnable {
                         draw(tempApp)
                    }
               )}*/


        } else {


            val app = (intent.getSerializableExtra("today_app") as TodayApp)?.also {

                app_details_bottom_bar.menu.findItem(R.id.appbar_write_review).setOnMenuItemClickListener{_->
                    val dialog = UserReviewBottomSheetFragment(userReviewAdapter, it.id , reviewModel)
                    dialog.show(
                        supportFragmentManager,
                        dialog.tag
                    )
                    true
                }



                reviewModel.featchUserReviewsByAppId(it.id)
                it.appDisplayname?.let { name ->
                    app_details_app_displayname.text = name
                }
                if (it.appFullDescription?.contains('.')!!) {
                    val partSizeEn = it.appFullDescription?.split('.')
                    Log.d("SizeEn", "${partSizeEn?.size}")
                    today_app_full_description_part1.text =
                        String.format(tempStr, partSizeEn?.get(0) ?: "", '.')
                    today_app_full_description_part2.text =
                        String.format(tempStr, partSizeEn?.get(1) ?: "", '.')
                    var lastcontent = StringUtils.EMPTY
                    for (i in 2 until partSizeEn?.size!!) {
                        lastcontent += partSizeEn[i]
                    }

                    today_app_full_description_part3.text = if (lastcontent.isEmpty()) {
                        ""
                    } else {
                        String.format(tempStr, lastcontent, '.')
                    }

                }

                if (it.appFullDescription?.contains('。')!!) {
                    val partSizeZh = it.appFullDescription?.split('。')
                    Log.d("SizeZh", "${partSizeZh?.size}")
                    today_app_full_description_part1.text =
                        String.format(tempStr, partSizeZh?.get(0) ?: "", '。')
                    today_app_full_description_part2.text =
                        String.format(tempStr, partSizeZh?.get(1) ?: "", '。')
                    var lastcontent = StringUtils.EMPTY
                    for (i in 2 until partSizeZh?.size!!) {
                        lastcontent += partSizeZh[i]
                    }
                    today_app_full_description_part3.text = if (lastcontent.isEmpty()) {
                        ""
                    } else {
                        String.format(tempStr, lastcontent, '。')
                    }
                }
                it.appScreenshotA?.let { ssa ->
                    Glide
                        .with(this)
                        .asBitmap()
                        .load(ssa)
                        .placeholder(R.color.colorTransparent)
                        .transition(BitmapTransitionOptions().crossFade())
                        .transform(CenterCrop())
                        .into(app_details_screenshot_a)
                }
                it.appScreenshotB?.let { ssb ->
                    Glide
                        .with(this)
                        .asBitmap()
                        .load(ssb)
                        .placeholder(R.color.colorTransparent)
                        .transition(BitmapTransitionOptions().crossFade())
                        .transform(CenterCrop())
                        .into(app_details_screenshot_b)
                }
                it.appRecommendedPictureA?.let { pa ->
                    Glide
                        .with(this)
                        .asBitmap()
                        .load(pa)
                        .placeholder(R.color.colorTransparent)
                        .transition(BitmapTransitionOptions().crossFade())
                        .transform(CenterCrop())
                        .into(image_header)
                }
                it.appIcon?.let { appIcon ->
                    Glide
                        .with(this)
                        .asBitmap()
                        .load(appIcon)
                        .placeholder(R.color.colorTransparent)
                        .transition(BitmapTransitionOptions().crossFade())
                        .transform(CenterCrop(), RoundedCorners(250))
                        .into(app_details_app_icon)
                }
                today_app_full_description_part1.visibility = View.VISIBLE
                today_app_full_description_part2.visibility = View.VISIBLE
                today_app_full_description_part3.visibility = View.VISIBLE
                image_header_card.visibility = View.VISIBLE
                app_details_screenshot_card_a.visibility = View.VISIBLE
                app_details_screenshot_card_b.visibility = View.VISIBLE
            }
        }
    }
    private fun draw(app: App?){




        val tempStr = "\t\t\t\t%s%s"
        app?.also { it ->
            app_details_bottom_bar.menu.findItem(R.id.appbar_favorite).setOnMenuItemClickListener {menuItem->
                //val declaredMethod = MainActivity::class.java.getDeclaredMethod("findViewById", Int::class.java)
               // val bottomNavigationView = declaredMethod.invoke(null, R.id.bottomNavigation) as? BottomNavigationView
                var isFavorite = false
                val point = Point()
                (this.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getSize(point)
                val jsonString = PreferenceUtil.getString(this@TodayDetailsActivity, FAVORITE_GOOGLE_PLAY)
                var googlePlayAppFavoriteData = gson.fromJson<MutableList<App>>(jsonString, object : TypeToken<MutableList<App>>() {}.type)
                if(googlePlayAppFavoriteData!=null && googlePlayAppFavoriteData.isNotEmpty()){

                    for(app in googlePlayAppFavoriteData){
                        if(it.getPackageName()==app.getPackageName()){
                            isFavorite = true
                            break
                        }else
                            continue
                    }
                    if(!isFavorite) {
                        //animate favorite icon drawable


                        favorite_active_img.visibility = View.VISIBLE
                        favorite_active_img
                            .animate()
                            .scaleX(4f)
                            .scaleY(4f)
                            .translationX(point.x.toFloat()/2)
                            .translationY(-point.y.toFloat()/2)
                            .translationZ(2f)
                            .setDuration(450)
                            .setInterpolator(DecelerateInterpolator()).withEndAction {
                                favorite_active_img.animate().alpha(0f).setDuration(100).scaleX(1f).scaleY(1f).translationX(0f).translationY(0f).withEndAction {

                                    favorite_active_img.apply {
                                        visibility = View.GONE
                                        alpha = 1f
                                    }
                                }.start()
                            }
                            .start()
                        favorite_active_img.animate().alpha(0f).setDuration(100).setStartDelay(450).start()

                        Toast.makeText(this, "已收藏",Toast.LENGTH_SHORT).show()
                        googlePlayAppFavoriteData.add(it)
                        /*bottomNavigationView?.getOrCreateBadge(R.id.fragmentFavorite)?.apply{
                            ++number
                        }*/
                    }
                    else {
                        Toast.makeText(this, "已取消收藏",Toast.LENGTH_SHORT).show()

                        googlePlayAppFavoriteData.remove(app)

                       /* bottomNavigationView?.getOrCreateBadge(R.id.fragmentFavorite)?.apply{
                            --number
                        }*/

                    }
                }else{
                    favorite_active_img.visibility = View.VISIBLE
                    favorite_active_img
                        .animate()
                        .scaleX(4f)
                        .scaleY(4f)
                        .translationX(point.x.toFloat()/2)
                        .translationY(-point.y.toFloat()/2)
                        .translationZ(2f)
                        .setDuration(450)
                        .setInterpolator(DecelerateInterpolator()).withEndAction {
                            favorite_active_img.animate().alpha(0f).setDuration(100).scaleX(1f).scaleY(1f).translationX(0f).translationY(0f).withEndAction {
                                favorite_active_img.apply {
                                    visibility = View.GONE
                                    alpha = 1f
                                }
                            }.start()
                        }
                        .start()
                    Toast.makeText(this, "已收藏",Toast.LENGTH_SHORT).show()

                    googlePlayAppFavoriteData = mutableListOf(it)
                   /* bottomNavigationView?.getOrCreateBadge(R.id.fragmentFavorite)?.apply{
                        ++number
                    }*/

                }

               PreferenceUtil.putString(this@TodayDetailsActivity, FAVORITE_GOOGLE_PLAY,gson.toJson(googlePlayAppFavoriteData))
                setCacheCreateTime(this@TodayDetailsActivity, Calendar.getInstance().timeInMillis, "Favorite")
                true
            }
            google_app_details_screenshot_recycler.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
            google_app_details_permission_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

            it.getDisplayName()?.let { name ->
                app_details_app_displayname.text = name
            }
            it.getIconUrl()?.let { appIcon ->
                Glide
                    .with(this@TodayDetailsActivity)
                    .asBitmap()
                    .load(appIcon)
                    .placeholder(R.color.colorTransparent)
                    .transition(BitmapTransitionOptions().crossFade())
                    .transform(CenterCrop(), RoundedCorners(250))
                    .into(app_details_app_icon)
            }
            Log.d("ScreenUrls","${it.getScreenshotUrls()}")
            google_app_details_short_description.text  = it.getShortDescription()
            val screenShotAdapter = it.getScreenshotUrls()?.let { it1 ->
                   SmallScreenShotAdapter(it1, this)
               }
            screenShotAdapter?.notifyDataSetChanged()
            google_app_details_screenshot_recycler.adapter = screenShotAdapter


            val permissions = it.getPermissions()
            val permissionAdapter = PermissionAdapter(this, permissions)
            permissionAdapter.notifyDataSetChanged()
            google_app_details_permission_recycler.adapter = permissionAdapter

            /*var description:Spanned?=null
            it.getDescription()?.let { des->
               description = Html.fromHtml(
                des,
                Html.FROM_HTML_MODE_LEGACY
            )
            }*/

/*            val bool1:Boolean?= description?.contains('.')
            val bool2:Boolean? = description?.contains('。')
            if (bool1!=null&&bool1) {
                val partSizeEn = it.getDescription()?.split('.')
                Log.d("SizeEn", "${partSizeEn?.size}")
                today_app_full_description_part1.text =
                    String.format(tempStr, partSizeEn?.get(0) ?: "", '.')
                today_app_full_description_part2.text =
                    String.format(tempStr, partSizeEn?.get(1) ?: "", '.')
                var lastcontent = StringUtils.EMPTY
                for (i in 2 until partSizeEn?.size!!) {
                    lastcontent += partSizeEn[i]
                }

                today_app_full_description_part3.text = if (lastcontent.isEmpty()) {
                    ""
                } else {
                    String.format(tempStr, lastcontent, '.')
                }

            }

            if (bool2 !=null && bool2) {
                val partSizeZh = it.getDescription()?.split('。')
                Log.d("SizeZh", "${partSizeZh?.size}")
                today_app_full_description_part1.text =
                    String.format(tempStr, partSizeZh?.get(0) ?: "", '。')
                today_app_full_description_part2.text =
                    String.format(tempStr, partSizeZh?.get(1) ?: "", '。')
                var lastcontent = StringUtils.EMPTY
                for (i in 2 until partSizeZh?.size!!) {
                    lastcontent += partSizeZh[i]
                }
                today_app_full_description_part3.text = if (lastcontent.isEmpty()) {
                    ""
                } else {
                    String.format(tempStr, lastcontent, '。')
                }
            }*/





            google_app_details_short_description.visibility = View.VISIBLE
            google_app_details_screenshot_recycler.visibility = View.VISIBLE
            google_app_permission_card.visibility = View.VISIBLE



        }

    }
}
