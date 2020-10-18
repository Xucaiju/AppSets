package xcj.appsets.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import biz.laenger.android.vpbs.ViewPagerBottomSheetDialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.user_profile_bottom_sheet.view.*
import xcj.appsets.R
import xcj.appsets.model.AppSetsLoginInfo
import xcj.appsets.ui.AboutActivity
import xcj.appsets.ui.preference.SettingsActivity

open class UserProfileBottomSheetDailogFragment() :ViewPagerBottomSheetDialogFragment() {

/*    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_profile_bottom_sheet,container,false)
    }*/
    lateinit var userAvatar:AppCompatImageView
    lateinit var userNickName:MaterialTextView
    lateinit var userAccount:MaterialTextView

    lateinit var settingAction:MaterialTextView
    lateinit var aboutAction:MaterialTextView
    //lateinit var downloadContentAction:MaterialTextView
    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(requireContext(), R.layout.user_profile_bottom_sheet, null)
        dialog.setContentView(contentView)
        userAvatar = contentView.bottom_user_profile_avatar
        userNickName = contentView.bottom_user_nickname
        userAccount = contentView.bottom_user_account
        settingAction = contentView.bottom_setting_action
        aboutAction = contentView.bottom_about_action
       // downloadContentAction = contentView.bottom_download_content_action
        setupUserInfo()
        settingAction.setOnClickListener {
            startActivity(Intent(context,
                SettingsActivity::class.java))
            super.dismiss()
        }
        aboutAction.setOnClickListener {
            startActivity(Intent(context, AboutActivity::class.java))
            super.dismiss()
        }
       /* downloadContentAction.setOnClickListener {
            startActivity(Intent(context,DownloadManagerActivity::class.java))
            super.dismiss()
        }*/
    }
    private fun setupUserInfo(){
        val appSetsLoginInfo = context?.let { AppSetsLoginInfo.getSavedInstance(it) }
        userNickName.text = appSetsLoginInfo?.username
        userAccount.text = appSetsLoginInfo?.account
        Glide
            .with(this)
            .asBitmap()
            .load(appSetsLoginInfo?.avatar)
            .error(R.drawable.avatar)
            .transition(BitmapTransitionOptions().crossFade(100))
            .transform(CenterCrop(), RoundedCorners(250))
            .into(userAvatar)
    }

}