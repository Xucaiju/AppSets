package xcj.appsets.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.item_small_screenshot.view.*
import xcj.appsets.R
import xcj.appsets.ui.fragment.FullScreenScreenShotDialogFragment

class SmallScreenShotAdapter(private val URLs: List<String>, private val context: Context, val fragmentManager: FragmentManager) :
    RecyclerView.Adapter<SmallScreenShotAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_small_screenshot, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        Glide.with(context)
            .load(URLs[position]).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .transition(DrawableTransitionOptions().crossFade())
            .transform(CenterCrop(), RoundedCorners(25))
            .into(object : SimpleTarget<Drawable?>() {


                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    holder.imageView?.layoutParams?.width = resource.intrinsicWidth
                    holder.imageView?.layoutParams?.height = resource.intrinsicHeight
                    holder.imageView?.setImageDrawable(resource)
                }
            })
        holder.imageView?.setOnClickListener {
           /* val fragment:ViewPagerBottomSheetDialogFragment = fragmentManager.findFragmentByTag(fragmentTag.tag) as ViewPagerBottomSheetDialogFragment
            fragment.dismiss()*/
            val fullScreenScreenShotDialogFragment =
                FullScreenScreenShotDialogFragment()
           /* fullScreenScreenShotDialogFragment.arguments?.let {
                it.putInt(FullScreenScreenShotDialogFragment.INTENT_SCREENSHOT_NUMBER, position)
            }*/
            FullScreenScreenShotDialogFragment.screenShotPosition = position
            fullScreenScreenShotDialogFragment.show(fragmentManager, fullScreenScreenShotDialogFragment.tag)
           /* val transaction = fragmentManager.beginTransaction()
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            transaction.add(android.R.id.content, fullScreenScreenShotDialogFragment)
                .addToBackStack(null)
                .commit()*/
        }

    }

    override fun getItemCount(): Int = URLs.size

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {

        var imageView: ImageView? = itemView.screenshot_img

    }

}
