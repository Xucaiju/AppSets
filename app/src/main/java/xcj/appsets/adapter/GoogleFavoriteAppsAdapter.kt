package xcj.appsets.adapter

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.updateLayoutParams
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
import xcj.appsets.model.App
import xcj.appsets.ui.TodayDetailsActivity
import java.io.File

class GoogleFavoriteAppsAdapter(var context: Context):
    RecyclerView.Adapter<GoogleFavoriteAppsAdapter.ViewHolder>() {

    private var googleFavoriteAppsList:MutableList<App>?  = arrayListOf()

    fun setFavoriteApps(any:MutableList<App>?){

        this.googleFavoriteAppsList = any

        notifyDataSetChanged()

    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var iconImage: AppCompatImageView? = itemView.fragmentFavoriteIconImage
        var appDisplayName: MaterialTextView? = itemView.fragmentFavoriteAppDisplayName
        var appDeveloperName: MaterialTextView? = itemView.fragmentFavoriteDeveloperName
        var appDescription: MaterialTextView? = itemView.fragment_favorite_app_description
        var appContentCard: MaterialCardView? = itemView.fragment_favorite_content_card
        val viewDetailsButton : MaterialButton? = itemView.fragmentFavoriteInstallOrUninstallButton
        val transformationLayout: TransformationLayout? = itemView.fragmentFavoriteCardtranformationLayout
        val appFromSource :AppCompatImageView? = itemView.fragmentFavorite_app_from_source
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.favorite_item_design_time,parent,false)
        return ViewHolder(itemView)
    }
    override fun getItemCount(): Int = googleFavoriteAppsList?.size?:0

    override fun onBindViewHolder(holder: GoogleFavoriteAppsAdapter.ViewHolder, position: Int) {

        googleFavoriteAppsList?.get(position)?.let {
            holder.appDisplayName?.text = it.getDisplayName()?:context.getString(R.string.action_unknown)
            holder.appDeveloperName?.text = it.getDeveloperName()?:context.getString(R.string.action_unknown)
            holder.appDescription?.text = it.getShortDescription()?:context.getString(R.string.action_unknown)
            holder.iconImage?.let { it1 ->
                Glide
                    .with(context)
                    .asBitmap()
                    .load(it.getIconUrl())
                    .placeholder(R.color.colorTransparent)
                    .transition(BitmapTransitionOptions().crossFade(100))
                    .transform(CenterCrop(), RoundedCorners(250))
                    .into(it1)
            }
            AsyncTask.execute {
                val imgCacheFile: File? = Glide.with(context).asFile().load(it.getIconUrl()).submit().get()
                imgCacheFile?.let {file->
                    val bitmap = BitmapFactory.decodeFile(file.path)
                    Palette.from(bitmap).generate { palette->
                        val color = palette?.getLightVibrantColor(Color.BLUE)
                        holder.appContentCard?.strokeColor = color!!
                    }
                }
            }
        }
        holder.appFromSource?.let {
            it.setImageResource(R.drawable.ic_google_play_store)
            it.updateLayoutParams {
                width=36
                height=38
            }
        }


        holder.viewDetailsButton?.setOnClickListener{

            val intent = Intent(context, TodayDetailsActivity::class.java)
            intent.putExtra("app_from","google")
            intent.putExtra("app_packagename",googleFavoriteAppsList?.get(position)?.getPackageName())
            context?.apply {
                holder?.transformationLayout?.let {
                    TransformationCompat.startActivity(
                        it, intent)
                }
            }


        }


    }

}
