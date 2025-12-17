package com.kraaft.video.manager.ui.activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.kraaft.video.manager.ui.adapter.PagerFragmentAdapter
import com.kraaft.video.manager.databinding.ActivityStatusBinding
import com.kraaft.video.manager.ui.base.BaseActivity
import com.kraaft.video.manager.ui.fragment.FileListFragment
import com.kraaft.video.manager.utils.beVisible
import com.kraaft.video.manager.utils.getBusinessFolder
import com.kraaft.video.manager.utils.getStatusFolder
import com.kraaft.video.manager.utils.onSingleClick

class StatusActivity : BaseActivity() {

    private var binding: ActivityStatusBinding? = null

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatusBinding.inflate(layoutInflater)
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
            "Whatsapp", "Whatsapp Business"
        )
        binding?.let {
            it.viewPager.apply {
                adapter = PagerFragmentAdapter(supportFragmentManager, lifecycle).apply {
                    loadFragments().forEachIndexed { index, fragment ->
                        addFragment(fragment, tabList[index])
                    }
                }
            }
            TabLayoutMediator(it.tabRS, it.viewPager) { tab, position ->
                tab.text = tabList[position]
            }.attach()
        }
    }


    private fun loadFragments(): List<Fragment> {
        return listOf<Fragment>(
            FileListFragment.getInstance(getStatusFolder(), true),
            FileListFragment.getInstance(getBusinessFolder(), true)
        )
    }

}