package xcj.appsets.extendedclass

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.View
import android.widget.Toast
import biz.laenger.android.vpbs.ViewPagerBottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.bottom_review_dialog.view.*
import xcj.appsets.R
import xcj.appsets.adapter.AppSetsUserReviewAdapter
import xcj.appsets.model.AppSetsLoginInfo
import xcj.appsets.model.AppSetsUserReview
import xcj.appsets.viewmodel.UserReviewViewModel
import java.sql.Date
import java.util.*

open class UserReviewBottomSheetFragment(var userReviewAdapter: AppSetsUserReviewAdapter?,var appId:Int?, var reviewModel: UserReviewViewModel?) : ViewPagerBottomSheetDialogFragment() {

    private lateinit var confirmBotton:MaterialButton
    private lateinit var reviewContentEditText: TextInputEditText
    private lateinit var reviewText : String
    private  var reviewId : Int? = 0
    private var appSetsUser : AppSetsLoginInfo? = null
    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(requireContext(), R.layout.bottom_review_dialog, null)
        dialog.setContentView(contentView)
        confirmBotton = contentView.review_confirm_botton
        reviewContentEditText = contentView.review_content_intput_edit_text
        reviewId = userReviewAdapter?.currentReviewCount?.plus(1)
        appSetsUser = AppSetsLoginInfo.getSavedInstance(requireContext())
        var isUserReviewed = false
        var reviewPosition = 0
        userReviewAdapter?.itemCount?.let {
            for(i in 0 until it){
                if(appSetsUser?.account==userReviewAdapter?.getReviews()?.get(i)?.userAccount){
                    isUserReviewed = true
                    reviewPosition = i
                    break
                }else {
                    isUserReviewed = false
                    continue
                }
            }
        }
        confirmBotton.apply {
            if(isUserReviewed)
                text = "更新我的评论"
        }
        setupListener(isUserReviewed, reviewPosition)
    }
    private fun setupListener(isUserReviewed:Boolean, reviewPosition:Int) {




        confirmBotton.setOnClickListener {
            reviewText = reviewContentEditText.text.toString()
            if(reviewText.isNullOrEmpty()){
                Toast.makeText(it.context, "评论为空",Toast.LENGTH_SHORT).show()
            }else {

                val appSetsUserReview = AppSetsUserReview(
                    reviewId,
                    appId,
                    appSetsUser?.avatar,
                    appSetsUser?.account,
                    appSetsUser?.username,
                    Date(Calendar.getInstance().time.time),
                    reviewText
                )
                if(isUserReviewed){
                    val updateStatus = userReviewAdapter?.updateReview(reviewPosition, appSetsUserReview)
                    updateStatus?.let {status->
                        if(status){
                            this.dismiss()
                        }else{
                            Toast.makeText(requireContext(),"更新评论失败!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                   val addStatus =  userReviewAdapter?.addReview(appSetsUserReview, appId)
                    addStatus?.let {status->
                        if(status){
                            reviewModel?.featchUserReviewsByAppId(appId)
                            this.dismiss()
                        }else{
                            Toast.makeText(requireContext(),"评论失败!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
        }

    }
}