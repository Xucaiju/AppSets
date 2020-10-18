package xcj.appsets.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI.SUBCATEGORY
import com.skydoves.transformationlayout.TransformationCompat
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.category_page_item.view.*
import kotlinx.android.synthetic.main.item_installed.*
import kotlinx.android.synthetic.main.search_view.*
import xcj.appsets.Constant
import xcj.appsets.R
import xcj.appsets.listener.EndlessScrollListener
import xcj.appsets.model.App
import xcj.appsets.section.EndlessResultSection
import xcj.appsets.section.InstallAppSection
import xcj.appsets.ui.fragment.DialogFragment
import xcj.appsets.util.FragmentUtil
import xcj.appsets.viewmodel.CategoryAppsModel

class CategoryActivity : BaseActivity() , InstallAppSection.ClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private var subcategory = SUBCATEGORY.TOP_FREE//TOP_FREE
    private var section: EndlessResultSection? = null
    private var adapter: SectionedRecyclerViewAdapter? = null

    override fun onStart() {
        super.onStart()
        activity_category_blurLayout.fps = 60
        activity_category_blurLayout.startBlur()
    }

    override fun onStop() {
        activity_category_blurLayout.pauseBlur()
        super.onStop()
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            categoryId = intent.getStringExtra("CategoryId")
            categoryName = intent.getStringExtra("CategoryName")
        }
    }
