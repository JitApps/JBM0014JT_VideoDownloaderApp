package com.kraaft.video.manager.ui.activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.kraaft.video.manager.databinding.ActivityDownloadBinding
import com.kraaft.video.manager.ui.adapter.PagerFragmentAdapter
import com.kraaft.video.manager.ui.base.BaseActivity
import com.kraaft.video.manager.ui.fragment.FileListFragment
import com.kraaft.video.manager.utils.beVisible
import com.kraaft.video.manager.utils.getDownloadsPath
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
        binding?.includedToolbar?.btnBack?.beVisible()
        binding?.includedToolbar?.btnBack?.onSingleClick {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setViewPager() {
        val tabList = listOf(
            "Whatsapp", "Downloads"
        )
        binding?.let {
            it.viewPager.apply {
                adapter = PagerFragmentAdapter(supportFragmentManager, lifecycle).apply {
                    loadFragments().forEachIndexed { index, fragment ->
                        addFragment(fragment, tabList[index])
                    }
                }
                offscreenPageLimit = 2
            }
            TabLayoutMediator(it.tabRS, it.viewPager) { tab, position ->
                tab.text = tabList[position]
            }.attach()
        }
    }


    private fun loadFragments(): List<Fragment> {
        return listOf<Fragment>(
            FileListFragment.Companion.getInstance(getWhatsPath(), false),
            FileListFragment.Companion.getInstance(getDownloadsPath(), false)
        )
    }
}