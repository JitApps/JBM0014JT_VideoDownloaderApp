package com.kraaft.video.manager.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kraaft.video.manager.data.db.DbHelper
import com.kraaft.video.manager.data.repo.SoundRepo
import com.kraaft.video.manager.data.repo.VideoRepo
import com.kraaft.video.manager.model.FileEntity
import com.kraaft.video.manager.model.FolderCount
import com.kraaft.video.manager.model.UiState
import com.kraaft.video.manager.utils.FILE_AUDIO
import com.kraaft.video.manager.utils.FILE_VIDEO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val soundRepo: SoundRepo,
    private val videoRepo: VideoRepo,
    private val dbHelper: DbHelper
) : ViewModel() {

    private val _playData = MutableStateFlow<UiState<List<FileEntity>>?>(null)
    val playData: StateFlow<UiState<List<FileEntity>>?>
        get() = _playData


    fun fetchPlayList(playName: String) = viewModelScope.launch {
        _playData.emit(UiState.Loading)
        try {
            dbHelper.getFileByPlay(playName)
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


    fun fetchFolderFiles(folderPath: String, fileType: Int) = viewModelScope.launch {
        _playData.emit(UiState.Loading)
        try {
            dbHelper.getFolderFilesByType(folderPath, fileType)
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

    fun addToPlaylist(fileEntity: FileEntity, playName: String) = viewModelScope.launch {
        dbHelper.addToPlayList(fileEntity, playName)
    }

    fun removeFromPlaylist(playFile: FileEntity) = viewModelScope.launch {
        dbHelper.removeFromPlaylist(playFile.copy(playName = listOf()))
    }


    val uiSoundState: StateFlow<UiState<List<FileEntity>>> =
        combine(
            soundRepo.soundBuffer,
            soundRepo.isSoundSyncing,
            soundRepo.soundSyncError
        ) { files, syncing, error ->

            when {
                error != null -> UiState.Error(error)
                (syncing == null || syncing) && files.isEmpty() -> UiState.Loading
                files.isNotEmpty() -> UiState.Success(files)
                else -> UiState.Empty
            }

        }.distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.Companion.Lazily, UiState.Loading)


    val uiSoundFolderState: StateFlow<UiState<List<FolderCount>>> =
        combine(
            soundRepo.soundFolderBuffer,
            soundRepo.isSoundSyncing,
            soundRepo.soundSyncError
        ) { files, syncing, error ->

            when {
                error != null -> UiState.Error(error)
                (syncing == null || syncing) && files.isEmpty() -> UiState.Loading
                files.isNotEmpty() -> UiState.Success(files)
                else -> UiState.Empty
            }

        }.distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.Companion.Lazily, UiState.Loading)


    fun syncAndObserveSounds() {
        soundRepo.observeSounds()
        viewModelScope.launch(Dispatchers.IO) {
            soundRepo.syncSounds(dbHelper.getAllFiles(FILE_AUDIO))
        }
    }

    val uiVideoState: StateFlow<UiState<List<FileEntity>>> =
        combine(
            videoRepo.videoBuffer,
            videoRepo.isVideoSyncing,
            videoRepo.videoSyncError
        ) { files, syncing, error ->

            when {
                error != null -> UiState.Error(error)
                (syncing == null || syncing) && files.isEmpty() -> UiState.Loading
                files.isNotEmpty() -> UiState.Success(files)
                else -> UiState.Empty
            }

        }.distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.Companion.Lazily, UiState.Loading)


    val uiVideoFolderState: StateFlow<UiState<List<FolderCount>>> =
        combine(
            videoRepo.videoFolderBuffer,
            videoRepo.isVideoSyncing,
            videoRepo.videoSyncError
        ) { files, syncing, error ->

            when {
                error != null -> UiState.Error(error)
                (syncing == null || syncing) && files.isEmpty() -> UiState.Loading
                files.isNotEmpty() -> UiState.Success(files)
                else -> UiState.Empty
            }

        }.distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.Companion.Lazily, UiState.Loading)

    fun syncAndObserveVideos() {
        videoRepo.observeVideos()
        viewModelScope.launch(Dispatchers.IO) {
            videoRepo.syncVideos(dbHelper.getAllFiles(FILE_VIDEO))
        }
    }

    fun fetchVideo() = viewModelScope.launch {

    }
}