package xcj.appsets.section

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import kotlinx.android.synthetic.main.item_category_menu.view.*
import kotlinx.android.synthetic.main.item_empty.view.*
import kotlinx.android.synthetic.main.item_loading.view.*
import xcj.appsets.R
import xcj.appsets.model.CategoryModel


class CategoriesSection(var context: Context, var categories: List<CategoryModel>, header: String, private val clickListener: ClickListener) :
    Section(SectionParameters.builder()
            .itemResourceId(R.layout.item_category_menu)
            .loadingResourceId(R.layout.item_loading)
            .emptyResourceId(R.layout.item_empty)
            .build())
{
   /* private val header: String*/
    override fun getContentItemsTotal(): Int {
        return categories.size
    }

    override fun getItemViewHolder(view: View): RecyclerView.ViewHolder {
        return ContentHolder(view)
    }

  /*  override fun getHeaderViewHolder(view: View): RecyclerView.ViewHolder {
        return HeaderHolder(view)
    }*/

    override fun getEmptyViewHolder(view: View): RecyclerView.ViewHolder {
        return EmptyHolder(view)
    }

    override fun getLoadingViewHolder(view: View): RecyclerView.ViewHolder {
        return LoadingHolder(view)
    }

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val contentHolder = holder as? ContentHolder
        val categoryModel: CategoryModel? = categories[position]
        val randomColor = getRandomColor()
        var categoryIcon:Int? = fitIcon(categoryModel?.categoryId)
        contentHolder?.apply {
            item?.let {
                it.text = categoryModel?.categoryTitle
                //it.setIconResource(icon)
                it.setBackgroundColor(randomColor)
                it.setOnClickListener {
                        clickListener.onClick(
                            categoryModel?.categoryId,
                            categoryModel?.categoryTitle
                        )
                    }
                categoryIcon?.let { it1 -> it.setIconResource(it1) }
               /* CoroutineScope(Dispatchers.IO).launch{

                    Glide.with(context)
                        .load(categoryModel?.categoryImageUrl)
                        .downloadOnly(object : SimpleTarget<File?>() {

                            override fun onResourceReady(
                                resource: File,
                                transition: Transition<in File?>?
                            ) {

                            }
                        })

                    categoryIcon =  Drawable.createFromPath(
                        Glide
                            .with(context)
                            .asFile()
                            .load(categoryModel?.categoryImageUrl)
                            .submit()
                            .get()
                            .path
                    )
                }
               it.icon = categoryIcon*/
            }
        }
       /* contentHolder.topLabel.setText(categoryModel.getCategoryTitle())
        contentHolder.itemView.setOnClickListener { v: View? ->
            clickListener.onClick(
                categoryModel.getCategoryId(),
                categoryModel.getCategoryTitle()
            )
        }
        contentHolder.topImage!!.background = ImageUtil.getDrawable(position, GradientDrawable.OVAL)
        Glide
            .with(context)
            .load(categoryModel.getCategoryImageUrl())
            .circleCrop()
            .into(contentHolder.topImage)*/



    }
    fun fitIcon(categoryId:String?):Int?{
        return when(categoryId){
            "FINANCE"->{R.drawable.ic_monetization_on_black_24dp}
            "FOOD_AND_DRINK"->{R.drawable.ic_local_dining_black_24dp}
            "AUTO_AND_VEHICLES"->{R.drawable.ic_airport_shuttle_24px}
            "MAPS_ANDNAVIGATION"->{R.drawable.ic_near_me_24px}
            "PERSONALIZATION"->{R.drawable.ic_face_black_24dp}
            "TOOLS"->{R.drawable.ic_build_24px}
            "BUSINESS"->{R.drawable.ic_business_24px}
            "SHOPPING"->{R.drawable.ic_shopping_cart_black_24dp}
            "EVENTS"->{R.drawable.ic_rowing_24px}
            "HOUSE_AND_HOME"->{R.drawable.ic_home_24px}
            "FAMILY"->{R.drawable.ic_group_24px}
            "HEALTH_AND_FITNESS"->{R.drawable.ic_favorite_border_black}
            "EDUCATION"->{R.drawable.ic_school_24px}
            "TRAVEL_AND_LOCAL"->{R.drawable.ic_flight_24px}
            "COMICS"->{R.drawable.ic_pets_24px}
            "BEAUTY"->{R.drawable.ic_grade_24px}
            "LIBRARIES_AND_DEMO"->{R.drawable.ic_ondemand_video_24px}
            "PRODUCTIVITY"->{R.drawable.ic_keyboard_24px}
            "SOCIAL"->{R.drawable.ic_group_add_24px}
            "DATING"->{R.drawable.ic_favourite_active}
            "PHOTOGRAPHY"->{R.drawable.ic_photo_camera_24px}
            "LIFESTYLE"->{R.drawable.ic_emoji_nature_24px}
            "VIDEO_PLAYERS"->{R.drawable.ic_movie_creation_24px}
            "SPORTS"->{R.drawable.ic_sports_handball_24px}
            "WEATHER"->{R.drawable.ic_nights_stay_24px}
            "COMMUNICATION"->{R.drawable.ic_forum_24px}
            "BOOK_AND_PEFERENCE"->{R.drawable.ic_menu_book_24px}
            "NEWS_AND_MAGAZINES"->{R.drawable.ic_fiber_new_24px}
            "MEDICAL"->{R.drawable.ic_local_hospital_24px}
            "ART_AND_DESIGN"->{R.drawable.ic_brush_24px}
            "MUSIC_AND_AUDIO"->{R.drawable.ic_album_24px}
            "GAME"->{R.drawable.ic_sports_esports_24px}
            "ENTERTAINMENT"->{R.drawable.ic_toys_24px}
            "ANDROID_WEAR"->{R.drawable.ic_watch_24px}


            else -> { R.drawable.ic_emoji_nature_24px }
        }
    }
    fun getRandomColor():Int{
        var colorList:MutableList<Int>? = null
        val color1 = context.getColor(R.color.colorGoogleLightGreen)
        val color2 = context.getColor(R.color.colorGoogleLigntRed)
        val color3 = context.getColor(R.color.colorGoogleLightBlue)
        val color4 = context.getColor(R.color.colorGoogleLightYellow)
        val color5 = context.getColor(R.color.colorGoogleYellow)
        val color6 = context.getColor(R.color.colorGoogleBlue)
        val color7 = context.getColor(R.color.colorGoogleRed)
        val color8 = context.getColor(R.color.colorGoogleGreen)
        colorList = mutableListOf(color1, color2, color3, color4, color5, color6, color7, color8)
        return colorList.random()
    }
   /* override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder) {
        val headerHolder = holder as HeaderHolder
        headerHolder.line1!!.text = header
    }*/

    interface ClickListener {
        fun onClick(categoryId: String?, categoryName: String?)
    }

    class ContentHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
       var item:MaterialButton? = itemView.category_item_button
    }

 /*   internal class HeaderHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.line1)
        var line1: TextView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }*/

    internal class EmptyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var img: ImageView? = itemView.item_empty_img

        var line1: TextView? = itemView.item_empty_tip

        init {
            img!!.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_apps, null))
            line1!!.text = itemView.context.getString(R.string.list_empty_updates)
        }
    }

    internal class LoadingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var progressBar: ProgressBar? = itemView.progress_bar

        var line1: TextView? = itemView.item_loading_tip


    }

   /* init {
        this.state = State.LOADING
    }
*/
    init {

       /* this.header = header*/
        state = if (categories.isEmpty()) State.LOADING else State.LOADED
    }
}