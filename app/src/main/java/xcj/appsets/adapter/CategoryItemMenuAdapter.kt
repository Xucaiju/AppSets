package xcj.appsets.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.item_category_menu.view.*
import xcj.appsets.R

class CategoryItemMenuAdapter(val context: Context):RecyclerView.Adapter<CategoryItemMenuAdapter.ViewHolder>() {
    private val menuList = getCategoryMenuResource()
    inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        val item:MaterialButton? = itemView.category_item_button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_menu, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = menuList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val icon = fitIcon(menuList[position])
        val randomColor = getRandomColor()
        holder.item?.apply {
            text = menuList[position]
            setIconResource(icon)
            setBackgroundColor(randomColor)
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
    private fun fitIcon(name:String):Int {
        return when(name){
                "Finance"->{R.drawable.ic_monetization_on_black_24dp}
                "Food"->{R.drawable.ic_local_dining_black_24dp}
                "CarTraffic"->{R.drawable.ic_airport_shuttle_24px}
                "MapNavigation"->{R.drawable.ic_near_me_24px}
                "Personalise"->{R.drawable.ic_face_black_24dp}
                "Tool"->{R.drawable.ic_build_24px}
                "Company"->{R.drawable.ic_business_24px}
                "Shopping"->{R.drawable.ic_shopping_cart_black_24dp}
                "Activity"->{R.drawable.ic_rowing_24px}
                "HomeRenovation"->{R.drawable.ic_home_24px}
                "Family"->{R.drawable.ic_group_24px}
                "Healthy"->{R.drawable.ic_favorite_border_black}
                "Education"->{R.drawable.ic_school_24px}
                "Travel"->{R.drawable.ic_flight_24px}
                "Cartoon"->{R.drawable.ic_pets_24px}
                "BeautyFashion"->{R.drawable.ic_grade_24px}
                "SoftwareAndDemo"->{R.drawable.ic_ondemand_video_24px}
                "BusinessOffice"->{R.drawable.ic_keyboard_24px}
                "Social"->{R.drawable.ic_group_add_24px}
                "SicialDate"->{R.drawable.ic_favourite_active}
                "Photography"->{R.drawable.ic_photo_camera_24px}
                "LifeStyle"->{R.drawable.ic_emoji_nature_24px}
                "VideoPlaybackAndEditing"->{R.drawable.ic_movie_creation_24px}
                "Sports"->{R.drawable.ic_sports_handball_24px}
                "Weather"->{R.drawable.ic_nights_stay_24px}
                "Communication"->{R.drawable.ic_forum_24px}
                "Books"->{R.drawable.ic_menu_book_24px}
                "NewsMagazine"->{R.drawable.ic_fiber_new_24px}
                "MedicalTreatment"->{R.drawable.ic_local_hospital_24px}
                "ArtDesign"->{R.drawable.ic_brush_24px}
                "Music"->{R.drawable.ic_album_24px}
                "Game"->{R.drawable.ic_sports_esports_24px}
                "Entertainment"->{R.drawable.ic_toys_24px}
                "WearOsByGoogle"->{R.drawable.ic_watch_24px}


            else -> { R.drawable.ic_shopping_cart_black_24dp }
        }

    }
    private fun getCategoryMenuResource():MutableList<String> {
        val categoryList:MutableList<String> = mutableListOf()
        categoryList.add("Finance")
        categoryList.add("Food")
        categoryList.add("Car Traffic")
        categoryList.add("Map Navigation")
        categoryList.add("Personalise")
        categoryList.add("Tool")
        categoryList.add("Company")
        categoryList.add("Shopping")
        categoryList.add("Activity")
        categoryList.add("Home Renovation")
        categoryList.add("Family")
        categoryList.add("Healthy")
        categoryList.add("Education")
        categoryList.add("Travel")
        categoryList.add("Cartoon")
        categoryList.add("Beauty Fashion")
        categoryList.add("Software And Demo")
        categoryList.add("Business Office")
        categoryList.add("Social")
        categoryList.add("Sicial Date")
        categoryList.add("Photography")
        categoryList.add("LifeStyle")
        categoryList.add("Video And Editing")
        categoryList.add("Sports")
        categoryList.add("Weather")
        categoryList.add("Communication")
        categoryList.add("Books")
        categoryList.add("News Magazine")
        categoryList.add("Medical Treatment")
        categoryList.add("Art Design")
        categoryList.add("Music")
        categoryList.add("Game")
        categoryList.add("Entertainment")
       // categoryList.add("Parenting")
        categoryList.add("WearOS By Google")
       /* categoryList.add("StrategyGame")
        categoryList.add("ActionGame")
        categoryList.add("CosplayGame")
        categoryList.add("RacingGame")
        categoryList.add("CardGame")
        categoryList.add("CreativeGame")
        categoryList.add("PuzzleGame")
        categoryList.add("ActionAndAdventure")
        categoryList.add("EducationalGame")*/
        return categoryList
    }
}