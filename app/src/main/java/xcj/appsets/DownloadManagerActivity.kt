package xcj.appsets

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_download_manager.*
import xcj.appsets.ui.BaseActivity

class DownloadManagerActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_manager)
        val toolbar = toolbar
        setSupportActionBar(toolbar)
    }


}
