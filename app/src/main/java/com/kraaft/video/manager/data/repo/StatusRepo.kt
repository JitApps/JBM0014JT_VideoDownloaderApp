package com.kraaft.video.manager.data.repo

import android.content.Context
import com.kraaft.video.manager.model.FileModel
import com.kraaft.video.manager.data.sync.FileSyncManager
import com.kraaft.video.manager.model.UiState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class StatusRepo @Inject constructor(
    @ApplicationContext val context: Context,
    private val syncManager: FileSyncManager
) {
    private val _statusData = MutableStateFlow<UiState<List<FileModel>>?>(null)
    val statusData: StateFlow<UiState<List<FileModel>>?>
        get() = _statusData

    suspend fun fetchStatus(folderPath: String) {
        _statusData.emit(UiState.Loading)

        try {
            val data = syncManager.fetchStatus(folderPath)

            _statusData.emit(if (data.isEmpty()) {
                UiState.Empty
            } else {
                UiState.Success(data)
            })

        } catch (e: Exception) {
            _statusData.emit(UiState.Error(e.message ?: "Something went wrong"))
        }
    }

    suspend fun fetchDownloads(folderPath: String) {

        _statusData.emit(UiState.Loading)

        try {
            val data = syncManager.fetchDownloads(folderPath)

            _statusData.emit(if (data.isEmpty()) {
                UiState.Empty
            } else {
                UiState.Success(data)
            })

        } catch (e: Exception) {
            _statusData.emit(UiState.Error(e.message ?: "Something went wrong"))
        }
    }

}