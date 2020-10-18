package xcj.appsets.adapter

import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textview.MaterialTextView
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.item_user_trends.view.*
import kotlinx.android.synthetic.main.user_trends_viewpager_position_trends.view.*
import xcj.appsets.R
import xcj.appsets.model.TrendsUserInfo
import xcj.appsets.model.UserTrends
import xcj.appsets.ui.fragment.FragmentMine
import xcj.appsets.viewmodel.UserTrendsViewModel
import java.util.*


class TrendsViewPagerAdapter(private var lifecycleOwner: LifecycleOwner, private var application: Application): RecyclerView.Adapter<TrendsViewPagerAdapter.ViewHolder>(){
    companion object{
        val disposable = CompositeDisposable()
    }
    lateinit var currentUserInfoLite:TrendsUserInfo
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when(viewType){
            0->{
                ViewHolder(layoutInflater.inflate(R.layout.user_trends_viewpager_position_trends, parent, false))
            }
            1->{
                ViewHolder(layoutInflater.inflate(R.layout.user_trends_viewpager_position_favorties_apps, parent, false))
            }
            2->{
                ViewHolder(layoutInflater.inflate(R.layout.user_trends_viewpager_position_purchased_apps, parent, false))
            }
            3->{
                ViewHolder(layoutInflater.inflate(R.layout.user_trends_viewpager_position_notifications, parent, false))
            }
            4->{
                ViewHolder(layoutInflater.inflate(R.layout.user_trends_viewpager_position_my_dev_apps, parent, false))
            }
            else -> ViewHolder(layoutInflater.inflate(R.layout.user_trends_viewpager_position_trends, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
       return position
    }
    override fun getItemCount(): Int{
        return 5
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(position){
            0->{//trends
                FragmentMine.userTrendsViewModel = ViewModelProvider.AndroidViewModelFactory(application).create(UserTrendsViewModel::class.java)
                val a = UserTrendsContentAdapter()
                val manager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.VERTICAL, false)
                holder.itemView.user_trends_text_content_recycler?.apply {
                    layoutManager = manager
                    adapter = a
                }
                FragmentMine.userTrendsViewModel?.apply{
                    userTrendsLiveData?.observe(lifecycleOwner){
                        a.setTrendsContent(it)
                    }
                    userInfoLiteLiveData?.observe(lifecycleOwner){
                        a.setUserInfo(it)
                    }
                }
            }
            1->{//favorite apps
               /* FragmentFavorite.favoriteAppsViewModel = ViewModelProvider.AndroidViewModelFactory(application).create(FavoriteAppViewModel::class.java)
                holder?.itemView?.let {
                    it.favoriteAppsWhenNoInformation?.apply {
                        setImageResource(R.drawable.no_googleapi_tip)
                    }
                    it.favoriteRecyclerView?.apply{
                        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                        adapter = FragmentFavorite.favoriteAppsAdapter?.let {it1->
                           it1
                        }
                    }
                    it.google_favorite_recycler?.apply {
                        StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                        adapter = FragmentFavorite.googleFavoriteAppsAdapter?.let { it1->
                            it1
                        }
                    }

                }
                FragmentFavorite.favoriteAppsViewModel?.apply {
                    lifecycleOwner?.let {owner->
                        getAppsetsTodayFavoriteApps()?.observe(owner) {
                            it?.isEmpty()?.let { isEmpty ->
                                FragmentFavorite.isFavoriteAppsListEmpty = if (!isEmpty) {
                                    FragmentFavorite.favoriteAppsAdapter.setFavoriteApps(it)
                                    false
                                } else {
                                    true
                                }
                                if (!(FragmentFavorite.isFavoriteAppsListEmpty && FragmentFavorite.isGoogleFavoriteAppsListEmpty)) {
                                    holder.itemView.favoriteAppsWhenNoInformation.visibility = View.GONE
                                    holder.itemView.favorite_apps_nested_scroll_view.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                    lifecycleOwner?.let {owner->
                        getGooglePlayFavoriteApps()?.observe(owner) {
                            it?.isEmpty()?.let { isEmpty ->
                                FragmentFavorite.isGoogleFavoriteAppsListEmpty = if (!isEmpty) {
                                    FragmentFavorite.googleFavoriteAppsAdapter.setFavoriteApps(it)
                                    false
                                } else {
                                    true
                                }
                                if (!(FragmentFavorite.isFavoriteAppsListEmpty && FragmentFavorite.isGoogleFavoriteAppsListEmpty)) {
                                    holder.itemView.favoriteAppsWhenNoInformation.visibility = View.GONE
                                    holder.itemView.favorite_apps_nested_scroll_view.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }*/
            }
            2->{//purchased apps

            }
            3->{//notifications

            }
            4->{//dev apps

            }
        }
    }
    private class UserTrendsContentAdapter:RecyclerView.Adapter<UserTrendsContentAdapter.ViewHolder>(){
        private var trendsContentList: MutableList<UserTrends>? = mutableListOf()
        private var currentUserInfoLite:TrendsUserInfo? = TrendsUserInfo()
        fun setTrendsContent(l: List<UserTrends>){
            trendsContentList = l.toMutableList()
            notifyDataSetChanged()
        }
        fun setUserInfo(userInfoLite:TrendsUserInfo?){
            currentUserInfoLite = userInfoLite
        }
        class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
            val userAvatar:CircleImageView? = itemView.user_trends_content_user_avatar
            val userNickName:MaterialTextView? = itemView.user_trends_content_user_nickname
            val issusingTime:MaterialTextView? = itemView.user_trends_content_user_isussing_time
            val userTrendsContent:MaterialTextView? = itemView.user_trends_content_user_trends_text_content
            val likedAction: AppCompatImageButton? = itemView.user_trends_content_liked_action
            val replyAction: AppCompatImageButton? = itemView.user_trends_content_reply_action
            val picA: AppCompatImageView? = itemView.user_trends_pic_a
            val likedTimes:MaterialTextView? = itemView.user_trends_liked_times
        }
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
           return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_user_trends, parent,false))
        }

        override fun getItemCount(): Int = trendsContentList?.size?:0

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            //var trensUserInfo:TrendsUserInfo? = TrendsUserInfo()
            val userTrendsContentModel = trendsContentList?.get(position)
           /* val userTrendsContentModel = trendsContentList?.get(position)
            Observable.fromCallable {
                AppSetsServer.getUserNamebyId(userTrendsContentModel?.userAccount, holder.itemView.context)
            }.observeOn(AndroidSchedulers.from(Looper.myLooper(), true)).subscribeOn(Schedulers.io()).subscribe({
                it?.let {
                    trensUserInfo = it
                }
            }) {

            }.let {
                disposable.add(it)
            }*/
            val calendar = Calendar.getInstance()
           /* calendar.time = userTrendsContentModel?.let{
                (it.trendsIssuingTime as java.util.Date)
            }?:Date()*/
            holder.apply {
                userNickName?.text = currentUserInfoLite?.username
                //calendar.time = "2020-11-11 12:32:22"
                issusingTime?.text = String.format("%s-%s-%s %s:%s:%s",
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    calendar.get(Calendar.SECOND)
                    )
                likedTimes?.text = userTrendsContentModel?.trendsLiked.toString()
                userTrendsContent?.text = userTrendsContentModel?.trendsTextContent
                picA?.let {pic->
                    userTrendsContentModel?.trendsPicA?.let {url->
                        Glide
                            .with(pic.context)
                            .asBitmap()
                            .load(url)
                            .placeholder(R.color.colorTransparent)
                            .transition(BitmapTransitionOptions().crossFade(100))
                            .transform(CenterCrop())
                            .into(pic)
                    }
                }
                userAvatar?.let {userAvatar->
                    currentUserInfoLite?.avatar?.let{url->
                        Glide
                            .with(userAvatar.context)
                            .asBitmap()
                            .load(url)
                            .placeholder(R.color.colorTransparent)
                            .transition(BitmapTransitionOptions().crossFade(100))
                            .transform(CenterCrop())
                            .into(userAvatar)
                    }
                }
            }
        }


    }
   // private UserAdapter:RecyclerView.Adapter<UserTrendsContentAdapter.ViewHolder>(){
    interface tabLayoutCallback{
        fun callback_one():TabLayoutMediator.TabConfigurationStrategy
    }
}
