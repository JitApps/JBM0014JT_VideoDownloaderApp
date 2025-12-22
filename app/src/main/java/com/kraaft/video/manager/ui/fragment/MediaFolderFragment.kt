package com.kraaft.video.manager.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kraaft.video.manager.R
import com.kraaft.video.manager.databinding.FragmentMediaFolderBinding
import com.kraaft.video.manager.model.FolderCount
import com.kraaft.video.manager.model.UiState
import com.kraaft.video.manager.ui.adapter.AudioFolderAdapter
import com.kraaft.video.manager.ui.adapter.VideoFolderAdapter
import com.kraaft.video.manager.ui.base.BaseFragment
import com.kraaft.video.manager.ui.viewmodels.MediaViewModel
import com.kraaft.video.manager.utils.showError
import com.kraaft.video.manager.utils.showLoading
import com.kraaft.video.manager.utils.showPage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MediaFolderFragment : BaseFragment() {

    private var binding: FragmentMediaFolderBinding? = null
    private var isSound = false

    private var videoAdapter: VideoFolderAdapter? = null
    private var audioAdapter: AudioFolderAdapter? = null

    val viewModel: MediaViewModel by viewModels()

    companion object {
        fun getInstance(isSound: Boolean = false): MediaFolderFragment {
            return MediaFolderFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("isSound", isSound)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isSound = arguments?.getBoolean("isSound", false) ?: false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMediaFolderBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let {
            if (isSound) {
                it.initMusicRv()
            } else {
                it.initVideoRv()
            }
        }
    }

    fun Context.initMusicRv() {
        audioAdapter = AudioFolderAdapter(this) { item, position ->

        }
        binding?.rvMedia?.apply {
            layoutManager = LinearLayoutManager(this@initMusicRv)
            adapter = audioAdapter
        }
        observeAudioUiState()
    }

    fun Context.initVideoRv() {
        videoAdapter = VideoFolderAdapter(this) { item, position ->

        }
        binding?.rvMedia?.apply {
            layoutManager = LinearLayoutManager(this@initVideoRv)
            adapter = videoAdapter
        }
        observeVideoUiState()
    }


    private fun observeAudioUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiSoundFolderState.collectLatest { uiState ->
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

    private fun observeVideoUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiVideoFolderState.collectLatest { uiState ->
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

    private fun refreshVideoData(newList: List<FolderCount>) {
        videoAdapter?.refreshData(newList.toMutableList())
        binding?.let {
            it.includedError.showPage(it.cvMain)
        }
    }

    private fun refreshAudioData(newList: List<FolderCount>) {
        audioAdapter?.refreshData(newList.toMutableList())
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