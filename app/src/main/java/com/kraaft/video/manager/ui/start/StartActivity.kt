package com.kraaft.video.manager.ui.start

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kraaft.video.manager.databinding.ActivityStartBinding
import com.kraaft.video.manager.ui.main.MainActivity
import com.kraaft.video.manager.utils.delayTask
import com.kraaft.video.manager.utils.gotoActivity

class StartActivity : AppCompatActivity() {

    private var binding: ActivityStartBinding? = null

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun setEdgeToEdge() {
        binding?.apply {
            ViewCompat.setOnApplyWindowInsetsListener(main) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setEdgeToEdge()
        delayTask {
            gotoActivity(MainActivity::class.java, true)
        }
    }
}