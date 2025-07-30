package com.kraaft.video.manager.ui.status

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.kraaft.driver.manager.ui.main.PagerFragmentAdapter
import com.kraaft.video.manager.databinding.ActivityStatusBinding
import com.kraaft.video.manager.ui.base.BaseActivity
import com.kraaft.video.manager.ui.files.FileListFragment
import com.kraaft.video.manager.utils.getBusinessFilesFolder
import com.kraaft.video.manager.utils.getBusinessFolder
import com.kraaft.video.manager.utils.getStatusFilesFolder
import com.kraaft.video.manager.utils.getStatusFolder

class StatusActivity : BaseActivity() {

    private var binding: ActivityStatusBinding? = null

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
        binding = ActivityStatusBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setEdgeToEdge()
        setViewPager()
    }

    private fun setViewPager() {
        binding?.let {
            it.viewPager.apply {
                adapter = PagerFragmentAdapter(supportFragmentManager, lifecycle).apply {
                    loadFragments().forEach { item ->
                        addFragment(item, "")
                    }
                }
            }
        }
    }


    private fun loadFragments(): List<Fragment> {
        return listOf<Fragment>(
            FileListFragment.getInstance(getStatusFolder(), true),
            FileListFragment.getInstance(getBusinessFolder(), true)
        )
    }

}