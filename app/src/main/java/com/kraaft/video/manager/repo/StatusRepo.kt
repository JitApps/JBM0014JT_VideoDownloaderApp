package com.kraaft.video.manager.repo

import android.content.Context
import com.kraaft.video.manager.model.FileModel
import com.kraaft.video.manager.sync.FileSyncManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class StatusRepo @Inject constructor(
    @ApplicationContext val context: Context,
    private val syncManager: FileSyncManager
) {
    private val _statusData = MutableStateFlow<List<FileModel>>(listOf())
    val statusData: StateFlow<List<FileModel>>
        get() = _statusData

    suspend fun fetchStatus(folderPath: String) {
        _statusData.emit(syncManager.fetchStatus(folderPath))
    }

}