package xcj.appsets.util

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.github.zackratos.ultimatebar.UltimateBar
import xcj.appsets.Constant
import xcj.appsets.R
import xcj.appsets.manager.LocaleManager

class ThemeUtil {
    /**
     *cuttentTheme=0 Light Theme
     */
    private var currentTheme = 0
    fun onCreate(activity: AppCompatActivity) {
        LocaleManager(activity).setLocale()
        currentTheme = getSelectedTheme(activity)
        activity.setTheme(currentTheme)
    }

    fun onResume(activity: AppCompatActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity.isImmersive = true
        }
        when(currentTheme){
            R.style.AppTheme -> {

                UltimateBar.Companion.with(activity)
                    .statusDark(true)                  // 状态栏灰色模式(Android 6.0+)，默认 flase
                    .statusDrawable2(activity.getDrawable(R.drawable.statusbar_drawable))         // Android 6.0 以下状态栏灰色模式时状态栏颜色
                    .applyNavigation(true)              // 应用到导航栏，默认 flase
                    .navigationDark(true)              // 导航栏灰色模式(Android 8.0+)，默认 false
                    .navigationDrawable2(activity.getDrawable(R.drawable.navbar_drawable))     // Android 8.0 以下导航栏灰色模式时导航栏颜色
                    .create()
                    .immersionBar()
            }
            R.style.AppTheme_Black,R.style.AppTheme_Dark -> {

                UltimateBar.Companion.with(activity)
                    .statusDark(false)                  // 状态栏灰色模式(Android 6.0+)，默认 flase
                    .statusDrawable2(activity.getDrawable(R.drawable.statusbar_drawable))         // Android 6.0 以下状态栏灰色模式时状态栏颜色
                    .applyNavigation(true)              // 应用到导航栏，默认 flase
                    .navigationDark(true)              // 导航栏灰色模式(Android 8.0+)，默认 false
                    .navigationDrawable2(activity.getDrawable(R.drawable.navbar_drawable))     // Android 8.0 以下导航栏灰色模式时导航栏颜色
                    .create()
                    .immersionBar()
            }

            else -> {
                UltimateBar.Companion.with(activity)
                    .statusDark(false)                  // 状态栏灰色模式(Android 6.0+)，默认 flase
                    .statusDrawable2(activity.getDrawable(R.drawable.statusbar_drawable))         // Android 6.0 以下状态栏灰色模式时状态栏颜色
                    .applyNavigation(true)              // 应用到导航栏，默认 flase
                    .navigationDark(true)              // 导航栏灰色模式(Android 8.0+)，默认 false
                    .navigationDrawable2(activity.getDrawable(R.drawable.navbar_drawable))     // Android 8.0 以下导航栏灰色模式时导航栏颜色
                    .create()
                    .immersionBar()
            }
        }
        if (currentTheme != getSelectedTheme(activity)) {
            val intent = activity.intent
            activity.finish()
            activity.startActivity(intent)
        }
    }

    companion object {
        fun getSelectedTheme(activity: AppCompatActivity?): Int {
            return when (getTheme(activity)) {
                "light" -> R.style.AppTheme
                "dark" -> R.style.AppTheme_Dark
                "black" -> R.style.AppTheme_Black
                "follow_system" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val mode = activity?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
                        if(mode==Configuration.UI_MODE_NIGHT_YES){
                            R.style.AppTheme_Dark
                        }else{
                            R.style.AppTheme
                        }
                    }else{
                        R.style.AppTheme
                    }
                }
                else -> R.style.AppTheme
            }
        }

        fun isLightTheme(context: Context?): Boolean {
            return when (getTheme(context)) {
                "dark", "black" -> false
                else -> true
            }
        }
        private fun getTheme(context: Context?): String? = getSharedPreferences(context!!).getString(Constant.PREFERENCE_THEME, "light")
    }
}