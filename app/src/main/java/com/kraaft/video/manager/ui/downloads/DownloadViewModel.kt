package com.kraaft.video.manager.ui.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kraaft.video.manager.model.FileModel
import com.kraaft.video.manager.repo.DownloadRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DownloadViewModel  @Inject constructor(private val downloadRepo: DownloadRepo) : ViewModel() {

    val downloadData: StateFlow<List<FileModel>>
        get() = downloadRepo.downloadData

    fun fetchDownloads(folderPath: String) = viewModelScope.launch(Dispatchers.IO) {
        downloadRepo.fetchDownloads(folderPath)
    }

}