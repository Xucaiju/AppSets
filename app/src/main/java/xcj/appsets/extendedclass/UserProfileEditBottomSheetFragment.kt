package xcj.appsets.extendedclass

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import biz.laenger.android.vpbs.ViewPagerBottomSheetDialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.user_profile_edit_bottom_sheet_fragment.view.*
import kotlinx.coroutines.*
import xcj.appsets.R
import xcj.appsets.model.AppSetsLoginInfo
import xcj.appsets.ui.SettingsActivity

class UserProfileEditBottomSheetFragment : ViewPagerBottomSheetDialogFragment() {
//    lateinit var
    var userChoosedImgUri: Uri? = null
    var loggedInfo: AppSetsLoginInfo?=null
    lateinit var userAvatarChooseCard:MaterialCardView
    lateinit var actionConfirm:MaterialButton
    lateinit var editTextLayout:TextInputLayout
    lateinit var userAvatarImageview :AppCompatImageView
    lateinit var userAvatarMask:FrameLayout
    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(requireContext(), R.layout.user_profile_edit_bottom_sheet_fragment, null)
        dialog.setContentView(contentView)
        contentView?.apply {
            user_avatar_choose_card?.let {
                userAvatarChooseCard = it
            }
            user_profile_update_action?.let{
                actionConfirm = it
            }
            user_profil_input_layout?.let {
                editTextLayout = it
            }
            user_avatar_choose_image?.let{
                userAvatarImageview = it
            }
            user_avatar_mask?.let{
                userAvatarMask = it
            }
        }



        loggedInfo = AppSetsLoginInfo.getSavedInstance(requireContext())
        fillCurrentAvatarAndUserName()

        userAvatarChooseCard.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            try{
                startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_an_image)), 8989)

            }catch (e: ActivityNotFoundException){
                e.printStackTrace()
            }

        }
        actionConfirm.setOnClickListener {
            if(editTextLayout?.editText?.text.isNullOrBlank()&&editTextLayout?.editText?.text.isNullOrEmpty()){
                Toast.makeText(requireContext(), getString(R.string.cant_be_empty), Toast.LENGTH_SHORT).show()
            }else{
                updateProfile()
            }
        }

    }
    private fun fillCurrentAvatarAndUserName() {
        editTextLayout?.editText?.hint = loggedInfo?.username
        Glide.with(this)
            .asBitmap()
            .load(loggedInfo?.avatar)
            .placeholder(R.color.colorTransparent)
            .transition(BitmapTransitionOptions().crossFade())
            .transform(CenterCrop())
            .into(userAvatarImageview)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            8989-> if(resultCode== Activity.RESULT_OK && data!=null){
                userChoosedImgUri = data.data
                userAvatarMask?.let {
                    userAvatarChooseCard.removeView(it)
                }
                Glide.with(requireContext()).asBitmap().load(userChoosedImgUri)
                    .placeholder(R.color.colorTransparent)
                    .transition(BitmapTransitionOptions().crossFade())
                    .transform(CenterCrop())
                    .into(userAvatarImageview)

                Log.d("Uri",userChoosedImgUri.toString())
            }else{
                Toast.makeText(requireContext(), getString(R.string.please_rechoose_image), Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun updateProfile(){
        actionConfirm.text = getString(R.string.updating)
        loggedInfo?.username = editTextLayout?.editText?.text.toString()
        loggedInfo?.avatar = userChoosedImgUri?.toString()
        loggedInfo?.let { AppSetsLoginInfo.save(requireContext(), it) }

        /* runOnUiThread{

         }*/

        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                context?.applicationContext?.let { Glide.get(it).clearMemory() }
            }else {
                context?.applicationContext?.let { Glide.get(it).clearDiskCache() }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        CoroutineScope(Dispatchers.Default).launch {
            updateProfileInServer()
            withContext(Dispatchers.Main){
                val allActivitys = SettingsActivity.getAllActivitys(requireContext())
                allActivitys?.forEach {
                    it.recreate()
                }
                actionConfirm.text = getString(R.string.successful)
            }
        }
    }
    private suspend fun updateProfileInServer(){
        delay(5000)
        //return 1
    }
}