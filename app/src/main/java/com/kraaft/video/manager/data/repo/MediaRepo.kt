package com.kraaft.video.manager.data.repo

import android.R.attr.data
import android.content.Context
import com.kraaft.video.manager.data.db.PlayListDAO
import com.kraaft.video.manager.data.sync.MediaSyncManager
import com.kraaft.video.manager.model.PlayList
import com.kraaft.video.manager.model.SoundFile
import com.kraaft.video.manager.model.SoundModel
import com.kraaft.video.manager.model.UiState
import com.kraaft.video.manager.model.VideoFile
import com.kraaft.video.manager.model.VideoModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepo @Inject constructor(
    @ApplicationContext val context: Context,
    private val playDao: PlayListDAO,
    private val syncManager: MediaSyncManager
) {

    private val _soundFolderData = MutableStateFlow<UiState<List<SoundModel>>?>(null)
    val soundFolderData: StateFlow<UiState<List<SoundModel>>?>
        get() = _soundFolderData

    private val _soundData = MutableStateFlow<UiState<List<SoundFile>>?>(null)
    val soundData: StateFlow<UiState<List<SoundFile>>?>
        get() = _soundData

    private val _videoFolderData = MutableStateFlow<UiState<List<VideoModel>>?>(null)
    val videoFolderData: StateFlow<UiState<List<VideoModel>>?>
        get() = _videoFolderData

    private val _videoData = MutableStateFlow<UiState<List<VideoFile>>?>(null)
    val videoData: StateFlow<UiState<List<VideoFile>>?>
        get() = _videoData

    private val _playData = MutableStateFlow<UiState<List<PlayList>>?>(null)
    val playData: StateFlow<UiState<List<PlayList>>?>
        get() = _playData

    suspend fun addToPlayList(soundFile: SoundFile, playName: String) {
        playDao.addMedia(
            listOf(
                PlayList(
                    playName = playName,
                    itemId = soundFile.id,
                    name = soundFile.name,
                    uri = soundFile.uri,
                    duration = soundFile.duration,
                    size = soundFile.size,
                    dateModified = soundFile.dateModified
                )
            )
        )
    }

    suspend fun removeFromPlaylist(playFile: PlayList) {
        playDao.deleteMedia(listOf(playFile))
    }

    suspend fun fetchSounds() {
        _soundData.emit(UiState.Loading)
        _soundFolderData.emit(UiState.Loading)

        try {
            val data = syncManager.fetchSounds()
            val allFiles = data.flatMap { it.soundFiles }

            _soundData.emit(
                if (allFiles.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(allFiles)
                }
            )

            _soundFolderData.emit(
                if (data.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(data)
                }
            )

        } catch (e: Exception) {
            _soundFolderData.emit(UiState.Error(e.message ?: "Something went wrong"))
            _soundData.emit(UiState.Error(e.message ?: "Something went wrong"))
        }
    }

    suspend fun fetchVideos() {
        _videoData.emit(UiState.Loading)
        _videoFolderData.emit(UiState.Loading)

        try {
            val data = syncManager.fetchVideos()
            val allFiles = data.flatMap { it.videoFiles }

            _videoData.emit(
                if (allFiles.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(allFiles)
                }
            )

            _videoFolderData.emit(
                if (data.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(data)
                }
            )

        } catch (e: Exception) {
            _videoFolderData.emit(UiState.Error(e.message ?: "Something went wrong"))
            _videoData.emit(UiState.Error(e.message ?: "Something went wrong"))
        }
    }

    suspend fun fetchPlayList(playName: String) {
        _playData.emit(UiState.Loading)

        try {
            playDao.getMedia(playName)
                .distinctUntilChanged()
                .collectLatest { data ->
                    _playData.emit(
                        if (data.isEmpty()) {
                            UiState.Empty
                        } else {
                            UiState.Success(data)
                        }
                    )
                }
        } catch (e: Exception) {
            _playData.emit(UiState.Error(e.message ?: "Something went wrong"))
        }
    }

}