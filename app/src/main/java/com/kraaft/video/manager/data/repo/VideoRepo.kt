package com.kraaft.video.manager.data.repo

import android.content.Context
import com.kraaft.video.manager.data.db.DbHelper
import com.kraaft.video.manager.data.sync.MediaSyncManager
import com.kraaft.video.manager.model.FileEntity
import com.kraaft.video.manager.model.FolderCount
import com.kraaft.video.manager.utils.FILE_VIDEO
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepo @Inject constructor(
    @ApplicationContext val context: Context,
    private val dbHelper: DbHelper,
    private val syncManager: MediaSyncManager
) {

    private val _videoBuffer = MutableStateFlow<List<FileEntity>>(listOf())
    val videoBuffer: StateFlow<List<FileEntity>> = _videoBuffer

    private val _videoFolderBuffer = MutableStateFlow<List<FolderCount>>(listOf())
    val videoFolderBuffer: StateFlow<List<FolderCount>> = _videoFolderBuffer

    private val _isVideoSyncing = MutableStateFlow<Boolean?>(null)
    val isVideoSyncing: StateFlow<Boolean?> = _isVideoSyncing

    private val _videoSyncError = MutableStateFlow<String?>(null)
    val videoSyncError: StateFlow<String?> = _videoSyncError

    suspend fun syncVideos(dbFiles: List<FileEntity>) {

        if (_isVideoSyncing.value == true) return

        _isVideoSyncing.value = true
        _videoSyncError.value = null

        try {
            val systemVideos = syncManager.fetchVideos()
            val result = dbHelper.diffFiles(dbFiles, systemVideos)

            if (result.toInsert.isNotEmpty()) dbHelper.insertAll(result.toInsert)
            if (result.toUpdate.isNotEmpty()) dbHelper.updateAll(result.toUpdate)
            if (result.toDelete.isNotEmpty()) dbHelper.deleteAll(result.toDelete)

            _videoBuffer.value = dbHelper.getAllFiles(FILE_VIDEO)
            _videoFolderBuffer.value = dbHelper.getAllFolders(FILE_VIDEO)

        } catch (e: Exception) {
            _videoSyncError.value = e.message ?: "Something went wrong"
        } finally {
            _isVideoSyncing.value = false
        }
    }

    fun observeVideos() {
        combine(
            dbHelper.getFilesByType(FILE_VIDEO),
            dbHelper.getFoldersByType(FILE_VIDEO)
        ) { videos, folders -> videos to folders }
            .distinctUntilChanged()
            .onEach { (videos, folders) ->
                _videoBuffer.value = videos
                _videoFolderBuffer.value = folders
            }
            .launchIn(CoroutineScope(Dispatchers.Main))
    }
}