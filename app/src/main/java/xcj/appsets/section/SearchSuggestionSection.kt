package xcj.appsets.section

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dragons.aurora.playstoreapiv2.SearchSuggestEntry
import com.google.android.material.chip.Chip
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import kotlinx.android.synthetic.main.item_suggestion.view.*
import xcj.appsets.R
import xcj.appsets.ui.fragment.FragmentToday.Companion.getRandomColorStateList

class SearchSuggestionSection(
    private val context: Context,
    private val clickListener: ClickListener) : Section(SectionParameters.builder().itemResourceId(R.layout.item_suggestion).build())
{
    private val suggestEntryList: MutableList<SearchSuggestEntry>? = mutableListOf()

    fun addData(suggestEntryList: List<SearchSuggestEntry>?) {
        this.suggestEntryList?.addAll(suggestEntryList!!)
    }

    override fun getContentItemsTotal()= suggestEntryList?.size?:0

    override fun getItemViewHolder(view: View): RecyclerView.ViewHolder {
        return ContentHolder(view)
    }

    override fun getEmptyViewHolder(view: View): RecyclerView.ViewHolder {
        return EmptyHolder(view)
    }
    private fun createColorStateList(pressColor:Int, nomalColor:Int):ColorStateList {
        var states = Array(2){IntArray(2)}
        states[0] = intArrayOf(android.R.attr.state_pressed, android.R.attr.state_enabled)
        val colors = intArrayOf(pressColor, nomalColor)
        return ColorStateList(states, colors)

    }
/*    private fun getRandomColorStateList():ColorStateList?{
        val colorList:MutableList<ColorStateList?> = ArrayList()
        context.let {
            colorList.add(it.getColorStateList(R.color.suggestion_color_selector_red))
            colorList.add(it.getColorStateList(R.color.suggestion_color_selector_blue))
            colorList.add(it.getColorStateList(R.color.suggestion_color_selector_green))
            colorList.add(it.getColorStateList(R.color.suggestion_color_selector_yellow))
        }
        return colorList[(0 until 4).random()]
    }*/
    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val contentHolder = holder as ContentHolder
        val suggestEntry = suggestEntryList?.get(position)
        val title = suggestEntry?.title
        val packageName = suggestEntry?.packageNameContainer?.packageName
        contentHolder.chip?.text = title
        val colorstatelist = getRandomColorStateList(context)
        contentHolder.chip?.chipStrokeColor = colorstatelist

       /* contentHolder.chip?.chipStrokeColor = createColorStateList(colorUtil.getFirstColor(), colorUtil.randomColor())*/
        holder.chip?.setOnClickListener {
            clickListener.onClick(
                if (packageName?.isEmpty()!!) title else packageName
            )
        }
    }

    val list: MutableList<SearchSuggestEntry>?
        get() = suggestEntryList
    fun clearSuggestion(){
        suggestEntryList?.clear()
    }
    interface ClickListener {
        fun onClick(query: String?)
    }

    inner class ContentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var chip:Chip? = itemView.suggest_item_chip

    }

    internal class EmptyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var chip:Chip? = itemView.suggest_item_chip
        init {
            chip!!.text = itemView.context.getString(R.string.list_empty_updates)
        }
    }

}