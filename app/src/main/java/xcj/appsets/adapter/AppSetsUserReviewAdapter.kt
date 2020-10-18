package xcj.appsets.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.blue
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.item_user_review.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xcj.appsets.AppSetsApplication.Companion.getAppSetsDB
import xcj.appsets.Constant
import xcj.appsets.R
import xcj.appsets.database.repository.AppSetsUserReviewRepository
import xcj.appsets.model.AppSetsLoginInfo
import xcj.appsets.model.AppSetsUserReview
import xcj.appsets.server.AppSetsServer
import xcj.appsets.util.PreferenceUtil
import xcj.appsets.util.setCacheCreateTime
import xcj.appsets.viewmodel.UserReviewViewModel.Companion.saveToCacheAppSets
import java.util.*

class AppSetsUserReviewAdapter(var context: Context): RecyclerView.Adapter<AppSetsUserReviewAdapter.ViewHolder>() {
    private val appSetsUserReviewRepository = getAppSetsDB()?.appSetsUserReviewDao()?.let {
        AppSetsUserReviewRepository(
            it
        )
    }
    private val loginInfo = AppSetsLoginInfo.getSavedInstance(context)
    private var reviews:MutableList<AppSetsUserReview>? = mutableListOf()
    fun setReviewList(reviewsList:List<AppSetsUserReview>?){
        reviews = reviewsList?.toMutableList()
        notifyDataSetChanged()
    }
    fun getReviews():MutableList<AppSetsUserReview>? {
        return  reviews
    }
    fun addReview(review:AppSetsUserReview?, appId:Int?) :Boolean{
        if (review != null) {
            if(reviews?.isEmpty()!!){

                CoroutineScope(Dispatchers.IO).launch {
                    appSetsUserReviewRepository?.apply {
                        val todayAppReviewsCount = getTodayAppReviewsCount()
                        review.id = todayAppReviewsCount+1
                        addCurrentTodayAppUserReview(review)
                    }
                }



                val key = Constant.USER_REVIEW
                val reviewsJson = PreferenceUtil.getString(context, key+appId)
                val appData:MutableList<AppSetsUserReview>? = AppSetsServer.gson.fromJson<MutableList<AppSetsUserReview>>(reviewsJson, object : TypeToken<MutableList<AppSetsUserReview>>() {}.type)
                review.id = appData?.size
                appData?.add(review)
                saveToCacheAppSets(appData, key, context, appId)
                setCacheCreateTime(context, Calendar.getInstance().timeInMillis, "TodayAppUserReview$appId")
                reviews?.add(review)
                notifyDataSetChanged()
                return true
            }else{
                reviews?.indexOf(review)?.let { notifyItemChanged(it) }
                reviews?.contains(review)?.let {
                    return it
                }
            }
            //return false
        }
        return false
    }
    fun updateReview(reviewPosition: Int, appSetsUserReview: AppSetsUserReview):Boolean {

        reviews?.set(reviewPosition, appSetsUserReview)
        //notifyDataSetChanged()
        notifyItemChanged(reviewPosition)
        reviews?.contains(appSetsUserReview)?.let {

            CoroutineScope(Dispatchers.IO).launch {
                appSetsUserReviewRepository?.apply {
                    updateTodayAppUserReview(appSetsUserReview)
                }
            }

            return it
        }
        return false
    }
    fun removeReview(review: AppSetsUserReview?):Boolean{
        return if(reviews?.contains(review)!!){
            if (review != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    appSetsUserReviewRepository?.apply {
                        delectCurrentTodayAppUserReview(review)
                    }
                }
                reviews?.remove(review)
                reviews?.indexOf(review)?.let { notifyItemChanged(it) }

                true
            }else{
                false
            }
        }else{
            false
        }
    }
    val currentReviewCount:Int?
        get() = reviews?.size
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val avatar:AppCompatImageView? =  itemView.user_review_user_avatar
        val userNickname:MaterialTextView? = itemView.user_review_user_name
        val reviewTime:MaterialTextView? = itemView.user_review_time
        val reviewContent:MaterialTextView? = itemView.user_review_content
        val card:MaterialCardView? = itemView.user_review_card
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_review, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = reviews?.size?:0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.apply {
            itemView.setOnDragListener { v, event ->

                true
            }
            val userReview:AppSetsUserReview? = reviews?.get(position)
            userNickname?.text = if(reviews?.get(position)?.userAccount ==loginInfo?.account){
                context.getString(R.string.me)
            }else{
                userReview?.userNickName
            }

            reviewTime?.text = userReview?.reviewTime.toString()
            reviewContent?.text = userReview?.reviewDetails

            avatar?.let {
                Glide
                    .with(context)
                    .asBitmap()
                    .load(userReview?.userAvatarUrl)
                    .placeholder(R.color.colorTransparent)
                    .transition(BitmapTransitionOptions().crossFade(100))
                    .transform(CenterCrop(), RoundedCorners(250))
                    .into(it)
            }
            AsyncTask.execute {
                val imgCacheFile = Glide.with(context).asFile().load(userReview?.userAvatarUrl).submit()?.get()
                val bitmap = BitmapFactory.decodeFile(imgCacheFile.path)
                Palette.Builder(bitmap).addFilter(Palette.Filter { rgb, hsl ->

                    rgb.blue <= 180


                }).maximumColorCount(24).generate{

                    it?.getLightVibrantColor(context.getColor(R.color.colorGoogleLightGreen))?.apply {
                        holder.card?.setCardBackgroundColor(this)
                    }
                }
          /*      Palette.from(bitmap).generate {

                    *//*val color = *//*
                    it?.getVibrantColor(Color.BLUE)?.apply {
                        holder.card?.setCardBackgroundColor(this)
                    }
                    *//*holder?.homeAppCardView?.strokeColor = color!!*//*
                }*/
            }
        }
    }


}