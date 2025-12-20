package com.kraaft.video.manager.data.repo

import android.content.Context
import com.kraaft.video.manager.model.FileModel
import com.kraaft.video.manager.data.sync.FileSyncManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class DownloadRepo @Inject constructor(
    @ApplicationContext val context: Context,
    private val syncManager: FileSyncManager
) {
    private val _downloadData = MutableStateFlow<List<FileModel>>(listOf())
    val downloadData: StateFlow<List<FileModel>>
        get() = _downloadData

    suspend fun fetchDownloads(folderPath: String) {
        _downloadData.emit(syncManager.fetchDownloads(folderPath))
    }
}