package xcj.appsets.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.dragons.aurora.playstoreapiv2.SearchSuggestEntry
import com.google.android.material.snackbar.Snackbar
import com.skydoves.transformationlayout.onTransformationEndContainer
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_search.*
import org.apache.commons.lang3.StringUtils
import xcj.appsets.Constant
import xcj.appsets.R
import xcj.appsets.enums.ErrorType
import xcj.appsets.section.SearchSuggestionSection
import xcj.appsets.util.*
import xcj.appsets.viewmodel.SearchSuggestionViewModel
import java.util.regex.Pattern

class SearchActivity: BaseActivity(), SearchSuggestionSection.ClickListener {

    private var query: String? = null
    private var section: SearchSuggestionSection? = null
    private var madapter: SectionedRecyclerViewAdapter? = null
    private val interpolator = AccelerateDecelerateInterpolator()
    override fun onCreate(savedInstanceState: Bundle?) {
        onTransformationEndContainer(intent.getParcelableExtra("com.skydoves.transformationlayout"))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        search_activity_when_no_google_api.setImageResource(R.drawable.no_googleapi_tip)
        val suggestionModel by viewModels<SearchSuggestionViewModel>()
        suggestionModel.suggestions
            .observe(this){
                dispatchAppsToAdapter(it)
            }
        suggestionModel.error.observe(this) { errorType ->
            when (errorType) {
                ErrorType.NO_API, ErrorType.SESSION_EXPIRED -> {
                    validateApi(this)
                }
                ErrorType.NO_NETWORK -> {

                    val snackbar: Snackbar = Snackbar.make(coordinator,  R.string.error_no_network, Snackbar.LENGTH_LONG)
                    snackbar.setAction(R.string.action_retry) {
                        suggestionModel.fetchSuggestions(query)
                    }
                    snackbar.show()
                }
            }
        }
        setupSearch(suggestionModel)
        setupSuggestionRecycler()

    }
    private fun setupSuggestionRecycler() {
        section = SearchSuggestionSection(this, this)
        madapter = SectionedRecyclerViewAdapter()
        madapter?.addSection(section)
        suggestion_recycler.apply{
            adapter = madapter
            layoutManager = LinearLayoutManager(this@SearchActivity)
        }

    }
    private fun dispatchAppsToAdapter(suggestEntryList: List<SearchSuggestEntry>) {
        val oldList: List<SearchSuggestEntry>? = section?.list
        if (oldList?.isNotEmpty()!!) {
            section?.list?.clear()
            madapter?.notifyDataSetChanged()
        }
        if (suggestEntryList.isNotEmpty()) {
            section?.addData(suggestEntryList)
            madapter?.notifyDataSetChanged()
        }
    }

    private fun setupSearch(searchSuggestionModel: SearchSuggestionViewModel) {
        if(Accountant.isLoggedIn(this) == false) {
            search_view.hint = getString(R.string.no_google_paly_api)
            search_view.isEnabled = false
            search_activity_when_no_google_api.visibility = View.VISIBLE
            search_activity_recommend_words.visibility = View.GONE
        }
        action2.apply {
            setOnClickListener {
                search_view.clearFocus()
                section?.clearSuggestion()
                suggestion_recycler.adapter?.notifyDataSetChanged()
                search_view.setText("")
                search_activity_recommend_words.visibility = View.VISIBLE
                search_activity_recommend_words.animate().alpha(1f).start()

                layout
                    .animate()
                    .setDuration(350)
                    .translationY(DensityUtil.dip2px(it.context,0f))
                    .setInterpolator(interpolator)
                    .start()
                suggestion_recycler
                    .animate()
                    .setDuration(350)
                    .translationY(DensityUtil.dip2px(it.context,0f))
                    .setInterpolator(interpolator)
                    .start()
            }
        }

        search_view.setOnFocusChangeListener{ v, hasFocus ->
            if(hasFocus){
                layout
                    .animate()
                    .setDuration(350)
                    .translationY(DensityUtil.dip2px(v.context,200f))
                    .setInterpolator(interpolator)
                    .start()
                suggestion_recycler
                    .animate()
                    .setDuration(350)
                    .translationY(DensityUtil.dip2px(v.context,200f))
                    .setInterpolator(interpolator)
                    .start()
            }else{
                layout
                    .animate()
                    .setDuration(350)
                    .translationY(DensityUtil.dip2px(v.context,0f))
                    .setInterpolator(interpolator)
                    .start()
                suggestion_recycler
                    .animate()
                    .setDuration(350)
                    .translationY(DensityUtil.dip2px(v.context,0f))
                    .setInterpolator(interpolator)
                    .start()
                action2.visibility = View.INVISIBLE
            }
        }

        search_view.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (!StringUtils.isEmpty(s)) {
                    search_activity_recommend_words.animate().setDuration(350).alpha(0f).start()
                    search_activity_recommend_words.visibility = View.GONE
                    query = s.toString()
                    ContextUtil.runOnUiThread(Runnable { searchSuggestionModel.fetchSuggestions(query) }, 500)
                    action2.setImageDrawable(getDrawable(R.drawable.ic_cancel))
                    action2.visibility = View.VISIBLE
                }else{
                    section?.clearSuggestion()
                    suggestion_recycler.adapter?.notifyDataSetChanged()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        search_view.setOnEditorActionListener{ v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                query = search_view.text.toString()
                if (query?.isNotEmpty()!!) {
                    openSearchResultActivity(query)
                    return@setOnEditorActionListener true
                }
            }
            false
        }
    }
    private fun openSearchResultActivity(query: String?) {
        val intent = Intent(this, SearchResultActivity::class.java)
        intent.putExtra("QUERY", query)
        startActivity(intent, ViewUtil.getEmptyActivityBundle(this))
    }
    private fun openDetailsActivity(packageName: String) {
        val intent = Intent(this, AppDetailsActivity::class.java)
        intent.putExtra(Constant.INTENT_PACKAGE_NAME, packageName)
        startActivity(intent, ViewUtil.getEmptyActivityBundle(this))
    }
    fun back(view: View) {
        Log.d("BACK","True")
        onBackPressed()
    }

    override fun onClick(query: String?) {
        if (isSearchByPackageEnabled(this) && query?.let {isPackageName(it) }!!) {
            openDetailsActivity(query)
        } else {
            openSearchResultActivity(query)
        }
    }
    companion object{
        @JvmStatic
        open fun isPackageName(query: String): Boolean {
            if (TextUtils.isEmpty(query)) {
                return false
            }
            val pattern =
                "([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)+[\\p{L}_$][\\p{L}\\p{N}_$]*"
            val r = Pattern.compile(pattern)
            return r.matcher(query).matches()
        }
    }


}


