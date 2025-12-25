package com.kraaft.video.manager.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kraaft.video.manager.R
import com.kraaft.video.manager.databinding.FragmentDownloadBinding
import com.kraaft.video.manager.model.FileEntity
import com.kraaft.video.manager.model.UiState
import com.kraaft.video.manager.ui.adapter.VideoListAdapter
import com.kraaft.video.manager.ui.base.BaseFragment
import com.kraaft.video.manager.ui.viewmodels.DownloadViewModel
import com.kraaft.video.manager.utils.FILE_OTHER_DOWNLOAD
import com.kraaft.video.manager.utils.showError
import com.kraaft.video.manager.utils.showLoading
import com.kraaft.video.manager.utils.showPage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DownloadFragment : BaseFragment() {

    private var binding: FragmentDownloadBinding? = null
    private var videoAdapter: VideoListAdapter? = null
    var fileType = FILE_OTHER_DOWNLOAD
    val viewModel: DownloadViewModel by viewModels()

    companion object {
        fun getInstance(fileType: Int): DownloadFragment {
            return DownloadFragment().apply {
                arguments = Bundle().apply {
                    putInt("fileType", fileType)
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileType = arguments?.getInt("fileType", FILE_OTHER_DOWNLOAD) ?: FILE_OTHER_DOWNLOAD
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDownloadBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClick()
        context?.initVideoRv()
        viewModel.syncAndObserveDownloads(fileType)
    }

    fun onClick() {
        binding?.includedMenu?.ivCheckBox?.setSafeOnCheckedChangeListener { button, isChecked ->
            if (isChecked) {
                videoAdapter?.checkAll(true)
            } else {
                videoAdapter?.checkAll(false)
                toggleSelection(false)
            }
        }
    }

    fun toggleSelection(isVisible: Boolean) {
        binding?.includedMenu?.cvSelection?.isVisible = isVisible
        binding?.includedMenu?.cvDelete?.isVisible = false
        binding?.includedMenu?.cvDownload?.isVisible = true
        binding?.includedMenu?.ivCheckBox?.isChecked = false
    }

    fun Context.initVideoRv() {
        videoAdapter = VideoListAdapter(this, true, selCallBack = {
            toggleSelection(videoAdapter?.selectedList?.isNotEmpty() == true)
            if (videoAdapter?.isSelectedAll() == true) {
                binding?.includedMenu?.ivCheckBox?.isChecked = true
            }
        }) { item, position ->

        }
        binding?.rvMedia?.apply {
            layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
            adapter = videoAdapter
        }
        observeVideoUiState()
    }

    private fun observeVideoUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiDataState.collectLatest { uiState ->
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