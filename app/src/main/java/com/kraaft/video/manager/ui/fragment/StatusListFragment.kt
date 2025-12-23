package com.kraaft.video.manager.ui.fragment

import android.R.attr.onClick
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.florent37.inlineactivityresult.InlineActivityResult
import com.kraaft.video.manager.R
import com.kraaft.video.manager.databinding.FragmentStatusListBinding
import com.kraaft.video.manager.model.FileModel
import com.kraaft.video.manager.model.UiState
import com.kraaft.video.manager.ui.adapter.StatusListAdapter
import com.kraaft.video.manager.ui.base.BaseFragment
import com.kraaft.video.manager.ui.viewmodels.StatusViewModel
import com.kraaft.video.manager.utils.PreferenceClass
import com.kraaft.video.manager.utils.getBusinessFolder
import com.kraaft.video.manager.utils.getStatusFolder
import com.kraaft.video.manager.utils.isPackageInstalled
import com.kraaft.video.manager.utils.showError
import com.kraaft.video.manager.utils.showLoading
import com.kraaft.video.manager.utils.showPage
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StatusListFragment : BaseFragment() {

    @Inject
    lateinit var preferenceClass: PreferenceClass

    val viewModel: StatusViewModel by viewModels()

    private var binding: FragmentStatusListBinding? = null
    private var folderPath = ""

    private var statusListAdapter: StatusListAdapter? = null

    companion object {
        fun getInstance(folderPath: String, isStatus: Boolean): StatusListFragment {
            return StatusListFragment().apply {
                arguments = Bundle().apply {
                    putString("folderPath", folderPath)
                    putBoolean("pageStatus", isStatus)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        folderPath = arguments?.getString("folderPath", "") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatusListBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.setAdapter()
        onClick()
        observeUiState()
        checkForPermissions()
    }

    fun onClick() {
        binding?.includedMenu?.ivCheckBox?.setSafeOnCheckedChangeListener { button, isChecked ->
            if (isChecked && statusListAdapter?.isSelectedAll() == true) {
                statusListAdapter?.checkAll(false)
            } else {
                statusListAdapter?.checkAll(true)
            }
        }
    }

    fun toggleSelection(isVisible: Boolean) {
        binding?.includedMenu?.cvSelection?.isVisible = isVisible
        binding?.includedMenu?.cvDelete?.isVisible = false
        binding?.includedMenu?.cvDownload?.isVisible = true
        binding?.includedMenu?.ivCheckBox?.isChecked = false
    }

    private fun Context.setAdapter() {
        statusListAdapter = StatusListAdapter(this, selCallBack = {
            toggleSelection(statusListAdapter?.selectedList?.isNotEmpty() == true)
            if (statusListAdapter?.isSelectedAll() == true) {
                binding?.includedMenu?.ivCheckBox?.isChecked = true
            } else if (statusListAdapter?.isDeSelectedAll() == true) {
                toggleSelection(false)
            }
        }) { item, pos ->

        }
        binding?.apply {
            rvFiles.layoutManager = StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)
            rvFiles.adapter = statusListAdapter
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.statusData.collect { uiState ->
                uiState?.let {
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
    }

    private fun checkForPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            if (folderPath.endsWith(getStatusFolder()) && (preferenceClass.getString("w_path")
                    .endsWith(getStatusFolder()) || context?.isPackageInstalled("com.whatsapp") == false)
            ) {
                if (context?.isPackageInstalled("com.whatsapp") == false)
                    showErrorOrEmpty()
                else
                    startFetchingData()
            } else if (folderPath.endsWith(getBusinessFolder()) && (preferenceClass.getString(
                    "wb_path"
                )
                    .endsWith(getBusinessFolder()) || context?.isPackageInstalled("com.whatsapp.w4b") == false)
            ) {
                if (context?.isPackageInstalled("com.whatsapp.w4b") == false)
                    showErrorOrEmpty()
                else
                    startFetchingData()
            } else {
                binding?.apply {
                    includedError.showError(
                        "Please give permission to access media folder",
                        cvMain,
                        "Allow"
                    ) {
                        includedError.showLoading(cvMain)
                        getFolderPermission()
                    }
                }
            }
        } else {
            startFetchingData()
        }
    }

    private fun startFetchingData() {
        viewModel.fetchStatus(
            if (folderPath.endsWith(getStatusFolder())) preferenceClass.getString(
                "w_path", getStatusFolder()
            ) else preferenceClass.getString(
                "wb_path", getBusinessFolder()
            )
        )
    }

    fun showErrorOrEmpty(message: String = getString(R.string.kk_error_no_data)) {
        binding?.let {
            it.includedError.showError(message, it.cvMain)
        }
    }

    private fun refreshData(newList: List<FileModel>) {
        statusListAdapter?.refreshData(newList.toMutableList())
        binding?.let {
            it.includedError.showPage(it.cvMain)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getFolderPermission() {
        val manager = context?.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val intent = manager.primaryStorageVolume.createOpenDocumentTreeIntent()
        var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI")
        var scheme = uri.toString().replace("/root/", "/document/")
        scheme = "$scheme%3A$folderPath"
        uri = scheme.toUri()
        intent.putExtra("android.provider.extra.INITIAL_URI", uri)
        intent.putExtra("android.content.extra.SHOW_ADVANCED", true)
        InlineActivityResult(this)
            .startForResult(intent)
            .onSuccess { result ->
                result.data?.data?.also { uri ->
                    if (uri.toString().endsWith(getStatusFolder())) {
                        preferenceClass.setString("w_path", uri.toString())
                        context?.apply {
                            contentResolver.takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            )
                        }
                    } else if (uri.toString().endsWith(getBusinessFolder())) {
                        preferenceClass.setString("wb_path", uri.toString())
                        context?.apply {
                            contentResolver.takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            )
                        }
                    }
                    checkForPermissions()
                } ?: checkForPermissions()
            }.onFail { checkForPermissions() }
    }
}