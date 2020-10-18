package xcj.appsets.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import biz.laenger.android.vpbs.ViewPagerBottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.activity_search_result_filter.view.*
import xcj.appsets.R
import xcj.appsets.manager.FilterManager
import xcj.appsets.model.FilterModel

class SearchResultActivityFilterBottomSheetDialogFragment: ViewPagerBottomSheetDialogFragment() {
    private lateinit var filterModel: FilterModel
    private lateinit var ratingChipGroup: ChipGroup
    private lateinit var downloadsChipGroup: ChipGroup
    private lateinit var otherChipGroup: ChipGroup
    private lateinit var actionConfirmApplyFilter: MaterialButton
    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView:View = View.inflate(requireContext(), R.layout.activity_search_result_filter, null).also {
            ratingChipGroup = it.app_rating_chip_group
            downloadsChipGroup = it.app_downloads_chip_group
            otherChipGroup = it.other_choice_chip_group
            actionConfirmApplyFilter = it.action_confirm_filter
        }
        dialog.setContentView(contentView)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        filterModel = FilterManager.getFilterPreferences(this.requireContext())
        setupChips()
        actionConfirmApplyFilter.setOnClickListener {
            FilterManager.saveFilterPreferences(this.requireContext(), filterModel)
            dismiss()
        }
    }
    private fun setupChips(){
        val chipDrawable = ChipDrawable.createFromAttributes(this.requireContext(), null, 0, R.style.Widget_MaterialComponents_Chip_Choice)
        val chip_gsf = Chip(this.requireContext()).apply {
            chip_gsf_id = View.generateViewId()
            id =
                chip_gsf_id
            text = getString(R.string.action_filter_gsf_dependent_apps)
            //setChipDrawable(chipDrawable)
            isChecked = filterModel.gsfDependentApps
            setOnCheckedChangeListener { thisView, isChecked ->
                filterModel.gsfDependentApps = isChecked
            }
        }
        val chip_paid = Chip(this.requireContext()).apply {
            chip_paid_id = View.generateViewId()
            id =
                chip_paid_id
            text = getString(R.string.action_filter_paid_apps)
            //setChipDrawable(chipDrawable)
            isChecked = filterModel.paidApps
            setOnCheckedChangeListener { thisView, isChecked ->
                filterModel.paidApps = isChecked
            }
        }
        val chip_ads = Chip(this.requireContext()).apply {
            chip_ads_id = View.generateViewId()
            id =
                chip_ads_id
            text = getString(R.string.action_filter_apps_with_ads)
            //setChipDrawable(chipDrawable)
            isChecked = filterModel.appsWithAds
            setOnCheckedChangeListener { thisView, isChecked ->
                filterModel.appsWithAds = isChecked
            }
        }

        otherChipGroup.addView(chip_gsf,0)
        otherChipGroup.addView(chip_paid,1)
        otherChipGroup.addView(chip_ads,2)

        val downloadLabels =
            resources.getStringArray(R.array.filterDownloadsLabels)
        val downloadValues =
            resources.getStringArray(R.array.filterDownloadsValues)
        val ratingLabels =
            resources.getStringArray(R.array.filterRatingLabels)
        val ratingValues =
            resources.getStringArray(R.array.filterRatingValues)
       // val downloadsChipArray:Array<Chip> = arrayOf()
        for(c in downloadLabels.indices){
            val chip = Chip(this.requireContext()).apply {
                id = c
                text = downloadLabels[c]
               // setChipDrawable(chipDrawable)
                isChecked = filterModel.downloads==downloadValues[c].toInt()
            }
            downloadsChipGroup.addView(chip)

        }
        for(c in ratingLabels.indices){
            val chip = Chip(this.requireContext()).apply {
                id = c
                text = ratingLabels[c]
                //setChipDrawable(chipDrawable)
                isChecked = filterModel.rating==ratingValues[c].toFloat()
            }
            ratingChipGroup.addView(chip)
        }
        downloadsChipGroup.setOnCheckedChangeListener { group, checkedId ->
            filterModel.downloads = downloadValues[checkedId].toInt()
        }
        ratingChipGroup.setOnCheckedChangeListener { group, checkedId ->
            filterModel.rating = ratingValues[checkedId].toFloat()
        }

    }
    companion object{
        var chip_gsf_id = 0
        var chip_paid_id = 0
        var chip_ads_id = 0
    }

}