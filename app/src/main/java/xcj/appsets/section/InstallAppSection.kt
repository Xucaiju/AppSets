package xcj.appsets.section

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.AsyncTask
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.card.MaterialCardView
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import kotlinx.android.synthetic.main.item_empty.view.*
import kotlinx.android.synthetic.main.item_installed.view.*
import kotlinx.android.synthetic.main.item_loading.view.*
import xcj.appsets.R
import xcj.appsets.model.App
import xcj.appsets.ui.SearchResultActivity.Companion.tapPosition
import xcj.appsets.ui.SearchResultActivity.Companion.viewRect
import java.util.*

open class InstallAppSection(protected var context: Context?, private val clickListener: ClickListener?) : Section(
        SectionParameters.builder()
            .itemResourceId(R.layout.item_installed)
            .loadingResourceId(R.layout.item_loading)
            .emptyResourceId(R.layout.item_empty)
            .build()
    ) {
    protected var appList: MutableList<App>? = mutableListOf()

    open fun updateList(appList: MutableList<App>?) {
        this.appList?.clear()
        if (appList != null) {
            this.appList?.addAll(appList)
        }

        //Sort Apps by Names
        this.appList?.sortWith(Comparator { App1: App, App2: App ->
            App2.getDisplayName()?.let { App1.getDisplayName()?.compareTo(it) }!!
        })
        state = if (appList?.isEmpty()!!) State.EMPTY else State.LOADED
    }

    open fun removeApp(packageName: String?): Int {
        var i = 0
        val iterator: MutableIterator<App>? = appList?.iterator()
        while (iterator?.hasNext()!!) {
            if (iterator.next().getPackageName().equals(packageName)) {
                iterator.remove()
                return i
            }
            i++
        }
        return -1
    }

    val list: MutableList<App>?
        get() = appList

    override fun getContentItemsTotal(): Int {
        return appList?.size?:0
    }

    override fun getItemViewHolder(view: View): RecyclerView.ViewHolder {
        return ContentHolder(view)
    }

    override fun getEmptyViewHolder(view: View): RecyclerView.ViewHolder {
        return EmptyHolder(view)
    }

    override fun getLoadingViewHolder(view: View): RecyclerView.ViewHolder {
        return LoadingHolder(view)
    }

    override fun onBindItemViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        tapPosition = position
        val contentHolder = holder as ContentHolder
        holder.itemView.getGlobalVisibleRect(viewRect)
        val app: App? = appList?.get(position)
        val version: MutableList<String?> = ArrayList()
        val extra: MutableList<String?> = ArrayList()
        contentHolder.appTitle?.text = app?.getDisplayName()
        getDetails(version, extra, app)
        setText(contentHolder.appExtra, TextUtils.join(" • ", extra))
        setText(contentHolder.appVersion, TextUtils.join(" • ", version))
        contentHolder.appDescription?.text = app?.getShortDescription()
        context?.let {
            contentHolder.appIcon?.let { it1 ->
                Glide
                    .with(it)
                    .load(app?.getIconUrl())
                    .transition(DrawableTransitionOptions().crossFade())
                    .transform(CenterCrop(), RoundedCorners(250))
                    .into(it1)
            }
        }
        contentHolder.itemView.setOnClickListener {
            clickListener?.onClick(app)
           /* val intent = Intent(context, AppDetailsActivity::class.java)
            TransformationCompat.startActivity(it.findViewById(R.id.search_result_transformatilonlayout), intent)*/

            /*val dialogFragment = DialogFragment(1, app, lifecycleOwner).apply {
                arguments?.putString("app_package_name", app?.getPackageName())
            }
            dialogFragment?.show(this.supportFragmentManager, dialogFragment?.tag)*/

        }
        contentHolder.itemView.setOnLongClickListener {
            clickListener?.onLongClick(app)
            true
        }
        //thread(true){}
        AsyncTask.execute {
            val imgCacheFile = context?.let {
                Glide.with(it).asFile().load(app?.getIconUrl()).submit().get()
            }
            val bitmap = BitmapFactory.decodeFile(imgCacheFile?.path)
            Palette.from(bitmap).generate {
                val color  = it?.getLightVibrantColor(Color.BLUE)
                holder.contentCard?.strokeColor = color!!
            }
        }
    }

    open fun getDetails(
        Version: MutableList<String?>,
        Extra: MutableList<String?>,
        app: App?
    ) {
        Version.add("v" + app?.getVersionName().toString() + "." + app?.getVersionCode())
        if (app?.isSystem!!) Extra.add(context?.getString(R.string.list_app_system)) else Extra.add(
            context?.getString(R.string.list_app_user)
        )
    }

    private fun setText(textView: TextView?, text: String) {
        if (!TextUtils.isEmpty(text)) {
            textView?.text = text
            textView?.visibility = View.VISIBLE
        } else {
            textView?.visibility = View.GONE
        }
    }

        interface ClickListener {
            fun onClick(app: App?)
            fun onLongClick(app: App?)
        }


    inner class ContentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var appIcon: ImageView? = itemView.search_result_app_icon
        var contentCard: MaterialCardView? = itemView.search_result_item_card
        var appTitle: TextView? = itemView.search_result_app_displayname
        var appDescription: TextView? = itemView.search_result_app_description
        var appVersion: TextView? = itemView.search_result_app_version
        var appExtra: TextView? = itemView.search_result_app_extra

    }

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

    init {
        this.state = State.LOADING
    }
}
