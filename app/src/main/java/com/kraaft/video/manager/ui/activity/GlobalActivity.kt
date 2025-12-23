package com.kraaft.video.manager.ui.activity

import android.R.attr.data
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kraaft.video.manager.R
import com.kraaft.video.manager.databinding.ActivityGlobalBinding
import com.kraaft.video.manager.model.DownloadModel
import com.kraaft.video.manager.model.NetworkResult
import com.kraaft.video.manager.model.UiState
import com.kraaft.video.manager.ui.adapter.DownloadAdapter
import com.kraaft.video.manager.ui.adapter.VideoListAdapter
import com.kraaft.video.manager.ui.base.BaseActivity
import com.kraaft.video.manager.ui.viewmodels.DownloadViewModel
import com.kraaft.video.manager.utils.FILE_OTHER_DOWNLOAD
import com.kraaft.video.manager.utils.beVisible
import com.kraaft.video.manager.utils.getDownloadsPath
import com.kraaft.video.manager.utils.isNotEmpty
import com.kraaft.video.manager.utils.onSingleClick
import com.kraaft.video.manager.utils.showDownloadDialog
import com.kraaft.video.manager.utils.showError
import com.kraaft.video.manager.utils.showLoading
import com.kraaft.video.manager.utils.showPage
import com.kraaft.video.manager.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import kotlin.getValue
import kotlin.jvm.java

class GlobalActivity : BaseActivity() {

    private var binding: ActivityGlobalBinding? = null
    private var downloadType = ""
    private var downloadAdapter: DownloadAdapter? = null
    val viewModel: DownloadViewModel by viewModels()

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGlobalBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        getIntentData()
        onClick()
        initDownloadRv()
        observeDownloadUiState()
        viewModel.syncDownloads()
    }

    fun getIntentData() {
        downloadType = intent.getStringExtra("download") ?: ""
        when (downloadType) {
            "josh" -> binding?.ivLogo?.setImageResource(R.drawable.icon_josh)
            "chingari" -> binding?.ivLogo?.setImageResource(R.drawable.icon_chingari)
            else -> binding?.ivLogo?.setImageResource(R.drawable.start_logo)
        }
    }

    private fun onClick() {
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
        binding?.apply {
            includedToolbar.btnBack.beVisible()
            includedToolbar.btnBack.onSingleClick {
                onBackPressedDispatcher.onBackPressed()
            }
            btnDownload.onSingleClick {
                if (binding?.etUrl?.isNotEmpty(this@GlobalActivity) == true) {
                    viewModel.downloadFile(binding?.etUrl?.text.toString())
                }
            }
        }
    }


    fun Context.initDownloadRv() {
        downloadAdapter = DownloadAdapter(this, viewModel)
        binding?.rvMedia?.apply {
            layoutManager = LinearLayoutManager(this@initDownloadRv)
            itemAnimator?.apply {
                if (this is SimpleItemAnimator) {
                    supportsChangeAnimations = false
                }
            }
            adapter = downloadAdapter
        }
    }


    private fun observeDownloadUiState() {
        lifecycleScope.launch {
            viewModel.downloadData.collectLatest { response ->
                when (response) {
                    is NetworkResult.Loading -> {
                        showLoadingDialog(getString(R.string.kk_fetching_please_wait))
                    }

                    is NetworkResult.Success -> {
                        hideLoadingDialog()
                        binding?.etUrl?.text?.clear()
                        showToast("Download Started")
                        (binding?.rvMedia?.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(
                            0,
                            0
                        )
                    }

                    is NetworkResult.Error -> {
                        hideLoadingDialog()
                        showToast(response.message)
                    }

                    else -> {

                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.uiDownloadState.collectLatest { uiState ->
                when (uiState) {

                    is UiState.Loading -> {
                        binding?.let {
                            it.includedError.showLoading(it.cvMain)
                        }
                    }

                    is UiState.Success -> {
                        downloadAdapter?.refreshData(uiState.data)
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
            }
        }

        lifecycleScope.launch {
            viewModel.progressData.collectLatest { progressData ->
                progressData?.let { data ->
                    downloadAdapter?.videoList?.indexOfFirst { it.fileName == data.first }?.let {
                        if (it != -1)
                        {
                            downloadAdapter?.videoList[it]?.apply {
                                progress = data.second
                            }
                            downloadAdapter?.notifyItemChanged(it)
                        }
                    }
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