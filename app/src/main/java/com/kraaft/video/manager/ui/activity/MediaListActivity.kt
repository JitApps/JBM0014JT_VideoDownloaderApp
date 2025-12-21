package com.kraaft.video.manager.ui.activity

import android.R.attr.onClick
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.kraaft.video.manager.R
import com.kraaft.video.manager.databinding.ActivityHomeBinding
import com.kraaft.video.manager.databinding.ActivityMediaListBinding
import com.kraaft.video.manager.ui.adapter.PagerFragmentAdapter
import com.kraaft.video.manager.ui.base.BaseActivity
import com.kraaft.video.manager.ui.fragment.FileListFragment
import com.kraaft.video.manager.ui.fragment.MediaFileFragment
import com.kraaft.video.manager.ui.fragment.MediaFolderFragment
import com.kraaft.video.manager.ui.viewmodels.MediaViewModel
import com.kraaft.video.manager.utils.beVisible
import com.kraaft.video.manager.utils.getBusinessFolder
import com.kraaft.video.manager.utils.getStatusFolder
import com.kraaft.video.manager.utils.onSingleClick
import kotlin.getValue

class MediaListActivity : BaseActivity() {

    private var binding: ActivityMediaListBinding? = null
    private var isSound = false

    val viewModel: MediaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaListBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        isSound = intent.getBooleanExtra("isSound", false)
        if (isSound) {
            viewModel.syncAndObserveSounds()
        } else {
            viewModel.syncAndObserveVideos()
        }
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