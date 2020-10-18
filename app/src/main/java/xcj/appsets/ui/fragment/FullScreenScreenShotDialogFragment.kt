package xcj.appsets.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.full_screen_shot.view.*
import xcj.appsets.R
import xcj.appsets.adapter.FullScreenScreenShotAdapter

class FullScreenScreenShotDialogFragment: DialogFragment() {
    private var screenShotViewPager2:ViewPager2? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //screenShotPosition = savedInstanceState?.getInt(INTENT_SCREENSHOT_NUMBER)
        val view:View? = inflater.inflate(R.layout.full_screen_shot, container, false)
        screenShotViewPager2 = view?.full_srceen_screen_shot_viewpager2
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = FullScreenScreenShotAdapter(requireContext())
        screenShotViewPager2?.adapter = adapter
        adapter.addUrls(xcj.appsets.ui.fragment.DialogFragment.googleApp?.getScreenshotUrls())
        screenShotViewPager2?.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        screenShotPosition?.let {
            screenShotViewPager2?.currentItem = it
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (dialog?.window != null) {
            dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }

    }

    override fun onStart() {
        super.onStart()
        val dm = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        dialog?.window?.setLayout((dm.widthPixels * 0.95).toInt(), ViewGroup.LayoutParams.MATCH_PARENT)
    }
    companion object{
        const val INTENT_SCREENSHOT_NUMBER = "INTENT_SCREENSHOT_NUMBER"
        var screenShotPosition:Int? = 0
    }
}