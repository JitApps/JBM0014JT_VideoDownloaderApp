package com.kraaft.video.manager.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kraaft.video.manager.R
import com.kraaft.video.manager.databinding.FragmentFileListBinding
import com.kraaft.video.manager.databinding.FragmentMediaFileBinding
import com.kraaft.video.manager.model.UiState
import com.kraaft.video.manager.model.VideoFile
import com.kraaft.video.manager.model.VideoModel
import com.kraaft.video.manager.ui.adapter.VideoFolderAdapter
import com.kraaft.video.manager.ui.adapter.VideoListAdapter
import com.kraaft.video.manager.ui.base.BaseFragment
import com.kraaft.video.manager.ui.viewmodels.MediaViewModel
import com.kraaft.video.manager.utils.showError
import com.kraaft.video.manager.utils.showLoading
import com.kraaft.video.manager.utils.showPage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue


@AndroidEntryPoint
class MediaFileFragment : BaseFragment() {

    private var binding: FragmentMediaFileBinding? = null
    private var isSound = false
    private var videoAdapter: VideoListAdapter? = null

    val viewModel: MediaViewModel by viewModels()

    companion object {
        fun getInstance(isSound: Boolean = false): MediaFileFragment {
            return MediaFileFragment().apply {
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
        binding = FragmentMediaFileBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let {
            if (isSound) it.initMusicRv() else it.initVideoRv()
        }
    }

    fun Context.initMusicRv() {
        videoAdapter = VideoListAdapter(this) { item, position ->

        }
        binding?.rvMedia?.apply {
            layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
            adapter = videoAdapter
        }
    }

    fun Context.initVideoRv() {
        videoAdapter = VideoListAdapter(this) { item, position ->

        }
        binding?.rvMedia?.apply {
            layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
            adapter = videoAdapter
        }
        observeVideoUiState()
    }

    private fun observeVideoUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.videoData.collectLatest { uiState ->
                uiState?.let {
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
                } ?: run {
                    viewModel.fetchVideo()
                }
            }
        }
    }

    private fun refreshVideoData(newList: List<VideoFile>) {
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