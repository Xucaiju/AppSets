package xcj.appsets.model

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import xcj.appsets.Constant
import xcj.appsets.R
import xcj.appsets.util.Log
import xcj.appsets.util.ViewUtil

abstract class AbstractDetails(var view: View?, var app: App?) {
    protected var context: Context? = view?.context

    /*constructor(activity: ManualDownloadActivity?, app: App) {
        this.app = app
        ButterKnife.bind(activity)
    }*/

    abstract fun draw()
/*    protected fun showDevApps() {
        val intent = Intent(context, DevAppsActivity::class.java)
        intent.putExtra("SearchQuery", Constants.PUB_PREFIX + app.getDeveloperName())
        intent.putExtra("SearchTitle", app.getDeveloperName())
        context!!.startActivity(intent, ViewUtil.getEmptyActivityBundle(activity))
    }*/

    protected fun setText(viewId: Int, text: String) {
        val textView: TextView? = view?.findViewById(viewId)
        if (textView != null) {
            if (text.isEmpty()) textView.visibility = View.GONE else textView.text = text
        }
    }

    protected fun setText(viewId: Int, stringId: Int, vararg text: Any?) {
        view?.resources?.getString(stringId, text)?.let { setText(viewId, it) }
    }

    protected fun hide(viewID: Int) {
        view?.findViewById<View>(viewID)?.visibility = View.GONE
    }

    protected fun show(viewGroup: ViewGroup?, vararg viewIds: Int) {
        for (viewId in viewIds) {
            view?.findViewById<View>(viewId)?.visibility = View.VISIBLE
        }
    }

    protected fun show(vararg viewIds: Int) {
        for (viewId in viewIds) {
            view?.findViewById<View>(viewId)?.visibility = View.VISIBLE
        }
    }

    protected fun showPurchaseDialog() {

        context?.also {
            val builder = MaterialAlertDialogBuilder(context)
                .setTitle(it.getString(R.string.dialog_purchase_title))
                .setMessage(it.getString(R.string.dialog_purchase_desc))
                .setPositiveButton(it.getString(R.string.dialog_purchase_positive))
                { dialog: DialogInterface?, which: Int ->
                    openWebView(
                        Constant.APP_DETAIL_URL + app?.getPackageName()
                    )
                }

                .setNegativeButton(
                    context?.getString(R.string.action_later)
                ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
            val backGroundColor: Int =
                ViewUtil.getStyledAttribute(it, android.R.attr.colorBackground)
            builder.setBackground(ColorDrawable(backGroundColor))
            builder.create()
            builder.show()
        }


    }

    protected fun showDialog(@StringRes titleId: Int, @StringRes messageId: Int) {
        val builder = MaterialAlertDialogBuilder(context)
        builder.setTitle(titleId)
        builder.setMessage(messageId)
        builder.setPositiveButton(
            android.R.string.ok
        ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
        val backGroundColor: Int? =
            context?.let { ViewUtil.getStyledAttribute(it, android.R.attr.colorBackground) }
        builder.setBackground(backGroundColor?.let { ColorDrawable(it) })
        builder.create()
        builder.show()
    }

    private fun openWebView(URL: String) {
        try {
            context!!.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(URL)))
        } catch (e: Exception) {
            Log.e("No WebView found !")
        }
    }
}