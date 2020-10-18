package xcj.appsets.adapter

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.card.MaterialCardView
import com.madrapps.eyedropper.EyeDropper
import com.skydoves.transformationlayout.TransformationCompat
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.item_featured.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import xcj.appsets.R
import xcj.appsets.model.App
import xcj.appsets.ui.AppDetailsActivity
import xcj.appsets.ui.TodayDetailsActivity
import xcj.appsets.ui.fragment.DialogFragment
import xcj.appsets.util.PackageUtil
import xcj.appsets.util.humanReadableByteValue
import java.util.*

class FeaturedAppsAdapter(
    private val context: Context,
    private val requireActivity: FragmentActivity,
    var lifecycleOwner: LifecycleOwner,
    var compositeDisposable: CompositeDisposable?
) : RecyclerView.Adapter<FeaturedAppsAdapter.ViewHolder>() {
    private var appList: MutableList<App> = ArrayList()
    fun addData(appList: MutableList<App>) {
        this.appList.clear()
        this.appList = appList
        // notifyDataSetChanged()
    }

    val isDataEmpty: Boolean
        get() = appList.isEmpty()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_featured, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val app: App = appList[position]
        viewHolder.txtAppName?.text = app.getDisplayName()
        if (app.getShortDescription() != null) {
            viewHolder.appDescription?.text = app.getShortDescription()
        }
        if (app.getPageBackgroundImage() != null) drawBackground(app, viewHolder)
        viewHolder.txtIndicator?.visibility =
            if (PackageUtil.isInstalled(context, app)) View.VISIBLE else View.GONE
        if (viewHolder.txtSize != null)
            viewHolder.txtSize?.text = humanReadableByteValue(app.getSize()!!, true)
        viewHolder.itemView.setOnClickListener {
            AppDetailsActivity.app = app
            showBottomSheetDialog(app)

        }
        viewHolder.itemView.setOnLongClickListener {
            //AppDetailsActivity.app = app
            Log.d("Package name","${app.getPackageName()}")
            showAppDetails(app, context, viewHolder)
            true

        }
        val targetView = viewHolder.imgIcon as View
        EyeDropper(targetView, EyeDropper.ColorSelectionListener {
            viewHolder.homeAppCardView?.setCardBackgroundColor(it)
        })


    }
    private fun showAppDetails(app:App?,context: Context?,holder:ViewHolder?){
       val intent = Intent(context, TodayDetailsActivity::class.java)
        intent.putExtra("app_from","google")
        intent.putExtra("app_packagename",app?.getPackageName())
        context?.apply {
            holder?.itemView?.fragmentHome_transformationlayout?.let {
                TransformationCompat.startActivity(
                    it, intent)
            }
        }
    }
    private fun showBottomSheetDialog(app: App?) {
        val dialogFragment = DialogFragment(1, app, lifecycleOwner).apply {
            arguments?.putString("app_package_name", app?.getPackageName())
        }
       // fragmentTag.tag = dialogFragment?.tag
        dialogFragment?.show(requireActivity.supportFragmentManager, dialogFragment.tag)
    }

    private fun drawBackground(app: App, holder: ViewHolder) {
        Glide
            .with(context)
            .asBitmap()
            .load(app.getIconUrl())
            .placeholder(R.color.colorTransparent)
            .transition(BitmapTransitionOptions().crossFade())
            .transform(CenterCrop(), RoundedCorners(250))
            .into(holder.imgIcon!!)
        CoroutineScope(Default).launch {
            drawCardBorder(app, holder)
        }
        /*AsyncTask.execute{
            withContext(Default){

            }


        }*/




    }

    override fun getItemCount(): Int {
        return appList.size
    }
   /* suspend fun draw(app:App, holder:ViewHolder){
        drawCardBorder(app,holder)
    }*/
    private suspend fun drawCardBorder(app:App?, holder:ViewHolder?){
        delay(0)
        val imgCacheFile = Glide.with(context).asFile().load(app?.getIconUrl()).submit()?.get()
        val bitmap = BitmapFactory.decodeFile(imgCacheFile.path)
        Palette.from(bitmap).generate {
            /*val color = */
            it?.getLightVibrantColor(Color.BLUE)?.apply {
                holder?.homeAppCardView?.strokeColor = this
            }
            /*holder?.homeAppCardView?.strokeColor = color!!*/
        }

    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imgIcon: ImageView? = itemView.app_icon
        var appDescription: TextView? = itemView.app_description

        var txtAppName: TextView? = itemView.app_name

        var homeAppCardView: MaterialCardView? = itemView.homeAppCardView

        var txtSize: TextView? = itemView.app_size

        var txtIndicator: TextView? = itemView.txt_indicator

        init {
            val orientation = Resources.getSystem().configuration.orientation
        }
    }

}