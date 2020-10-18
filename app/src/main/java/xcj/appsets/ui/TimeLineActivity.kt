package xcj.appsets.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textview.MaterialTextView
import com.google.gson.reflect.TypeToken
import com.skydoves.transformationlayout.TransformationCompat
import com.skydoves.transformationlayout.onTransformationEndContainer
import kotlinx.android.synthetic.main.activity_time_line.*
import kotlinx.android.synthetic.main.include_today_app_card.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import xcj.appsets.Constant
import xcj.appsets.R
import xcj.appsets.model.TodayApp
import xcj.appsets.server.AppSetsServer.Companion.gson
import xcj.appsets.ui.fragment.FragmentRecommend
import xcj.appsets.ui.fragment.FragmentRecommend.Companion.translate
import xcj.appsets.ui.preference.SettingsActivity.Companion.getAllActivitys
import xcj.appsets.util.DensityUtil
import xcj.appsets.util.PreferenceUtil
import xcj.appsets.viewmodel.TimeLineAppModel
import xyz.sangcomz.stickytimelineview.RecyclerSectionItemDecoration
import xyz.sangcomz.stickytimelineview.model.SectionInfo
import java.util.*
import kotlin.collections.ArrayList

class TimeLineActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        onTransformationEndContainer(intent.getParcelableExtra("com.skydoves.transformationlayout"))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_line)
        val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        timeline_recycler.layoutManager = linearLayoutManager
        //val timeLineAppModel by lazy { ViewModelProvider.AndroidViewModelFactory.getInstance(this.application).create(TimeLineAppModel::class.java) }
        val timeLineAppModel by viewModels<TimeLineAppModel>()

        timeLineAppModel.apps?.observe(this) {
            it?.let {
                    timeline_recycler.addItemDecoration(getSectionCallback(it))

                timeline_recycler.adapter = TimeLineAdapter(this, layoutInflater, it, R.layout.include_today_app_card,this)
                (timeline_recycler.adapter as TimeLineAdapter).notifyDataSetChanged()
                //(timeline_recycler as AppSetsTimeLineRecyclerView).recyclerViewAttr?.sectionBackgroundColor = Color.WHITE
            }
        }
    }

    private fun getRandomColor(): Int {
        val color: MutableList<Int> = ArrayList()
        color.add(getColor(R.color.colorGoogleBlue))
        color.add(getColor(R.color.colorGoogleLightBlue))
        color.add(getColor(R.color.colorGoogleRed))
        color.add(getColor(R.color.colorGoogleLigntRed))
        color.add(getColor(R.color.colorGoogleGreen))
        color.add(getColor(R.color.colorGoogleLightGreen))
        color.add(getColor(R.color.colorGoogleYellow))
        color.add(getColor(R.color.colorGoogleLightYellow))
        return color[(0 until color.size).random()]
    }
//AppSetsRecyclerSectionItemDecoration.SectionCallback
    private fun getSectionCallback(appList: MutableList<TodayApp>): RecyclerSectionItemDecoration.SectionCallback {
        return object : RecyclerSectionItemDecoration.SectionCallback {
                //In your data, implement a method to determine if this is a section.
                override fun isSection(position: Int): Boolean {
                    /*if (!bool) {
                        (timeline_recycler as AppSetsTimeLineRecyclerView).recyclerViewAttr?.sectionLineColor =
                            getRandomColor()
                    }*/
                    return appList[position].showedDate != appList[position - 1].showedDate
                }

                //Implement a method that returns a SectionHeader.
                override fun getSectionHeader(position: Int): SectionInfo? {
                    val app = appList[position]
                    var appicon: Drawable? = null
                    CoroutineScope(Default).launch{
                        appicon =  Drawable.createFromPath(Glide.with(this@TimeLineActivity).asFile().load(app.appIcon).submit().get().path)
                    }

                    val calendar = Calendar.getInstance().apply {
                        app.showedDate?.let {
                            time = it
                        }
                    }
                    val year = calendar[Calendar.YEAR]
                    val month = calendar[Calendar.MONTH] +1
                    val day = calendar[Calendar.DATE]
                   /* val dateArray = app.showedDate?.toString()?.split(" ")
                    val month = when(dateArray?.get(0)){
                            "Jan"->{getString(R.string.jan)}
                            "Feb"->{getString(R.string.feb)}
                            "Mar"->{getString(R.string.mar)}
                            "Apr"->{getString(R.string.apr)}
                        "May"->{getString(R.string.may)}
                        "Jun"->{getString(R.string.jun)}
                        "Jul"->{getString(R.string.jul)}
                        "Aug"->{getString(R.string.aug)}
                        "Sep"->{getString(R.string.sep)}
                        "Oct"->{getString(R.string.oct)}
                        "Nov"->{getString(R.string.nov)}
                        "Dec"->{getString(R.string.dec)}
                        else ->{getString(R.string.jan)}
                    }
                    val day = dateArray?.get(1)?.split(",")?.get(0)
                    val year = dateArray?.get(1)?.split(",")?.get(1)
                    val appShowDate = "%s-%s"*/
                    return  SectionInfo("${year}-${month}-${day}", dotDrawable = appicon)
                }
            }

    }
}

