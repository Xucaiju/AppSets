package xcj.appsets.util

import android.app.Activity
import android.view.View
import com.leaf.library.StatusBarUtil

fun immerseBar(activity: Activity,view:View){
    activity.isImmersive = true
    StatusBarUtil.setTransparentForWindow(activity)
    StatusBarUtil.setDarkMode(activity)
    StatusBarUtil.setPaddingTop(activity, view)
}