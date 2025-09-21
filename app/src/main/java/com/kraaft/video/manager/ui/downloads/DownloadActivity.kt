package com.kraaft.video.manager.ui.downloads

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.kraaft.driver.manager.ui.main.PagerFragmentAdapter
import com.kraaft.video.manager.databinding.ActivityDownloadBinding
import com.kraaft.video.manager.databinding.ActivityStatusBinding
import com.kraaft.video.manager.ui.base.BaseActivity
import com.kraaft.video.manager.ui.files.FileListFragment
import com.kraaft.video.manager.utils.getBusinessFolder
import com.kraaft.video.manager.utils.getChingariPath
import com.kraaft.video.manager.utils.getJoshPath
import com.kraaft.video.manager.utils.getStatusFolder
import com.kraaft.video.manager.utils.getWhatsPath
import com.kraaft.video.manager.utils.onSingleClick

class DownloadActivity : BaseActivity() {

    private var binding: ActivityDownloadBinding? = null

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownloadBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        onClick()
        setViewPager()
    }

    private fun onClick() {
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
        binding?.includedToolbar?.btnBack?.onSingleClick {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setViewPager() {
        val tabList = listOf(
            "Whatsapp", "Chingari", "Josh"
        )
        binding?.let {
            it.viewPager.apply {
                adapter = PagerFragmentAdapter(supportFragmentManager, lifecycle).apply {
                    loadFragments().forEachIndexed { index, fragment ->
                        addFragment(fragment, tabList[index])
                    }
                }
                offscreenPageLimit = 3
            }
            TabLayoutMediator(it.tabRS, it.viewPager) { tab, position ->
                tab.text = tabList[position]
            }.attach()
        }
    }


    private fun loadFragments(): List<Fragment> {
        return listOf<Fragment>(
            FileListFragment.getInstance(getWhatsPath(), false),
            FileListFragment.getInstance(getChingariPath(), false),
            FileListFragment.getInstance(getJoshPath(), false)
        )
    }
}