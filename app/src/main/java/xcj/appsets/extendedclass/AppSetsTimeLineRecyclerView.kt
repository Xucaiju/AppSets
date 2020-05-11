package xcj.appsets.extendedclass

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import xcj.appsets.R

class AppSetsTimeLineRecyclerView(context: Context, attrs: AttributeSet?) : RecyclerView(context, attrs){
    var recyclerViewAttr: AppSetsRecyclerViewAttr? = null
    init {
        attrs?.let {
            val a = context?.theme?.obtainStyledAttributes(
                attrs,
                R.styleable.AppSetsTimeLineRecyclerView,
                0, 0)

            a?.let {
                recyclerViewAttr =
                    AppSetsRecyclerViewAttr(it.getColor(
                        R.styleable.AppSetsTimeLineRecyclerView_sectionBackgroundColor,
                        ContextCompat.getColor(context, R.color.colorDefaultBackground)),

                        it.getColor(R.styleable.AppSetsTimeLineRecyclerView_sectionTitleTextColor,
                            ContextCompat.getColor(context, R.color.colorDefaultTitle)),

                        it.getColor(R.styleable.AppSetsTimeLineRecyclerView_sectionSubTitleTextColor,
                            ContextCompat.getColor(context, R.color.colorDefaultSubTitle)),

                        it.getColor(R.styleable.AppSetsTimeLineRecyclerView_timeLineColor,
                            ContextCompat.getColor(context, R.color.colorDefaultLine)),

                        it.getColor(R.styleable.AppSetsTimeLineRecyclerView_timeLineCircleColor,
                            ContextCompat.getColor(context, R.color.colorDefaultLine)),

                        it.getColor(R.styleable.AppSetsTimeLineRecyclerView_timeLineCircleStrokeColor,
                            ContextCompat.getColor(context, R.color.colorDefaultStroke)),

                        it.getDimension(R.styleable.AppSetsTimeLineRecyclerView_sectionTitleTextSize,
                            context.resources.getDimension(R.dimen.title_text_size)),

                        it.getDimension(R.styleable.AppSetsTimeLineRecyclerView_sectionSubTitleTextSize,
                            context.resources.getDimension(R.dimen.sub_title_text_size)),

                        it.getDimension(R.styleable.AppSetsTimeLineRecyclerView_timeLineWidth,
                            context.resources.getDimension(R.dimen.line_width)),

                        it.getBoolean(R.styleable.AppSetsTimeLineRecyclerView_isSticky, true),

                        it.getDrawable(R.styleable.AppSetsTimeLineRecyclerView_customDotDrawable))
            }

        }
    }

    /**
     * Add RecyclerSectionItemDecoration for Sticky TimeLineView
     *
     * @param callback SectionCallback
     */
    /*fun addItemDecoration(callback: AppSetsRecyclerSectionItemDecoration.SectionCallback) {
        recyclerViewAttr?.let {
            this.addItemDecoration(
                AppSetsRecyclerSectionItemDecoration(context,
                    callback,
                    it)
            )
        }
    }*/
    fun addItemDecoration(callback: AppSetsRecyclerSectionItemDecoration.SectionCallback) {
        recyclerViewAttr?.let {
            this.addItemDecoration(
                AppSetsRecyclerSectionItemDecoration(context,
                    callback,
                    it)
            )
        }
    }

}