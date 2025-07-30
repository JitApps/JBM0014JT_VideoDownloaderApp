package com.kraaft.video.manager.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kraaft.video.manager.databinding.ActivityMainBinding
import com.kraaft.video.manager.ui.base.BaseActivity
import com.kraaft.video.manager.ui.status.StatusActivity
import com.kraaft.video.manager.utils.gotoActivity
import com.kraaft.video.manager.utils.onSingleClick

class MainActivity : BaseActivity() {

    private var binding: ActivityMainBinding? = null

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
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setEdgeToEdge()
        onClick()
    }

    private fun onClick() {
        binding?.apply {
            btnApp.onSingleClick {
                gotoActivity(StatusActivity::class.java, false)
            }
        }
    }

}