class TimeLineAdapter(
    val context: Context,
    var layoutInflater: LayoutInflater,
    var list: MutableList<TodayApp>,
    var item: Int,
    activity:Activity?
) :
    RecyclerView.Adapter<TimeLineAdapter.ViewHolder>() {
    var appList: MutableList<TodayApp> = list

    inner class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        var appIcon: AppCompatImageView? = itemview.fragmentTodayAppIconView
        var appDisplayname: MaterialTextView? = itemview.fragmentTodayAppDisplayName
        var appTypeChipGroup:ChipGroup? = itemview.fragmentToday_app_type_chip_group

        var appShortDescription: MaterialTextView? = itemview.fragmentTodayAppFeaturesTextView
        var favoriteButton: MaterialButton? = itemview.fragmentToday_favorite_button
        var card :MaterialCardView? = itemview.fragmentTodayFeatureCard
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = layoutInflater.inflate(item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = appList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var mark = false
        var times = 0
        val key = Constant.FAVORITE_APPSETS
        val jsonString = PreferenceUtil.getString(context, key)
        val appData = gson.fromJson<MutableList<TodayApp>>(
            jsonString,
            object : TypeToken<MutableList<TodayApp>>() {}.type
        )
        appData?.let {
            for (app in it) {
                if (app.id == appList[position].id) {
                    mark = true
                    times++
                    continue
                } else {
                    continue
                }
            }
        }
        val appTypes = appList[position].appTypes?.split("type_")

        holder.apply {
            appDisplayname?.text = appList[position].appDisplayname
            appShortDescription?.text = appList[position].appShortDescription
            favoriteButton?.apply {
                if (mark) {
                    fragmentToday_favorite_button.text = context.getString(R.string.uncollect)
                } else {
                    fragmentToday_favorite_button.text = context.getString(R.string.collect_it)
                }
                setOnClickListener {
                    if(mark){
                        favoriteButton?.text = context.getString(R.string.uncollect)


                        // step 1
                        appList[position].favorites = appList[position].favorites?.plus(1)
                        holder.itemView.findViewById<ChipGroup>(R.id.fragmentToday_app_type_chip_group)?.findViewWithTag<Chip>("timeline_today_app_favorites_chip")?.apply {
                            text = String.format(
                                context.getString(R.string.app_favorites_times),
                                appList[position].favorites
                            )
                        }


                        // step 2
                        FragmentRecommend.updateLoggedUserFavoriteAppInCatch(
                            0,
                            appList[position],
                            context
                        )
                        //fragmentToday_favorite_button.setBackgroundColor(context.getColor(R.color.colorGray))

                        favoriteButton?.text = context.getString(R.string.collect_it)
                    }else{
                        favoriteButton?.text = context.getString(R.string.collect_it)
                        getAllActivitys(context)?.forEach {
                            if(it is MainActivity){
                                it.findViewById<BottomNavigationView>(R.id.bottomNavigation)
                                    .getOrCreateBadge(R.id.fragmentFavorite).apply {
                                        number++
                                        setVisible(true, true)
                                    }
                            }
                        }

                        // todayAppModel.setUpTodayAppFavoriteTimes(1)
                        appList[position].favorites = appList[position].favorites?.minus(1)
                        findViewById<ChipGroup>(R.id.fragmentToday_app_type_chip_group)?.findViewWithTag<Chip>("today_app_favorites_chip")?.apply {
                            text = String.format(
                                context.getString(R.string.app_favorites_times),
                                appList[position].favorites
                            )
                        }


                        // step 2
                        FragmentRecommend.updateLoggedUserFavoriteAppInCatch(
                            1,
                            appList[position],
                            context
                        )

                        favoriteButton?.text = context.getString(R.string.uncollect)
                    }
                }

            }

            appTypeChipGroup?.apply {
                val priceChip = Chip(this.context).apply {
                    tag = "timeline_today_app_price_chip"
                    text = if (appList[position].appPrice?.toDouble() != 0.00) {
                        val price = appList[position].appPrice?.toDouble()?.let {
                            FragmentRecommend.conversionPrice(
                                it,context)
                        }
                        "${context.getString(R.string.app_price)}: $price ${context.getString(R.string.price_symbol)}"
                    } else {
                        context.getString(R.string.free)
                    }
                    chipStrokeWidth = DensityUtil.dip2px(context, 1f)
                    chipStrokeColor = FragmentRecommend.getRandomColorStateList(context)
                    chipBackgroundColor = ColorStateList.valueOf(android.R.attr.colorBackground)
                }
                val favoritesChip = Chip(this.context).apply {
                    tag = "timeline_today_app_favorites_chip"
                    text = String.format(context.getString(R.string.app_favorites_times), appList[position].favorites)
                    chipStrokeWidth = DensityUtil.dip2px(context, 1f)
                    chipStrokeColor = FragmentRecommend.getRandomColorStateList(context)
                    chipBackgroundColor = ColorStateList.valueOf(android.R.attr.colorBackground)
                }

                addView(priceChip)
                addView(favoritesChip)
                if (appTypes != null) {
                    for (type in appTypes) {
                        if (type.isEmpty())
                            continue
                        val chip = Chip(this.context).apply {
                            tag = "timeline_type_${type}"
                            text = translate(type, context)
                            chipStrokeWidth = DensityUtil.dip2px(context, 1f)
                            chipStrokeColor =
                                FragmentRecommend.getRandomColorStateList(context)
                            chipBackgroundColor = ColorStateList.valueOf(android.R.attr.colorBackground)

                        }
                        addView(chip)
                    }
                }
            }

            card?.setOnClickListener {
                val intent = Intent(context, TodayDetailsActivity::class.java)
                intent.putExtra("app_from","appsets")
                intent.putExtra("today_app",appList[position])
                TransformationCompat.startActivity(holder.itemView.fragmentTodayFeaturedCardtranformationLayout, intent)
            }
            appIcon?.let {
                Glide
                    .with(context)
                    .asBitmap()
                    .load(appList[position].appIcon)
                    .placeholder(R.color.colorTransparent)
                    .transition(BitmapTransitionOptions().crossFade())
                    .transform(CenterCrop(), RoundedCorners(250))
                    .into(it)
            }

        }
    }

}



