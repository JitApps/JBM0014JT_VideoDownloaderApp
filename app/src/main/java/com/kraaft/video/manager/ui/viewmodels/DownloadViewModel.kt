package com.kraaft.video.manager.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kraaft.video.manager.data.db.DbHelper
import com.kraaft.video.manager.data.sync.FileSyncManager
import com.kraaft.video.manager.model.FileEntity
import com.kraaft.video.manager.model.UiState
import com.kraaft.video.manager.utils.FILE_OTHER_DOWNLOAD
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val dbHelper: DbHelper,
    private val syncManager: FileSyncManager
) : ViewModel() {

    private val _dataBuffer = MutableStateFlow<List<FileEntity>>(listOf())
    private val _isDataSyncing = MutableStateFlow<Boolean?>(null)
    private val _dataError = MutableStateFlow<String?>(null)

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
        observeFiles()
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

    fun observeFiles() {
        dbHelper.getFilesByType(FILE_OTHER_DOWNLOAD).distinctUntilChanged()
            .onEach { files ->
                _dataBuffer.value = files
            }
            .launchIn(CoroutineScope(Dispatchers.Main))
    }
}