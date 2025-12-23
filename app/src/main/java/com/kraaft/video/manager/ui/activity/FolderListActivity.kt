package com.kraaft.video.manager.ui.activity

import android.content.Context
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kraaft.video.manager.R
import com.kraaft.video.manager.databinding.ActivityDownloadBinding
import com.kraaft.video.manager.databinding.ActivityFolderListBinding
import com.kraaft.video.manager.model.UiState
import com.kraaft.video.manager.ui.adapter.AudioListAdapter
import com.kraaft.video.manager.ui.adapter.VideoListAdapter
import com.kraaft.video.manager.ui.base.BaseActivity
import com.kraaft.video.manager.ui.viewmodels.MediaViewModel
import com.kraaft.video.manager.utils.FILE_VIDEO
import com.kraaft.video.manager.utils.beVisible
import com.kraaft.video.manager.utils.onSingleClick
import com.kraaft.video.manager.utils.showError
import com.kraaft.video.manager.utils.showLoading
import com.kraaft.video.manager.utils.showPage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class FolderListActivity : BaseActivity() {

    private var binding: ActivityFolderListBinding? = null

    private var videoAdapter: VideoListAdapter? = null
    private var audioAdapter: AudioListAdapter? = null

    val viewModel: MediaViewModel by viewModels()
    var folderPath: String = ""
    var fileType: Int = FILE_VIDEO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFolderListBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        getIntentData()
        onClick()
        if (fileType == FILE_VIDEO) initVideoRv() else initMusicRv()
        observeFileUiState()
    }

    fun getIntentData() {
        fileType = intent.getIntExtra("fileType", FILE_VIDEO)
        folderPath = intent.getStringExtra("folderPath") ?: ""
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

    fun Context.initMusicRv() {
        audioAdapter = AudioListAdapter(this, onMenuClick = { item, isAdd ->
            if (isAdd) {
                viewModel.addToPlaylist(item, "sound")
            } else {
                viewModel.removeFromPlaylist(item)
            }
        }, onClickListener = { item, position ->

        })
        binding?.rvMedia?.apply {
            layoutManager = LinearLayoutManager(this@initMusicRv)
            adapter = audioAdapter
        }
    }

    fun Context.initVideoRv() {
        videoAdapter = VideoListAdapter(this) { item, position ->

        }
        binding?.rvMedia?.apply {
            layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
            adapter = videoAdapter
        }
    }

    private fun observeFileUiState() {
        lifecycleScope.launch {
            viewModel.playData.collectLatest { uiState ->
                uiState?.let {
                    when (uiState) {

                        is UiState.Loading -> {
                            binding?.let {
                                it.includedError.showLoading(it.cvMain)
                            }
                        }

                        is UiState.Success -> {
                            if (fileType == FILE_VIDEO) {
                                videoAdapter?.refreshData(uiState.data.toMutableList())
                            } else {
                                audioAdapter?.refreshData(uiState.data.toMutableList())
                            }
                            binding?.let {
                                it.includedError.showPage(it.cvMain)
                            }
                        }

                        is UiState.Error -> {
                            showErrorOrEmpty(uiState.message)
                        }

                        is UiState.Empty -> {
                            showErrorOrEmpty()
                        }
                    }
                } ?: run {
                    viewModel.fetchFolderFiles(folderPath, fileType)
                }
            }
        }
    }


    fun showErrorOrEmpty(message: String = getString(R.string.kk_error_no_data)) {
        binding?.let {
            it.includedError.showError(message, it.cvMain)
        }
    }
}