package xcj.appsets.ui.preference

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.AsyncTask
import android.os.Bundle
import android.os.Looper
import android.view.View
import androidx.core.content.edit
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.settings_activity.*
import xcj.appsets.Constant
import xcj.appsets.R
import xcj.appsets.manager.LocaleManager
import xcj.appsets.model.AppSetsLoginInfo
import xcj.appsets.server.AppSetsServer
import xcj.appsets.ui.AboutActivity
import xcj.appsets.ui.BaseActivity
import xcj.appsets.ui.LoginActivity
import xcj.appsets.ui.fragment.UserProfileEditBottomSheetFragment
import xcj.appsets.util.PreferenceUtil
import xcj.appsets.util.TextUtil
import xcj.appsets.util.getSharedPreferences
import java.io.File
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*

class SettingsActivity : BaseActivity() {
    private val compositeDisposable = CompositeDisposable()
    private fun fillSettingUerProfil() {
        val appSetsLoginInfo = AppSetsLoginInfo.getSavedInstance(this)
        preference_userName.text = appSetsLoginInfo?.username
        Glide
            .with(this)
            .asBitmap()
            .load(appSetsLoginInfo?.avatar)
            .error(R.drawable.avatar)
            .transition(BitmapTransitionOptions().crossFade(100))
            .transform(CenterCrop(), RoundedCorners(250))
            .into(preference_userAvatar)
        /*.into(object : BitmapImageViewTarget(preference_userAvatar){
            override fun setResource(resource: Bitmap?) {
                var circular =
                    RoundedBitmapDrawableFactory.create(this@SettingsActivity.resources, resource)
                circular.isCircular = true
                preference_userAvatar.setImageDrawable(circular)
            }
        })*/
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings,
                SettingsFragment()
            )
            .commit()
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
        signout_button.setOnClickListener {
            compositeDisposable.add(Observable.fromCallable {
                AppSetsServer.userSignout(this)
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                    when (it) {
                        "SIGN_OUT_SUCCESSFUL" -> {
                            clearLoggedUserDataThenSignout()
                        }
                        "OPERATION_FAILED" -> {
                            clearLoggedUserDataThenSignout()
                        }
                    }
                }) {

                }
            )

        }
        fillSettingUerProfil()
    }

    private fun clearLoggedUserDataThenSignout() {
        getSharedPreferences(this).edit {
            clear()
            apply()
        }
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                runOnUiThread {
                    deleteFilesByDirectory(applicationContext.cacheDir)
                    deleteFilesByDirectory(applicationContext.filesDir)

                }
            }else{
                AsyncTask.execute{
                    Glide.get(applicationContext).clearDiskCache()
                    Glide.get(applicationContext).clearMemory()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        getAllActivitys(this)
            ?.let {
            for (activity in it) {
                if (activity is SettingsActivity) {
                    continue
                } else {
                    activity.finish()
                }
            }
        }
        startActivity(Intent(this, LoginActivity::class.java))
        this.finish()

    }
    companion object{
        @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
        fun getAllActivitys(context: Context): List<Activity>? {
            val list: MutableList<Activity> = ArrayList()
            try {
                context.classLoader.loadClass("android.app.ActivityThread")
                val activityThread = Class.forName("android.app.ActivityThread")
                val currentActivityThread: Method =
                    activityThread.getDeclaredMethod("currentActivityThread")
                currentActivityThread.isAccessible = true
                //获取主线程对象
                val activityThreadObject: Any? = currentActivityThread.invoke(null)
                val mActivitiesField: Field = activityThread.getDeclaredField("mActivities")
                mActivitiesField.isAccessible = true
                val mActivities = mActivitiesField.get(activityThreadObject) as Map<Any, Any>
                for ((_, value) in mActivities) {
                    val activityClientRecordClass: Class<*> = value.javaClass
                    val activityField: Field = activityClientRecordClass.getDeclaredField("activity")
                    activityField.isAccessible = true
                    val o: Any? = activityField.get(value)
                    list.add(o as Activity)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return list
        }
    }


    private fun deleteFilesByDirectory(directory: File?) {
        if (directory != null && directory.exists() && directory.isDirectory) {
            directory.listFiles()?.let {
                for (item in it) {
                    item.delete()
                }
            }
        }
    }

    class SettingsFragment : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener {
        private var mcontext: Context? = null
        private var localeManager: LocaleManager? = null
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }


        override fun onAttach(context: Context) {
            super.onAttach(context)
            mcontext = context
            localeManager = LocaleManager(context)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val mPreference =
                context?.getSharedPreferences("xcj.appsets_preferences", Context.MODE_PRIVATE)
            mPreference?.registerOnSharedPreferenceChangeListener(this)

            val localeList = findPreference<ListPreference>(Constant.PREFERENCE_LOCALE_LIST)!!

            val mThemeStyle = findPreference<ListPreference>(Constant.PREFERENCE_THEME)!!
            val about = findPreference<Preference>("PREFERENCE_ABOUT")!!
            about.setOnPreferenceClickListener {
                startActivity(Intent(requireContext(), AboutActivity::class.java))
                true
            }
            localeList.setOnPreferenceChangeListener { preference, newValue ->
                val choice = newValue.toString()
                if (TextUtil.isEmpty(choice)) {
                    PreferenceUtil.putBoolean(mcontext!!, Constant.PREFERENCE_LOCALE_CUSTOM, false)
                    localeManager?.setNewLocale(Locale.getDefault(), false)
                } else {
                    val lang = choice.split("-").toTypedArray()[0]
                    val country = choice.split("-").toTypedArray()[1]
                    val locale = Locale(lang, country)
                    localeManager?.setNewLocale(locale, true)
                    requireActivity().supportFragmentManager.popBackStack()
                    //requireActivity().recreate()
                    getAllActivitys(
                        requireContext()
                    )?.let {
                        for (activity in it) {
                            activity.recreate()
                        }
                    }

                }
                true
            }
            mThemeStyle.setOnPreferenceChangeListener { _, newValue ->
                PreferenceUtil.putString(mcontext!!, Constant.PREFERENCE_THEME, newValue.toString())
                if (activity != null) {
                    requireActivity().supportFragmentManager.popBackStack()
                    requireActivity().recreate()


                }
                true
            }
        }

        override fun onSharedPreferenceChanged(
            sharedPreferences: SharedPreferences?,
            key: String?
        ) {


        }

       /* private fun getAllActivitys(): List<Activity>? {
            val list: MutableList<Activity> = ArrayList()
            try {
                requireActivity().classLoader.loadClass("android.app.ActivityThread")
                val activityThread = Class.forName("android.app.ActivityThread")
                val currentActivityThread: Method =
                    activityThread.getDeclaredMethod("currentActivityThread")
                currentActivityThread.isAccessible = true
                //获取主线程对象
                val activityThreadObject: Any? = currentActivityThread.invoke(null)
                val mActivitiesField: Field = activityThread.getDeclaredField("mActivities")
                mActivitiesField.isAccessible = true
                val mActivities = mActivitiesField.get(activityThreadObject) as Map<Any, Any>
                for ((_, value) in mActivities) {
                    val activityClientRecordClass: Class<*> = value.javaClass
                    val activityField: Field =
                        activityClientRecordClass.getDeclaredField("activity")
                    activityField.isAccessible = true
                    val o: Any? = activityField.get(value)
                    list.add(o as Activity)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return list
        }*/

    }

    fun updateMyProfile(view: View) {
        //startActivity(Intent(this, UserProfileActivity::class.java))
        val userProfileEditBottomSheetFragment =
            UserProfileEditBottomSheetFragment()
        userProfileEditBottomSheetFragment.show(
            supportFragmentManager,
            userProfileEditBottomSheetFragment.tag
        )
    }
}