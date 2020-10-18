package xcj.appsets.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.skydoves.transformationlayout.TransformationCompat
import com.skydoves.transformationlayout.TransformationLayout
import kotlinx.android.synthetic.main.favorite_item_design_time.view.*
import xcj.appsets.R
import xcj.appsets.model.TodayApp
import xcj.appsets.ui.TodayDetailsActivity

class FavoriteAppsAdapter(var context: Context): RecyclerView.Adapter<FavoriteAppsAdapter.ViewHolder>() {


    private var favoriteAppsList:MutableList<TodayApp>? = arrayListOf()


    fun setFavoriteApps(any:MutableList<TodayApp>??){

        this.favoriteAppsList = any

        notifyDataSetChanged()

    }

    inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var iconImage: AppCompatImageView? = itemView.fragmentFavoriteIconImage
        var appDisplayName: MaterialTextView? = itemView.fragmentFavoriteAppDisplayName
        var appDeveloperName: MaterialTextView? = itemView.fragmentFavoriteDeveloperName
        var appDescription:MaterialTextView? = itemView.fragment_favorite_app_description
        var appContentCard:MaterialCardView? = itemView.fragment_favorite_content_card
        val viewDetailsButton : MaterialButton? = itemView.fragmentFavoriteInstallOrUninstallButton
        val transformationLayout:TransformationLayout? = itemView.fragmentFavoriteCardtranformationLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val itemView = LayoutInflater.from(parent.context).inflate(R.layout.favorite_item_design_time,parent,false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = favoriteAppsList?.size?:0


     override fun onBindViewHolder(holder: ViewHolder, position: Int) {

             favoriteAppsList?.get(position)?.let {
                 holder.appDisplayName?.text = it.appDisplayname?:context.getString(R.string.action_unknown)
                 holder.appDeveloperName?.text = it.appDevelopername?:context.getString(R.string.action_unknown)
                 holder.appDescription?.text = it.appShortDescription?:context.getString(R.string.action_unknown)
                 holder.iconImage?.let { it1 ->
                     it.appIcon?.let {it2->
                         Glide
                             .with(context)
                             .asBitmap()
                             .load(it2)
                             .placeholder(R.color.colorTransparent)
                             .transition(BitmapTransitionOptions().crossFade(100))
                             .transform(CenterCrop(), RoundedCorners(250))
                             .into(it1)
                     }

                 }
                 AsyncTask.execute {
                     it?.appIcon?.let {icon->
                         val imgCacheFile = Glide.with(context).asFile().load(icon).submit().get()
                         val bitmap:Bitmap? = BitmapFactory.decodeFile(imgCacheFile?.path)
                         bitmap?.let {bit->
                             Palette.from(bit).generate {palette->
                                 val color = palette?.getLightVibrantColor(Color.BLUE)
                                 holder.appContentCard?.strokeColor = color!!
                             }
                         }
                     }
                 }
             }




         holder.viewDetailsButton?.setOnClickListener {

                 val intent = Intent(it.context, TodayDetailsActivity::class.java)
                 intent.putExtra("app_from","appsets")
                 intent.putExtra("today_app", favoriteAppsList?.get(position))
                 holder.transformationLayout?.let { it1 -> TransformationCompat.startActivity(it1, intent) }


         }


    }
}

