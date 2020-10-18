package xcj.appsets.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import biz.laenger.android.vpbs.ViewPagerBottomSheetDialogFragment
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.category_select_bottom_sheet.view.*
import xcj.appsets.Constant
import xcj.appsets.R
import xcj.appsets.adapter.CategoryItemMenuAdapter
import xcj.appsets.manager.CategoryManager
import xcj.appsets.section.CategoriesSection
import xcj.appsets.ui.CategoryActivity
import xcj.appsets.ui.MainActivity
import xcj.appsets.util.ViewUtil
import xcj.appsets.viewmodel.CategoryViewModel

class CategorySelectBottomSheetDialogFragment: ViewPagerBottomSheetDialogFragment(), CategoriesSection.ClickListener {
    lateinit var categoryRecycler:RecyclerView
    lateinit var categoryManager:CategoryManager
    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(requireContext(), R.layout.category_select_bottom_sheet, null)
        dialog.setContentView(contentView)
        categoryRecycler = contentView.menu_category_recycler
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        categoryManager = CategoryManager(requireContext())
        val categoryMenuAdapter = CategoryItemMenuAdapter(requireContext())
        val categoryViewMoel by viewModels<CategoryViewModel>()
        categoryViewMoel.fetchCompleted.observe(this){
            if (it) {
                setupRecycler()
                //progressBar.setVisibility(View.GONE)
            }
        }
        categoryViewMoel.fetchCategories()
       /* categoryRecycler?.apply {
            layoutManager = GridLayoutManager(this.context, 2, GridLayoutManager.VERTICAL, false)
            adapter = categoryMenuAdapter
        }*/
    }

/*    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as ViewPagerBottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheetDialog = it as ViewPagerBottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
                BottomSheetBehavior.from(bottomSheet).peekHeight = 400
            }
        }
        return dialog
    }*/
private fun setupRecycler(){
        val viewAdapter = SectionedRecyclerViewAdapter()
        viewAdapter.addSection(
            TAG_CATEGORIES_ALL,
            CategoriesSection(
                requireContext(),
                categoryManager.getCategories(Constant.CATEGORY_APPS),
                getString(R.string.category_all),
                this
            )
        )
        viewAdapter.addSection(
            TAG_CATEGORIES_GAME,
            CategoriesSection(
                requireContext(),
                categoryManager.getCategories(Constant.CATEGORY_GAME),
                getString(R.string.category_games),
                this
            )
        )
        viewAdapter.addSection(
            TAG_CATEGORIES_FAMILY,
            CategoriesSection(requireContext(),
                categoryManager.getCategories(Constant.CATEGORY_FAMILY),
                getString(R.string.category_family),
                this
            )
        )
        categoryRecycler.layoutManager = GridLayoutManager(this.context, 2, GridLayoutManager.VERTICAL, false)
        categoryRecycler.adapter = viewAdapter
    }
    override fun onClick(categoryId: String?, categoryName: String?) {
        val intent = Intent(requireContext(), CategoryActivity::class.java)
        intent.putExtra("CategoryId", categoryId)
        intent.putExtra("CategoryName", categoryName)
        requireContext().startActivity(
            intent,
            ViewUtil.getEmptyActivityBundle(requireContext() as MainActivity)
        )
    }
    companion object{
        private const val TAG_CATEGORIES_ALL = "TAG_CATEGORIES_ALL"
        private const val TAG_CATEGORIES_GAME = "TAG_CATEGORIES_GAME"
        private const val TAG_CATEGORIES_FAMILY = "TAG_CATEGORIES_FAMILY"
    }
}