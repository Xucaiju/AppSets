package xcj.appsets.ui

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.graphics.Rect
import android.os.Bundle
import android.os.PersistableBundle
import android.transition.TransitionSet
import android.view.animation.Interpolator
import android.widget.Toast
import androidx.activity.viewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import androidx.recyclerview.widget.RecyclerView.OnFlingListener
import com.skydoves.transformationlayout.TransformationCompat
import com.skydoves.transformationlayout.onTransformationStartContainer
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_search_result.*
import kotlinx.android.synthetic.main.item_installed.*
import xcj.appsets.AppSetsApplication
import xcj.appsets.AutoDisposable
import xcj.appsets.Constant
import xcj.appsets.R
import xcj.appsets.enums.ErrorType
import xcj.appsets.enums.ErrorType.NO_API
import xcj.appsets.events.Event
import xcj.appsets.listener.EndlessScrollListener
import xcj.appsets.manager.FilterManager
import xcj.appsets.model.App
import xcj.appsets.model.FilterModel
import xcj.appsets.section.InstallAppSection
import xcj.appsets.section.SearchResultSection
import xcj.appsets.ui.fragment.DialogFragment
import xcj.appsets.util.DensityUtil
import xcj.appsets.util.FragmentUtil
import xcj.appsets.util.filterSearchNonPersistent
import xcj.appsets.util.validateApi
import xcj.appsets.viewmodel.SearchAppsViewModel

private val transitionInterpolator = FastOutSlowInInterpolator()
private const val TRANSITION_DURATION = 550L
private const val TAP_POSITION = "tap_position"

class SearchResultActivity : BaseActivity(), InstallAppSection.ClickListener, OnSharedPreferenceChangeListener {
    companion object {
        @JvmField
        var tapPosition = NO_POSITION

        @JvmField
        val viewRect = Rect()
    }

    private var adapter: SectionedRecyclerViewAdapter? = null
    private var section: SearchResultSection? = null
    private var searchAppsViewModel: SearchAppsViewModel? = null
    private var query: String? = null
    private var sharedPreferences: SharedPreferences? = null
    private val autoDisposable: AutoDisposable = AutoDisposable()


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val bundle = intent?.extras
        if (bundle != null) {
            query = bundle.getString("QUERY")
            search_view.setText(query)
            searchAppsViewModel?.fetchQueriedApps(query, false)
        } else
            finishAfterTransition()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        onTransformationStartContainer()

        super.onCreate(savedInstanceState)
        tapPosition = savedInstanceState?.getInt(TAP_POSITION, NO_POSITION) ?: NO_POSITION

        setContentView(R.layout.activity_search_result)

        sharedPreferences = xcj.appsets.util.getSharedPreferences(this)
        sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
        autoDisposable.bindTo(lifecycle)
        setUpSearchBar()
        setupResultRecycler()
        val searchAppModel by viewModels<SearchAppsViewModel>()
        searchAppsViewModel = searchAppModel

        searchAppsViewModel?.getQueriedApps()?.observe(this) {

            dispatchAppsToAdapter(it)

        }
        searchAppsViewModel?.error?.observe(this, {
            when (it) {
                NO_API, ErrorType.SESSION_EXPIRED -> {
                    validateApi(this)
                }
                ErrorType.NO_NETWORK -> {
                    Toast.makeText(this, "NO network", Toast.LENGTH_SHORT).show()
                    /* showSnackBar(
                         coordinator
                         R.string.error_no_network
                     ) { v -> model.fetchQueriedApps(query, false) }*/
                }
                else->{}
            }
        })

        autoDisposable.add(AppSetsApplication.getRxBus()?.getBus()?.subscribe({
            when (it?.getSubType()) {
                Event.SubType.API_SUCCESS -> {
                    searchAppsViewModel?.fetchQueriedApps(query, false)
                }
                else->{}
            }
        }) {

        })
        filter_fab.setOnClickListener {
           /* val filterDialogFragment = SearchResultActivityFilterBottomSheetDialogFragment()
            filterDialogFragment.show(supportFragmentManager, filterDialogFragment.tag)*/
            FragmentUtil.showFilterDialogFragment(supportFragmentManager)
        }
        onNewIntent(intent)
    }

    private fun dispatchAppsToAdapter(newList: MutableList<App>?) {

        val oldList: MutableList<App>? = section?.list
        var isUpdated = false
        if (oldList?.isEmpty()!!) {
            section?.updateList(newList)
            adapter?.getAdapterForSection(section)?.notifyAllItemsChanged()
        } else {
            if (newList != null) {
                if (newList.isNotEmpty()) {
                    for (app in newList) {
                        if (oldList.contains(app)) {
                            continue
                        }
                        section?.add(app)
                        isUpdated = true
                    }
                    if (isUpdated)
                        section?.count?.minus(1)?.let {
                            adapter?.getAdapterForSection(section)?.notifyItemInserted(it)
                        }
                }
            }
        }
    }

    private fun setUpSearchBar() {
        search_view.isFocusable = false
        search_view.setOnClickListener {
            onBackPressed()
        }
        action1.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupResultRecycler() {
        adapter = SectionedRecyclerViewAdapter()
        section = SearchResultSection(this, this)
        adapter?.addSection(section)
        searchResultrecycler.adapter = adapter
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val endlessScrollListener: EndlessScrollListener =
            object : EndlessScrollListener(layoutManager) {
                override fun onLoadMore(
                    page: Int,
                    totalItemsCount: Int,
                    view: RecyclerView?
                ) {
                    searchAppsViewModel?.fetchQueriedApps(query, true)
                }
            }
        searchResultrecycler.addOnScrollListener(endlessScrollListener)
        searchResultrecycler.onFlingListener = object : OnFlingListener() {
            override fun onFling(velocityX: Int, velocityY: Int): Boolean {
                if (velocityY < 0) {
                    filter_fab.show()
                    search_result_searbar_bar.animate().setDuration(350).translationY(
                        DensityUtil.dip2px(this@SearchResultActivity, 0f)
                    ).start()

                } else if (velocityY > 0) {
                    filter_fab.hide()
                    search_result_searbar_bar.animate().setDuration(350).translationY(
                        DensityUtil.dip2px(this@SearchResultActivity, -84f)
                    ).start()
                }
                return false
            }
        }
        searchResultrecycler.layoutManager = layoutManager

    }

    private fun TransitionSet.setCommonInterpolator(interpolator: Interpolator): TransitionSet {
        (0 until transitionCount)
            .map {
                getTransitionAt(it)
            }
            .forEach {
                it.interpolator = interpolator
            }

        return this
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

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            Constant.PREFERENCE_FILTER_APPS -> {
                purgeAdapterData()
                searchAppsViewModel?.fetchQueriedApps(query, false)
            }
        }
    }

    private fun purgeAdapterData() {
        section?.purgeData()
        adapter?.getAdapterForSection(section)?.notifyAllItemsChanged()
    }

    override fun onDestroy() {
        sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
        if (filterSearchNonPersistent(this)) FilterManager.saveFilterPreferences(
            this,
            FilterModel()
        )
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putInt(TAP_POSITION, tapPosition)
    }
}
