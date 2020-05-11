package xcj.appsets.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import xcj.appsets.R

class RecoverPasswordActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_password)
    }
    fun toAbout(view: View) {
        startActivity(Intent(this, AboutActivity::class.java))
    }
}
