package com.kraaft.video.manager.ui.fragment

import android.content.Context
import android.content.Intent
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
import com.kraaft.video.manager.ui.activity.FolderListActivity
import com.kraaft.video.manager.ui.adapter.FolderAdapter
import com.kraaft.video.manager.ui.base.BaseFragment
import com.kraaft.video.manager.ui.viewmodels.MediaViewModel
import com.kraaft.video.manager.utils.FILE_AUDIO
import com.kraaft.video.manager.utils.FILE_VIDEO
import com.kraaft.video.manager.utils.gotoIntent
import com.kraaft.video.manager.utils.showError
import com.kraaft.video.manager.utils.showLoading
import com.kraaft.video.manager.utils.showPage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MediaFolderFragment : BaseFragment() {

    private var binding: FragmentMediaFolderBinding? = null
    private var fileType = FILE_VIDEO

    private var folderAdapter: FolderAdapter? = null

    val viewModel: MediaViewModel by viewModels()

    companion object {
        fun getInstance(fileType: Int = FILE_VIDEO): MediaFolderFragment {
            return MediaFolderFragment().apply {
                arguments = Bundle().apply {
                    putInt("fileType", fileType)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileType = arguments?.getInt("fileType", FILE_VIDEO) ?: FILE_VIDEO
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
        context?.initRv()
    }

    fun Context.initRv() {
        folderAdapter = FolderAdapter(this) { item, position ->
            activity?.let {
                it.gotoIntent(Intent(it, FolderListActivity::class.java).apply {
                    putExtra("folderPath", item)
                    putExtra("fileType", fileType)
                }, false)
            }
        }
        binding?.rvMedia?.apply {
            layoutManager = LinearLayoutManager(this@initRv)
            adapter = folderAdapter
        }
        if (fileType == FILE_AUDIO) observeAudioUiState() else observeVideoUiState()
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
                        refreshData(uiState.data)
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
                        refreshData(uiState.data)
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

    private fun refreshData(newList: List<FolderCount>) {
        folderAdapter?.refreshData(newList.toMutableList())
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