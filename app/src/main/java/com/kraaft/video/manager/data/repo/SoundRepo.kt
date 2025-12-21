package com.kraaft.video.manager.data.repo

import android.content.Context
import android.util.Log
import com.kraaft.video.manager.data.db.DbHelper
import com.kraaft.video.manager.data.sync.MediaSyncManager
import com.kraaft.video.manager.model.FileEntity
import com.kraaft.video.manager.model.FolderCount
import com.kraaft.video.manager.utils.FILE_AUDIO
import com.kraaft.video.manager.utils.FILE_VIDEO
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundRepo @Inject constructor(
    @ApplicationContext val context: Context,
    private val dbHelper: DbHelper,
    private val syncManager: MediaSyncManager
) {

    private val _soundBuffer = MutableStateFlow<List<FileEntity>>(listOf())
    val soundBuffer: StateFlow<List<FileEntity>> = _soundBuffer

    private val _soundFolderBuffer = MutableStateFlow<List<FolderCount>>(listOf())
    val soundFolderBuffer: StateFlow<List<FolderCount>> = _soundFolderBuffer

    private val _isSoundSyncing = MutableStateFlow<Boolean?>(null)
    val isSoundSyncing: StateFlow<Boolean?> = _isSoundSyncing

    private val _soundSyncError = MutableStateFlow<String?>(null)
    val soundSyncError: StateFlow<String?> = _soundSyncError

    suspend fun syncSounds(dbFiles: List<FileEntity>) {

        if (_isSoundSyncing.value == true) return

        _isSoundSyncing.value = true
        _soundSyncError.value = null

        try {
            val systemSounds = syncManager.fetchSounds()
            val result = dbHelper.diffFiles(dbFiles, systemSounds)

            if (result.toInsert.isNotEmpty()) dbHelper.insertAll(result.toInsert)
            if (result.toUpdate.isNotEmpty()) dbHelper.updateAll(result.toUpdate)
            if (result.toDelete.isNotEmpty()) dbHelper.deleteAll(result.toDelete)

            _soundBuffer.value = dbHelper.getAllFiles(FILE_AUDIO)
            _soundFolderBuffer.value = dbHelper.getAllFolders(FILE_AUDIO)

        } catch (e: Exception) {
            _soundSyncError.value = e.message ?: "Something went wrong"
        } finally {
            _isSoundSyncing.value = false
        }
    }

    fun observeSounds() {
        combine(
            dbHelper.getFilesByType(FILE_AUDIO),
            dbHelper.getFoldersByType(FILE_AUDIO)
        ) { videos, folders -> videos to folders }
            .distinctUntilChanged()
            .onEach { (videos, folders) ->
                _soundBuffer.value = videos
                _soundFolderBuffer.value = folders
            }
            .launchIn(CoroutineScope(Dispatchers.Main))
    }
}