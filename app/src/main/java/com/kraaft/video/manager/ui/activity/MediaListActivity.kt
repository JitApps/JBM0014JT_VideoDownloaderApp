package com.kraaft.video.manager.ui.activity

import android.R.attr.onClick
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.kraaft.video.manager.R
import com.kraaft.video.manager.databinding.ActivityHomeBinding
import com.kraaft.video.manager.databinding.ActivityMediaListBinding
import com.kraaft.video.manager.ui.adapter.PagerFragmentAdapter
import com.kraaft.video.manager.ui.base.BaseActivity
import com.kraaft.video.manager.ui.fragment.FileListFragment
import com.kraaft.video.manager.ui.fragment.MediaFileFragment
import com.kraaft.video.manager.ui.fragment.MediaFolderFragment
import com.kraaft.video.manager.utils.beVisible
import com.kraaft.video.manager.utils.getBusinessFolder
import com.kraaft.video.manager.utils.getStatusFolder
import com.kraaft.video.manager.utils.onSingleClick

class MediaListActivity : BaseActivity() {

    private var binding: ActivityMediaListBinding? = null
    private var isSound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaListBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        isSound = intent.getBooleanExtra("isSound", false)
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
            if (isSound) "Music" else "Videos", "Folder", "PlayList"
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
            MediaFileFragment.getInstance(isSound),
            MediaFolderFragment.getInstance(isSound),
            MediaFileFragment.getInstance(isSound, true),
        )
    }
}