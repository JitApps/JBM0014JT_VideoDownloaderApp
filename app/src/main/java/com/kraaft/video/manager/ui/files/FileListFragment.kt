package com.kraaft.video.manager.ui.files

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.storage.StorageManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.florent37.inlineactivityresult.InlineActivityResult
import com.kraaft.video.manager.R
import com.kraaft.video.manager.databinding.FragmentFileListBinding
import com.kraaft.video.manager.model.FileModel
import com.kraaft.video.manager.ui.base.BaseFragment
import com.kraaft.video.manager.ui.common.StatusViewModel
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
class FileListFragment : BaseFragment() {

    @Inject
    lateinit var preferenceClass: PreferenceClass

    val viewModel: StatusViewModel by viewModels()

    private var binding: FragmentFileListBinding? = null
    private var folderPath = ""
    private var isStatus = false

    private var fileListAdapter: FileListAdapter? = null

    companion object {
        fun getInstance(folderPath: String, isStatus: Boolean): FileListFragment {
            return FileListFragment().apply {
                arguments = Bundle().apply {
                    putString("folderPath", folderPath)
                    putBoolean("pageStatus", isStatus)
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.setMainContext()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        folderPath = arguments?.getString("folderPath", "") ?: ""
        isStatus = arguments?.getBoolean("pageStatus", false) ?: false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFileListBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        observeUiState()
        checkForPermissions()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.statusData.collect { newList ->
                newList?.let {
                    refreshData(it)
                }
            }
        }
    }

    private fun setAdapter() {
        appContext?.let {
            fileListAdapter = FileListAdapter(it) { item, pos ->

            }
            binding?.apply {
                rvFiles.layoutManager = StaggeredGridLayoutManager(3, VERTICAL)
                rvFiles.adapter = fileListAdapter
            }
        }

    }

    private fun checkForPermissions() {
        if (isStatus) {
            if (VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                if (folderPath.endsWith(getStatusFolder()) && (preferenceClass.getString("w_path")
                        .endsWith(getStatusFolder()) || appContext?.isPackageInstalled("com.whatsapp") == false)
                ) {
                    if (appContext?.isPackageInstalled("com.whatsapp") == false)
                        refreshData(listOf())
                    else
                        startFetchingData()
                } else if (folderPath.endsWith(getBusinessFolder()) && (preferenceClass.getString(
                        "wb_path"
                    )
                        .endsWith(getBusinessFolder()) || appContext?.isPackageInstalled("com.whatsapp.w4b") == false)
                ) {
                    if (appContext?.isPackageInstalled("com.whatsapp.w4b") == false)
                        refreshData(listOf())
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
        } else {
            startFetchingData()
        }
    }

    private fun startFetchingData() {
        binding?.let {
            it.includedError.showLoading(it.cvMain)
        }
        if (isStatus) {
            viewModel.fetchStatus(
                if (folderPath.endsWith(getStatusFolder())) preferenceClass.getString(
                    "w_path", getStatusFolder()
                ) else preferenceClass.getString(
                    "wb_path", getBusinessFolder()
                )
            )
        } else {
            viewModel.fetchDownloads(folderPath)
        }
    }

    private fun refreshData(newList: List<FileModel>) {
        fileListAdapter?.refreshData(newList.toMutableList())
        binding?.apply {
            if ((fileListAdapter?.itemCount ?: 0) > 0) {
                includedError.showPage(cvMain)
            } else {
                includedError.showError(getString(R.string.kk_error_no_data), cvMain)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getFolderPermission() {
        val manager = appContext?.getSystemService(Context.STORAGE_SERVICE) as StorageManager
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
                        appContext?.apply {
                            contentResolver.takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            )
                        }
                    } else if (uri.toString().endsWith(getBusinessFolder())) {
                        preferenceClass.setString("wb_path", uri.toString())
                        appContext?.apply {
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