package com.kraaft.video.manager.ui.activity

import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.kraaft.video.manager.api.BaseUrlInterceptor
import com.kraaft.video.manager.databinding.ActivityStartBinding
import com.kraaft.video.manager.ui.base.BaseActivity
import com.kraaft.video.manager.utils.delayTask
import com.kraaft.video.manager.utils.gotoActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StartActivity : BaseActivity() {

    private var binding: ActivityStartBinding? = null

    @Inject
    lateinit var baseUrlInterceptor: BaseUrlInterceptor

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        baseUrlInterceptor.updateBaseUrl("")

        delayTask {
            gotoActivity(MainActivity::class.java, true)
        }
    }
}