package com.kraaft.video.manager.ui.activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.kraaft.video.manager.databinding.ActivityMediaListBinding
import com.kraaft.video.manager.ui.adapter.PagerFragmentAdapter
import com.kraaft.video.manager.ui.base.BaseActivity
import com.kraaft.video.manager.ui.fragment.MediaFileFragment
import com.kraaft.video.manager.ui.fragment.MediaFolderFragment
import com.kraaft.video.manager.ui.viewmodels.MediaViewModel
import com.kraaft.video.manager.utils.FILE_AUDIO
import com.kraaft.video.manager.utils.FILE_VIDEO
import com.kraaft.video.manager.utils.beVisible
import com.kraaft.video.manager.utils.onSingleClick
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class MediaListActivity : BaseActivity() {

    private var binding: ActivityMediaListBinding? = null
    private var fileType = FILE_VIDEO

    val viewModel: MediaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaListBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        getIntentData()
        onClick()
        setViewPager()
        if (fileType == FILE_AUDIO) {
            viewModel.syncAndObserveSounds()
        } else {
            viewModel.syncAndObserveVideos()
        }
    }

    fun getIntentData() {
        fileType = intent.getIntExtra("fileType", FILE_VIDEO)
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
            if (fileType == FILE_AUDIO) "Music" else "Videos", "Folder", "PlayList"
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
            MediaFileFragment.getInstance(fileType),
            MediaFolderFragment.getInstance(fileType),
            MediaFileFragment.getInstance(fileType, true),
        )
    }
}