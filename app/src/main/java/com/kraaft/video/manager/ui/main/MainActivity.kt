package com.kraaft.video.manager.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kraaft.video.manager.databinding.ActivityMainBinding
import com.kraaft.video.manager.ui.base.BaseActivity

class MainActivity : BaseActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setEdgeToEdge()
    }

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
}