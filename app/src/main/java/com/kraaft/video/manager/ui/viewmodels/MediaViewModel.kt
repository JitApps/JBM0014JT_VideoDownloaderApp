package com.kraaft.video.manager.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kraaft.video.manager.data.repo.MediaRepo
import com.kraaft.video.manager.model.PlayList
import com.kraaft.video.manager.model.SoundFile
import com.kraaft.video.manager.model.SoundModel
import com.kraaft.video.manager.model.UiState
import com.kraaft.video.manager.model.VideoFile
import com.kraaft.video.manager.model.VideoModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(private val mediaRepo: MediaRepo) : ViewModel() {

    val soundFolderData: StateFlow<UiState<List<SoundModel>>?>
        get() = mediaRepo.soundFolderData

    val soundData: StateFlow<UiState<List<SoundFile>>?>
        get() = mediaRepo.soundData

    val videoFolderData: StateFlow<UiState<List<VideoModel>>?>
        get() = mediaRepo.videoFolderData

    val videoData: StateFlow<UiState<List<VideoFile>>?>
        get() = mediaRepo.videoData

    val playData: StateFlow<UiState<List<PlayList>>?>
        get() = mediaRepo.playData

    fun fetchPlayList(playName: String) = viewModelScope.launch(Dispatchers.IO) {
        mediaRepo.fetchPlayList(playName)
    }

    fun addToPlaylist(soundFile: SoundFile, playName: String) =
        viewModelScope.launch(Dispatchers.IO) {
            mediaRepo.addToPlayList(soundFile, playName)
        }

    fun removeFromPlaylist(playFile: PlayList) =
        viewModelScope.launch(Dispatchers.IO) {
            mediaRepo.removeFromPlaylist(playFile)
        }

    fun fetchSound() = viewModelScope.launch(Dispatchers.IO) {
        mediaRepo.fetchSounds()
    }

    fun fetchVideo() = viewModelScope.launch(Dispatchers.IO) {
        mediaRepo.fetchVideos()
    }
}