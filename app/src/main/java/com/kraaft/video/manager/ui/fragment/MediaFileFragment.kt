package com.kraaft.video.manager.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kraaft.video.manager.R
import com.kraaft.video.manager.databinding.FragmentMediaFileBinding
import com.kraaft.video.manager.model.FileEntity
import com.kraaft.video.manager.model.UiState
import com.kraaft.video.manager.ui.adapter.AudioListAdapter
import com.kraaft.video.manager.ui.adapter.VideoListAdapter
import com.kraaft.video.manager.ui.base.BaseFragment
import com.kraaft.video.manager.ui.viewmodels.MediaViewModel
import com.kraaft.video.manager.utils.FILE_AUDIO
import com.kraaft.video.manager.utils.FILE_VIDEO
import com.kraaft.video.manager.utils.showError
import com.kraaft.video.manager.utils.showLoading
import com.kraaft.video.manager.utils.showPage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MediaFileFragment : BaseFragment() {

    private var binding: FragmentMediaFileBinding? = null
    private var fileType = FILE_VIDEO
    private var isPlayList = false
    private var videoAdapter: VideoListAdapter? = null
    private var audioAdapter: AudioListAdapter? = null

    val viewModel: MediaViewModel by viewModels()

    companion object {
        fun getInstance(fileType: Int = FILE_VIDEO, isPlayList: Boolean = false): MediaFileFragment {
            return MediaFileFragment().apply {
                arguments = Bundle().apply {
                    putInt("fileType", fileType)
                    putBoolean("isPlayList", isPlayList)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileType = arguments?.getInt("fileType", FILE_VIDEO) ?: FILE_VIDEO
        isPlayList = arguments?.getBoolean("isPlayList", false) ?: false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMediaFileBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (fileType == FILE_AUDIO) context?.initMusicRv() else context?.initVideoRv()
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
        if (isPlayList) {
            observePlayUiState()
        } else
            observeAudioUiState()
    }

    fun Context.initVideoRv() {
        videoAdapter = VideoListAdapter(this) { item, position ->

        }
        binding?.rvMedia?.apply {
            layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
            adapter = videoAdapter
        }
        if (isPlayList) {
            observePlayUiState()
        } else
            observeVideoUiState()
    }

    private fun observePlayUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.playData.collectLatest { uiState ->
                uiState?.let {
                    when (uiState) {

                        is UiState.Loading -> {
                            binding?.let {
                                it.includedError.showLoading(it.cvMain)
                            }
                        }

                        is UiState.Success -> {
                            refreshAudioData(uiState.data)
                        }

                        is UiState.Error -> {
                            showErrorOrEmpty(uiState.message)
                        }

                        is UiState.Empty -> {
                            showErrorOrEmpty()
                        }
                    }
                } ?: run {
                    viewModel.fetchPlayList(if (fileType == FILE_AUDIO) "sound" else "video")
                }
            }
        }
    }

    private fun observeVideoUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiVideoState.collectLatest { uiState ->
                when (uiState) {

                    is UiState.Loading -> {
                        binding?.let {
                            it.includedError.showLoading(it.cvMain)
                        }
                    }

                    is UiState.Success -> {
                        refreshVideoData(uiState.data)
                    }

                    is UiState.Error -> {
                        showErrorOrEmpty(uiState.message)
                    }

                    is UiState.Empty -> {
                        showErrorOrEmpty()
                    }
                }
            }
        }
    }

    private fun observeAudioUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiSoundState.collectLatest { uiState ->
                when (uiState) {

                    is UiState.Loading -> {
                        binding?.let {
                            it.includedError.showLoading(it.cvMain)
                        }
                    }

                    is UiState.Success -> {
                        refreshAudioData(uiState.data)
                    }

                    is UiState.Error -> {
                        showErrorOrEmpty(uiState.message)
                    }

                    is UiState.Empty -> {
                        showErrorOrEmpty()
                    }
                }
            }
        }
    }

    private fun refreshAudioData(newList: List<FileEntity>) {
        audioAdapter?.refreshData(newList.toMutableList())
        binding?.let {
            it.includedError.showPage(it.cvMain)
        }
    }

    private fun refreshVideoData(newList: List<FileEntity>) {
        videoAdapter?.refreshData(newList.toMutableList())
        binding?.let {
            it.includedError.showPage(it.cvMain)
        }
    }

    fun showErrorOrEmpty(message: String = getString(R.string.kk_error_no_data)) {
        binding?.let {
            it.includedError.showError(message, it.cvMain)
        }
    }
}