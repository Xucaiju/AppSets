package xcj.appsets.model

import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_nested_scroll.view.*
import xcj.appsets.adapter.SmallScreenShotAdapter

class Screenshot(var mview: View?, var mapp: App?) : AbstractDetails(mview, mapp) {

    var rootLayout: LinearLayout? = mview?.app_details_root
    var recyclerView: RecyclerView? = mview?.bottom_sheet_screenshot_recycler
    override fun draw() {
        if (app?.getScreenshotUrls()?.size!! > 0) {
            drawGallery()
        }
    }

    private fun drawGallery() {
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            alpha = 0f
            adapter = app?.getScreenshotUrls()?.let {
                context?.let { it1 ->
                    SmallScreenShotAdapter(
                        it,
                        it1
                    )
                }
            }
            adapter?.notifyDataSetChanged()
            visibility = View.VISIBLE
            animate().alpha(1f).setDuration(350).setStartDelay(600).translationY(0f).start()
        }
    }
}