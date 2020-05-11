package xcj.appsets.util

import android.content.Context

class DensityUtil {
    companion object{
        @JvmStatic
        fun dip2px(context: Context, dp:Float):Float{
            val scale = context.resources.displayMetrics.density
            return dp*scale+0.5f
        }
        @JvmStatic
        fun px2dip(context: Context, px:Float):Float{
            val scale = context.resources.displayMetrics.density
            return px/scale+0.5f
        }
    }
}