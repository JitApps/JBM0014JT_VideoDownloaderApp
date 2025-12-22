package com.kraaft.video.manager.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kraaft.video.manager.R
import com.kraaft.video.manager.databinding.ActivityHomeBinding
import com.kraaft.video.manager.databinding.ActivityMainBinding
import com.kraaft.video.manager.ui.base.BaseActivity
import com.kraaft.video.manager.utils.beVisible
import com.kraaft.video.manager.utils.gotoActivity
import com.kraaft.video.manager.utils.gotoIntent
import com.kraaft.video.manager.utils.onSingleClick
import dagger.hilt.android.AndroidEntryPoint
import kotlin.jvm.java

@AndroidEntryPoint
class HomeActivity : BaseActivity() {
    private var binding: ActivityHomeBinding? = null

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        onClick()
    }

    private fun onClick() {

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        binding?.apply {
            includedToolbar.btnBack.beVisible()
            includedToolbar.btnBack.onSingleClick {
                onBackPressedDispatcher.onBackPressed()
            }

            btnStatus.onSingleClick {
                gotoActivity(StatusActivity::class.java, false)
            }

            btnJosh.onSingleClick {
                gotoIntent(Intent(this@HomeActivity, GlobalActivity::class.java).apply {
                    putExtra("download", "josh")
                }, false)
            }

            btnChingari.onSingleClick {
                gotoIntent(Intent(this@HomeActivity, GlobalActivity::class.java).apply {
                    putExtra("download", "chingari")
                }, false)
            }
        }
    }

}