/*    private fun setSubcategory(category:String?) {
        if (category != null)
            subcategory = when (category) {
            "TOP_FREE" -> SUBCATEGORY.TOP_FREE
            "TOP_GROSSING" -> SUBCATEGORY.TOP_GROSSING
            else -> SUBCATEGORY.MOVERS_SHAKERS
        }
    }*/
    private fun dispatchAppsToAdapter(newList: List<App>?) {
        val oldList: MutableList<App>? = section?.list
        if (oldList?.isEmpty()!!) {
            newList?.toMutableList()?.let { section?.updateList(it) }
            adapter?.notifyDataSetChanged()
        } else {
            if (newList?.isNotEmpty()!!) {
                for (app in newList) section?.add(app)
                section?.count?.minus(1)?.let { adapter?.notifyItemInserted(it) }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        val pager:ViewPager2? = category_viewpaper
        //pager?.currentItem = 0
        /*Observable.fromCallable {pager}.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe( {
            it?.let {
                if(it.currentItem!= oldItem){
                    Toast.makeText(this, "当前位置:$it", Toast.LENGTH_SHORT).show()
                    oldItem = it.currentItem
                }
            }
        }){
            Toast.makeText(this, "异常", Toast.LENGTH_SHORT).show()
        }.let {
            disposable.add(it)
        }*/
        val categoryAppsModel by viewModels<CategoryAppsModel>()
        model = categoryAppsModel
        onNewIntent(intent)
        MainActivity.fillSearchBarUserAvatar(this, searchbar_user_avatar)
        main_searchbar_search_icon?.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            TransformationCompat.startActivity(transformationLayout, intent)
        }
        search_edit_text?.apply {
            text = getString(R.string.title_search)
            setOnClickListener {
                val intent = Intent(it.context, SearchActivity::class.java)
                TransformationCompat.startActivity(transformationLayout, intent)
            }
        }
        searchbar_user_avatar?.setOnClickListener {
           FragmentUtil.showUserProfiltDialogFragment(supportFragmentManager)
        }
        filter_fab.setOnClickListener {
            FragmentUtil.showFilterDialogFragment(supportFragmentManager)
        }
        model?.categoryApps?.observe(this){
            dispatchAppsToAdapter(it)
        }
        categoryId?.let {
            model?.fetchCategoryApps(it, subcategory, false)
        }

        category_top_free_chip.setOnClickListener {
            pager?.currentItem = 0
        }
        category_fastest_rise_chip.setOnClickListener {
            pager?.currentItem = 1
        }
        category_highest_revenue_generation_chip?.setOnClickListener {
            pager?.currentItem = 2
        }

        pager?.adapter = CategoryViewPaperAdapter()
        pager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when(position){
                    0-> {
                        subcategory = SUBCATEGORY.TOP_FREE
                        categoryId?.let { model?.fetchCategoryApps(it, subcategory, false) }
                        category_top_free_chip.isChecked = true
                        category_fastest_rise_chip.isChecked = false
                        category_highest_revenue_generation_chip.isChecked = false
                    }
                    1->{
                        subcategory = SUBCATEGORY.TOP_GROSSING
                        categoryId?.let { model?.fetchCategoryApps(it, subcategory, false) }
                        category_top_free_chip.isChecked = false
                        category_fastest_rise_chip.isChecked = true
                        category_highest_revenue_generation_chip.isChecked = false
                    }
                    2->{
                        subcategory = SUBCATEGORY.MOVERS_SHAKERS
                        categoryId?.let { model?.fetchCategoryApps(it, subcategory, false) }
                        category_top_free_chip.isChecked = false
                        category_fastest_rise_chip.isChecked = false
                        category_highest_revenue_generation_chip.isChecked = true
                    }
                }
            }
        })
    }

    override fun onClick(app: App?) {
        val dialogFragment = DialogFragment(1, app, this)
        dialogFragment?.let {
            it.show(supportFragmentManager, it.tag)
        }
    }

    override fun onLongClick(app: App?) {
        val intent = Intent(this, TodayDetailsActivity::class.java)
        intent.putExtra("app_from","google")
        intent.putExtra("app_packagename",app?.getPackageName())
        search_result_transformatilonlayout?.let {
            TransformationCompat.startActivity(it, intent)
        }
    }
    private fun purgeAdapterData() {
        section?.purgeData()
        adapter?.notifyDataSetChanged()
    }
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == Constant.PREFERENCE_FILTER_APPS) {
            purgeAdapterData()
            categoryId?.let { model?.fetchCategoryApps(it, subcategory, false) }
        }
    }


    inner class CategoryViewPaperAdapter:RecyclerView.Adapter<CategoryViewPaperAdapter.ViewHolder>(){

        inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
            val itemRecyclerView:RecyclerView? = itemView.category_item_recycler
            init {
                setupRecycler(itemRecyclerView)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            val view = LayoutInflater.from(parent.context).inflate(R.layout.category_page_item, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return 3
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
           /* if(position==0) {
                subcategory = SUBCATEGORY.TOP_FREE
                categoryId?.let { model?.fetchCategoryApps(it, subcategory, false) }
            }
            if(position==1){
                subcategory = SUBCATEGORY.TOP_GROSSING
                categoryId?.let { model?.fetchCategoryApps(it, subcategory, false) }
            }
            if(position==2){
                subcategory = SUBCATEGORY.MOVERS_SHAKERS
                categoryId?.let { model?.fetchCategoryApps(it, subcategory, false) }
            }*/
        }
        private fun setupRecycler(recyclerView:RecyclerView?) {
            val layoutManager = LinearLayoutManager(this@CategoryActivity, RecyclerView.VERTICAL, false)
            adapter = SectionedRecyclerViewAdapter()
            section = EndlessResultSection(this@CategoryActivity, this@CategoryActivity)
            adapter?.addSection(section)
            val endlessScrollListener: EndlessScrollListener = object : EndlessScrollListener(layoutManager) {
                override fun onLoadMore(
                    page: Int,
                    totalItemsCount: Int,
                    view: RecyclerView?
                ) {
                    categoryId?.let { model?.fetchCategoryApps(it, subcategory, true) }
                }
            }
            recyclerView?.let {
                it.adapter = adapter
                it.addOnScrollListener(endlessScrollListener)
                it.layoutManager = layoutManager
            }
        }
    }


    companion object{
        var model:CategoryAppsModel? = null
        var categoryId: String? = null
        var categoryName: String? = null
        var oldItem = 0
    }
}

