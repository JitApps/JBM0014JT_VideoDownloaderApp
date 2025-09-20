package com.kraaft.video.manager.ui.start

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kraaft.video.manager.databinding.ActivityStartBinding
import com.kraaft.video.manager.ui.base.BaseActivity
import com.kraaft.video.manager.ui.main.MainActivity
import com.kraaft.video.manager.utils.delayTask
import com.kraaft.video.manager.utils.gotoActivity

class StartActivity : BaseActivity() {

    private var binding: ActivityStartBinding? = null

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        delayTask {
            gotoActivity(MainActivity::class.java, true)
        }
    }
}