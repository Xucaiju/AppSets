package xcj.appsets.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import xcj.appsets.Constant
import xcj.appsets.R
import xcj.appsets.adapter.FavoriteAppsAdapter
import xcj.appsets.adapter.GoogleFavoriteAppsAdapter
import xcj.appsets.adapter.TrendsViewPagerAdapter
import xcj.appsets.model.TrendsUserInfo
import xcj.appsets.util.PreferenceUtil
import xcj.appsets.viewmodel.FavoriteAppViewModel
import xcj.appsets.viewmodel.UserTrendsViewModel

class FragmentMine : Fragment(), TrendsViewPagerAdapter.tabLayoutCallback {
    val viewPager2: ViewPager2 by lazy{ requireActivity().findViewById<ViewPager2>(R.id.user_trends_viewpager)}
    val tableLayout: TabLayout by lazy{ requireActivity().findViewById<TabLayout>(R.id.user_trends_tablayout)}
    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<AppCompatTextView>(R.id.search_edit_text).apply {
            text = getString(R.string.title_search)
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
        return inflater.inflate(R.layout.fragment_mine, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val pagerAdapter = TrendsViewPagerAdapter(viewLifecycleOwner, requireActivity().application)
        viewPager2.apply {
            adapter = pagerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }
        favoriteAppsAdapter = FavoriteAppsAdapter(requireContext())
        googleFavoriteAppsAdapter = GoogleFavoriteAppsAdapter(requireContext())
        TabLayoutMediator(tableLayout,viewPager2,
            callback_one()
        ).attach()
       /* viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {


        })*/
        val userInfoLite = Gson().fromJson<TrendsUserInfo>(PreferenceUtil.getString(requireContext(), Constant.KEY_USER_INFO_LITE),
            object : TypeToken<TrendsUserInfo>() {}.type)
        userInfoLite?.apply {

            requireActivity().findViewById<TextView>(R.id.trends_user_name_text_view).text = username
            requireActivity().findViewById<TextView>(R.id.trends_user_type).let {
                it.text =  if(userType=="du"){
                    getString(R.string.developer)
                }else{
                    getString(R.string.general_user)
                }
            }
        }
        viewPagerDefaultHeight = viewPager2.measuredHeight
        val measuredWindowHeight = getWindowViewHeight(requireActivity())
        this.viewPager2.setOnTouchListener { v, event ->
            var mPosX = 0f
            var mPosY = 0f
            var mCurPosX = 0f
            var mCurPosY = 0f
            when(event.action){
                MotionEvent.ACTION_DOWN->{
                    mPosX = event.x
                    mPosY = event.y
                }
                MotionEvent.ACTION_MOVE->{
                    mCurPosX = event.x
                    mCurPosY = event.y
                }
                MotionEvent.ACTION_UP->{
                    val c= mCurPosY-mPosY
                    if(c>0){
                        Log.d("滑动方向","上")
                        viewPager2.layoutParams = (viewPager2.parent as? ViewGroup)?.layoutParams?.apply {
                            height = measuredWindowHeight.minus(100)
                        }
                    }else if(c<0){
                        Log.d("滑动方向","下")
                        viewPager2.layoutParams = (viewPager2.parent as? ViewGroup)?.layoutParams?.apply {
                            height = viewPagerDefaultHeight
                        }
                    }
                }
              }
            true
        }

    }

    companion object{
        var isGoogleFavoriteAppsListEmpty = true
        var isFavoriteAppsListEmpty = true
        var favoriteAppsViewModel:FavoriteAppViewModel? = null
        var userTrendsViewModel:UserTrendsViewModel? = null
        lateinit var favoriteAppsAdapter: FavoriteAppsAdapter
        lateinit var googleFavoriteAppsAdapter: GoogleFavoriteAppsAdapter
        var viewPagerDefaultHeight = 0
    }

    override fun callback_one(): TabLayoutMediator.TabConfigurationStrategy {
        return TabLayoutMediator.TabConfigurationStrategy { tab, position ->
            when(position){
                0->{
                    tab.text = getString(R.string.trends)
                }
                1->{
                    tab.text = getString(R.string.favorite)
                }
                2->{
                    tab.text = getString(R.string.purchased_apps)
                }
                3->{
                    tab.text = getString(R.string.notifications)
                }
                4->{
                    tab.text = getString(R.string.mine_dev_apps)
                }
            }

        }
    }
    fun getWindowViewHeight(activity:Activity):Int{
        return activity.window.decorView.measuredHeight
    }
}
fun <X> X.setup(setup:X.()->Unit):X?{
    this.setup()
    return this
}