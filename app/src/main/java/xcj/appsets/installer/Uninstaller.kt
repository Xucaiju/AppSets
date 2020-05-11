package xcj.appsets.installer

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import xcj.appsets.Constant
import xcj.appsets.R
import xcj.appsets.model.App
import xcj.appsets.util.PreferenceUtil
import xcj.appsets.util.ViewUtil

class Uninstaller(context: Context) {
    var context = context
    fun uninstall(app: App?) {
        when (PreferenceUtil.getString(context, Constant.PREFERENCE_INSTALLATION_METHOD)) {
            "0", "2" -> uninstallByPackageManager(app!!)
            "1" -> askUninstall(app!!)
            else -> uninstallByPackageManager(app!!)
        }
    }
    private fun uninstallByPackageManager(app: App) {
        val uri = Uri.fromParts("package", app.getPackageName(), null)
        val intent = Intent()
        intent.data = uri
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            intent.action = Intent.ACTION_DELETE
        } else {
            intent.action = Intent.ACTION_UNINSTALL_PACKAGE
            intent.putExtra(Intent.EXTRA_RETURN_RESULT, true)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
    private fun uninstallByRoot(app: App) {
        RootedUninstaller().uninstall(app)
    }
    private fun askUninstall(app: App){
        var materialAlterDialogBuilder = MaterialAlertDialogBuilder(context)
            .setTitle(app.getDisplayName())
            .setMessage(context.getString(R.string.dialog_uninstall_confirmation))
            .setPositiveButton(context.getString(android.R.string.ok)
            ) { _, _ -> uninstallByRoot(app) }
            .setNegativeButton(context.getString(android.R.string.cancel))
            { dialog, _ -> dialog.dismiss() }
        var backGroundColor = ViewUtil.getStyledAttribute(context, android.R.attr.colorBackground)
        materialAlterDialogBuilder.background = ColorDrawable(backGroundColor)
        materialAlterDialogBuilder.create().show()

    }

}