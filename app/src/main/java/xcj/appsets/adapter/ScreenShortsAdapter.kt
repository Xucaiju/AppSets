package xcj.appsets.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.item_screenshots_small.view.*
import xcj.appsets.R

class ScreenShortsAdapter(private val context: Context) : RecyclerView.Adapter<ScreenShortsAdapter.ViewHolder>() {

    private var mUrls:MutableList<String>? = arrayListOf()

    fun setUrls(urls:MutableList<String>?) {
        mUrls = urls
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScreenShortsAdapter.ViewHolder {
       val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_small_screenshot,parent,false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = mUrls?.size?:0


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var appScreenshotImg: AppCompatImageView = itemView.screenshot_img

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Glide.with(context)
                .load(mUrls?.get(position))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .transition(DrawableTransitionOptions().crossFade())
                .transform(CenterCrop(), RoundedCorners(25))
                .into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        holder.appScreenshotImg.layoutParams.width = resource.intrinsicWidth
                        holder.appScreenshotImg.layoutParams.height = resource.intrinsicHeight
                        holder.appScreenshotImg.setImageDrawable(resource)
                    }

                })
    }
}


