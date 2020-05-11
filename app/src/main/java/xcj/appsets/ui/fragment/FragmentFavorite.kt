package xcj.appsets.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_favorite.*
import xcj.appsets.R
import xcj.appsets.adapter.FavoriteAppsAdapter
import xcj.appsets.adapter.GoogleFavoriteAppsAdapter
import xcj.appsets.viewmodel.FavoriteAppViewModel

class FragmentFavorite : Fragment() {
    private lateinit var favoriteAppsAdapter: FavoriteAppsAdapter
    private lateinit var googleFavoriteAppsAdapter: GoogleFavoriteAppsAdapter
    private var isFavoriteAppsListEmpty = true
    private var isGoogleFavoriteAppsListEmpty = true
    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<AppCompatTextView>(R.id.search_edit_text).apply {
            text = getString(R.string.favorite)
        }
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
            .getOrCreateBadge(R.id.fragmentFavorite).apply {
                isVisible = false
                number=0

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        favoriteAppsWhenNoInformation.setImageResource(R.drawable.no_googleapi_tip)
        val favoriteAppModel by viewModels<FavoriteAppViewModel>()
        favoriteAppsAdapter = FavoriteAppsAdapter(requireContext(), requireActivity())
        googleFavoriteAppsAdapter = GoogleFavoriteAppsAdapter(requireContext())
        favoriteRecyclerView.adapter = favoriteAppsAdapter
        google_favorite_recycler.adapter = googleFavoriteAppsAdapter//googleFavoriteAppsAdapter
        //Observable.fromCallable { PreferenceUtil.getString("") }
        favoriteRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        google_favorite_recycler.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        favoriteAppModel.getAppsetsTodayFavoriteApps()?.observe(viewLifecycleOwner) {
            it?.isEmpty()?.let { isEmpty->

                isFavoriteAppsListEmpty = if(!isEmpty){
                    favoriteAppsAdapter.setFavoriteApps(it)
                    false
                }else{
                    true
                }
                if(!(isFavoriteAppsListEmpty&&isGoogleFavoriteAppsListEmpty)){
                    favoriteAppsWhenNoInformation.visibility = View.GONE
                    favorite_apps_nested_scroll_view.visibility = View.VISIBLE
                }
            }
        }
        favoriteAppModel.getGooglePlayFavoriteApps()?.observe(viewLifecycleOwner) {
            it?.isEmpty()?.let { isEmpty->
                    isGoogleFavoriteAppsListEmpty = if(!isEmpty){
                        googleFavoriteAppsAdapter.setFavoriteApps(it)
                        false
                    }else{
                        true
                    }
                if(!(isFavoriteAppsListEmpty&&isGoogleFavoriteAppsListEmpty)){
                    favoriteAppsWhenNoInformation.visibility = View.GONE
                    favorite_apps_nested_scroll_view.visibility = View.VISIBLE
                }

            }
        }
        favorite_apps_nested_scroll_view.isSmoothScrollingEnabled = true

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(
            "Fragment栈的大小为", "[${
            requireActivity().supportFragmentManager.backStackEntryCount
            }]"
        )

    }
}
