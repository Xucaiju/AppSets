package xcj.appsets.util

import android.R
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.ActivityOptions
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt

class ViewUtil {
    companion object{
        private const val ANIMATION_DURATION_SHORT = 250
        @JvmStatic
        fun dpToPx(context: Context, dp: Int): Int {
            val displayMetrics = context.resources.displayMetrics
            return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
        }
        @JvmStatic
        fun pxToDp(context: Context, px: Int): Int {
            val displayMetrics = context.resources.displayMetrics
            return (px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
        }
        @JvmStatic
        fun getStyledAttribute(context: Context, styleID: Int): Int {
            val arr =
                context.obtainStyledAttributes(TypedValue().data, intArrayOf(styleID))
            val styledColor = arr.getColor(0, Color.WHITE)
            arr.recycle()
            return styledColor
        }
        @JvmStatic
        fun showWithAnimation(view: View) {
            val mShortAnimationDuration = view.resources.getInteger(
                R.integer.config_shortAnimTime
            )
            view.alpha = 0f
            view.visibility = View.VISIBLE
            view.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration.toLong())
                .setListener(null)
        }
        @JvmStatic
        fun hideWithAnimation(view: View) {
            val mShortAnimationDuration = view.resources.getInteger(
                R.integer.config_shortAnimTime
            )
            view.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        view.visibility = View.GONE
                    }
                })
        }
        @JvmStatic
        fun rotateView(view: View, reverse: Boolean) {
            val animation = RotateAnimation(
                if (reverse) 180f else 0f,
                if (reverse) 0f else 180f,
                view.width.toFloat() / 2,
                view.height.toFloat() / 2
            )
            animation.duration = 300
            animation.fillAfter = true
            view.startAnimation(animation)
        }
        @JvmStatic
        fun expandView(v: View, targetHeight: Int) {
            val prevHeight = v.height
            v.visibility = View.VISIBLE
            val valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight)
            valueAnimator.interpolator = DecelerateInterpolator()
            valueAnimator.addUpdateListener { animation: ValueAnimator ->
                v.layoutParams.height = animation.animatedValue as Int
                v.requestLayout()
            }
            valueAnimator.duration = ANIMATION_DURATION_SHORT.toLong()
            valueAnimator.start()
        }
        @JvmStatic
        fun collapseView(v: View, targetHeight: Int) {
            val prevHeight = v.height
            val valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight)
            valueAnimator.interpolator = DecelerateInterpolator()
            valueAnimator.addUpdateListener { animation: ValueAnimator ->
                v.layoutParams.height = animation.animatedValue as Int
                v.requestLayout()
            }
            valueAnimator.interpolator = DecelerateInterpolator()
            valueAnimator.duration = ANIMATION_DURATION_SHORT.toLong()
            valueAnimator.start()
        }
        @JvmStatic
        fun setVisibility(view: View, visibility: Boolean) {
            if (visibility) showWithAnimation(view) else hideWithAnimation(view)
        }
        @JvmStatic
        fun setVisibility(
            view: View,
            visibility: Boolean,
            noAnim: Boolean
        ) {
            if (noAnim) view.visibility =
                if (visibility) View.VISIBLE else View.INVISIBLE else setVisibility(
                view,
                visibility
            )
        }
        @JvmStatic
        fun getEmptyActivityBundle(activity: AppCompatActivity?): Bundle? {
            return ActivityOptions.makeSceneTransitionAnimation(activity).toBundle()
        }
    }
}