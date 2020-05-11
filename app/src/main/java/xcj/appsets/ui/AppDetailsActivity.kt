package xcj.appsets.ui

import android.os.Bundle
import com.skydoves.transformationlayout.TransformationAppCompatActivity
import xcj.appsets.R
import xcj.appsets.model.App

class AppDetailsActivity : TransformationAppCompatActivity() {

    companion object {
        var app: App? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_details)
    }


}