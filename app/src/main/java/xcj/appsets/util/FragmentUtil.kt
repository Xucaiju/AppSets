package xcj.appsets.util

import androidx.fragment.app.FragmentManager
import xcj.appsets.ui.fragment.SearchResultActivityFilterBottomSheetDialogFragment
import xcj.appsets.ui.fragment.UserProfileBottomSheetDailogFragment


object FragmentUtil {
    fun showFilterDialogFragment(fragmentManager:FragmentManager){
        val filterDialogFragment =
            SearchResultActivityFilterBottomSheetDialogFragment()
        filterDialogFragment.show(fragmentManager, filterDialogFragment.tag)
    }
    fun showUserProfiltDialogFragment(fragmentManager:FragmentManager){
        val userProfilBottomSheet =
            UserProfileBottomSheetDailogFragment()
        userProfilBottomSheet.show(fragmentManager, userProfilBottomSheet.tag)
    }
}