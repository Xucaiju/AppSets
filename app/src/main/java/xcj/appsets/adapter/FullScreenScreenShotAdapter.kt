package xcj.appsets.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import kotlinx.android.synthetic.main.item_full_screen_shot.view.*
import xcj.appsets.R

class FullScreenScreenShotAdapter(val context: Context): RecyclerView.Adapter<FullScreenScreenShotAdapter.ViewHolder>() {
    private var screenShotUrlList:MutableList<String>? = mutableListOf()
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val img:AppCompatImageView? = itemView.full_srceen_screen_shot_item
    }
    fun addUrls(mutableList: MutableList<String>?){
        screenShotUrlList = mutableList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_full_screen_shot, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int  = screenShotUrlList?.size?:0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.img?.let {
            Glide
                .with(context)
                .asBitmap()
                .load(screenShotUrlList?.get(position))
                .placeholder(R.color.colorTransparent)
                .transition(BitmapTransitionOptions().crossFade(100))
                .into(it)
        }

    }
}