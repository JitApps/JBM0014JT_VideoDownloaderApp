package com.kraaft.video.manager.ui.viewmodels

import android.R.attr.data
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.kraaft.video.manager.R
import com.kraaft.video.manager.api.RetroAPI
import com.kraaft.video.manager.data.db.DbHelper
import com.kraaft.video.manager.data.repo.DownloadRepo
import com.kraaft.video.manager.data.sync.FileSyncManager
import com.kraaft.video.manager.model.DownloadModel
import com.kraaft.video.manager.model.DownloadProgress
import com.kraaft.video.manager.model.FileEntity
import com.kraaft.video.manager.model.MediaUrl
import com.kraaft.video.manager.model.NetworkResult
import com.kraaft.video.manager.model.UiState
import com.kraaft.video.manager.utils.DOWNLOAD_COMPLETE
import com.kraaft.video.manager.utils.DOWNLOAD_FAILED
import com.kraaft.video.manager.utils.DOWNLOAD_NOT_STARTED
import com.kraaft.video.manager.utils.FILE_OTHER_DOWNLOAD
import com.kraaft.video.manager.utils.handleResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val dbHelper: DbHelper,
    private val downloadRepo: DownloadRepo,
    private val syncManager: FileSyncManager,
    private val retroAPI: RetroAPI
) : ViewModel() {

    private val _downloadData = MutableStateFlow<NetworkResult<ResponseBody>?>(null)
    val downloadData: StateFlow<NetworkResult<ResponseBody>?>
        get() = _downloadData

    val progressData: StateFlow<Pair<String, DownloadProgress>?>
        get() = downloadRepo.downloadProgressMap

    private val _dataBuffer = MutableStateFlow<List<FileEntity>>(listOf())
    private val _isDataSyncing = MutableStateFlow<Boolean?>(null)
    private val _dataError = MutableStateFlow<String?>(null)

    val uiDownloadState = MutableStateFlow<UiState<List<MediaUrl>>>(UiState.Loading)

    val uiDataState: StateFlow<UiState<List<FileEntity>>> =
        combine(
            _dataBuffer,
            _isDataSyncing,
            _dataError
        ) { files, syncing, error ->

            when {
                error != null -> UiState.Error(error)
                (syncing == null || syncing) && files.isEmpty() -> UiState.Loading
                files.isNotEmpty() -> UiState.Success(files)
                else -> UiState.Empty
            }

        }.distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.Companion.Lazily, UiState.Loading)


    fun syncAndObserveSounds(fileType: Int) {
        observeFiles(fileType)
        viewModelScope.launch(Dispatchers.IO) {
            syncFiles(fileType)
        }
    }

    private suspend fun syncFiles(fileType: Int) {

        if (_isDataSyncing.value == true) return

        _isDataSyncing.value = true
        _dataError.value = null

        try {
            val systemSounds = syncManager.fetchDownloads(fileType)
            val dbFiles = dbHelper.getAllFiles(fileType)
            val result = dbHelper.diffFiles(dbFiles, systemSounds)

            if (result.toDelete.isNotEmpty()) dbHelper.deleteAll(result.toDelete)

            _dataBuffer.value = dbHelper.getAllFiles(fileType)

        } catch (e: Exception) {
            _dataError.value = e.message ?: "Something went wrong"
        } finally {
            _isDataSyncing.value = false
        }
    }

    fun observeFiles(fileType: Int) {
        dbHelper.getFilesByType(fileType).distinctUntilChanged()
            .onEach { files ->
                _dataBuffer.value = files
            }
            .launchIn(CoroutineScope(Dispatchers.Main))
    }


    fun syncDownloads() = viewModelScope.launch {
        downloadRepo.downloadListData.collectLatest { files ->
            uiDownloadState.value = when {
                files.isNotEmpty() -> UiState.Success(files)
                else -> UiState.Empty
            }
        }
    }

    fun downloadFile(fileUrl: String) = viewModelScope.launch {
        try {
            _downloadData.value =
                NetworkResult.Loading()
            val jsonObject = JSONObject()
            jsonObject.put("url", fileUrl)
            val response = retroAPI.downloadFile(jsonObject.toString())
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    Gson().fromJson(
                        body.string(),
                        DownloadModel::class.java
                    )?.let { downloadModel ->
                        if (downloadModel.success && downloadModel.mediaUrls.isNotEmpty()) {
                            val updatedList = downloadModel.mediaUrls.map { item ->
                                item.copy(progress = DownloadProgress())
                            }
                            downloadRepo.addMediaFiles(updatedList)
                            _downloadData.value = NetworkResult.Success(body)
                        } else {
                            _downloadData.value =
                                NetworkResult.Error("Download Failed")
                        }
                    }
                } ?: run {
                    _downloadData.value =
                        NetworkResult.Error("Download Failed")
                }
            } else {
                _downloadData.value =
                    NetworkResult.Error(context.resources.getString(R.string.kk_error_unknown))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _downloadData.value =
                NetworkResult.Error(context.resources.getString(R.string.kk_error_unknown))
        }
    }
}