package xcj.appsets.ui

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xcj.appsets.R
import xcj.appsets.model.AppSetsLoginInfo
import xcj.appsets.ui.preference.SettingsActivity.Companion.getAllActivitys

class UserProfileActivity : BaseActivity() {
    var userChoosedImgUri: Uri? = null
    var loggedInfo:AppSetsLoginInfo?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        loggedInfo = AppSetsLoginInfo.getSavedInstance(this)
        fillCurrentAvatarAndUserName()
        user_avatar_choose_card.setOnClickListener {
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
        user_profile_update_action.setOnClickListener {
            if(user_profil_input_layout?.editText?.text.isNullOrBlank()&&user_profil_input_layout?.editText?.text.isNullOrEmpty()){
                Toast.makeText(this, getString(R.string.cant_be_empty), Toast.LENGTH_SHORT).show()
            }else{
                updateProfile()
            }
        }
    }
    private fun updateProfile(){
        user_profile_update_action.text = getString(R.string.updating)
        loggedInfo?.username = user_profil_input_layout?.editText?.text.toString()
        loggedInfo?.avatar = userChoosedImgUri?.toString()
        loggedInfo?.let { AppSetsLoginInfo.save(this, it) }

       /* runOnUiThread{

        }*/

        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Glide.get(applicationContext).clearMemory()
            }else {
                Glide.get(applicationContext).clearDiskCache()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        CoroutineScope(Default).launch {
            updateProfileInServer()
            withContext(Main){
                val allActivitys = getAllActivitys(this@UserProfileActivity)
                allActivitys?.forEach {
                    it.recreate()
                }
                user_profile_update_action.text = getString(R.string.successful)
            }
        }
    }
    private suspend fun updateProfileInServer(){
        delay(5000)
        //return 1
    }
    private fun fillCurrentAvatarAndUserName() {
        user_profil_input_layout?.editText?.hint = loggedInfo?.username
        Glide.with(this)
            .asBitmap()
            .load(loggedInfo?.avatar)
            .placeholder(R.color.colorTransparent)
            .transition(BitmapTransitionOptions().crossFade())
            .transform(CenterCrop())
            .into(user_avatar_choose_image)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            8989-> if(resultCode==Activity.RESULT_OK && data!=null){
                userChoosedImgUri = data.data
                user_avatar_choose_card.removeView(user_avatar_mask)
                Glide.with(this).asBitmap().load(userChoosedImgUri)
                    .placeholder(R.color.colorTransparent)
                    .transition(BitmapTransitionOptions().crossFade())
                    .transform(CenterCrop())
                    .into(user_avatar_choose_image)

                Log.d("Uri",userChoosedImgUri.toString())
            }else{
                Toast.makeText(this, getString(R.string.please_rechoose_image),Toast.LENGTH_LONG).show()
            }
        }
    }
}
