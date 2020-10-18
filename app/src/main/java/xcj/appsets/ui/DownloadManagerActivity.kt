package xcj.appsets.ui

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_download_manager.*
import xcj.appsets.R

class DownloadManagerActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_manager)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        val toolbar = toolbar
        setSupportActionBar(toolbar)
    }


}
