package com.kraaft.video.manager.ui.josh

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kraaft.video.manager.R
import com.kraaft.video.manager.databinding.ActivityJoshBinding
import com.kraaft.video.manager.databinding.ActivityMainBinding
import com.kraaft.video.manager.ui.status.StatusActivity
import com.kraaft.video.manager.utils.gotoActivity
import com.kraaft.video.manager.utils.onSingleClick

class JoshActivity : AppCompatActivity() {
    private var binding: ActivityJoshBinding? = null

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
        binding = ActivityJoshBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setEdgeToEdge()
        onClick()
    }

    private fun onClick() {
        binding?.apply {

        }
    }
}