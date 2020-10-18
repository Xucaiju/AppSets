package xcj.appsets.ui

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.apache.commons.lang3.StringUtils
import xcj.appsets.R
import xcj.appsets.adapter.TrendsViewPagerAdapter

class MyTrendsActivity : BaseActivity(),TrendsViewPagerAdapter.tabLayoutCallback {
    companion object{
        var userAccount:String? = StringUtils.EMPTY
    }
    val viewPager2:ViewPager2 by lazy{ findViewById<ViewPager2>(R.id.user_trends_viewpager)}
    val tableLayout:TabLayout by lazy{ findViewById<TabLayout>(R.id.user_trends_tablayout)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_mine)
        val pagerAdapter = TrendsViewPagerAdapter(this, application)
        viewPager2.adapter = pagerAdapter
        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        TabLayoutMediator(tableLayout,viewPager2,
            callback_one()
        )
    }

    override fun callback_one(): TabLayoutMediator.TabConfigurationStrategy {
        return TabLayoutMediator.TabConfigurationStrategy { tab, position ->
            when(position){
                0->{
                    tab.text = "动态"
                }
                1->{
                    tab.text = "收藏的应用"
                }
                2->{
                    tab.text = "已购项目"
                }
                3->{
                    tab.text = "通知"
                }
                4->{
                    tab.text = "开发的应用"
                }
            }

        }

    }

}
