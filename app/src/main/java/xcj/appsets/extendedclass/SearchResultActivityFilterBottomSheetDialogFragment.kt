package xcj.appsets.extendedclass

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.View
import biz.laenger.android.vpbs.ViewPagerBottomSheetDialogFragment
import xcj.appsets.R

class SearchResultActivityFilterBottomSheetDialogFragment: ViewPagerBottomSheetDialogFragment() {
    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(requireContext(), R.layout.activity_search_result_filter, null)
        dialog.setContentView(contentView)
    }
}