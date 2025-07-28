package com.kraaft.video.manager.ui.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kraaft.video.manager.model.FileModel
import com.kraaft.video.manager.repo.StatusRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatusViewModel @Inject constructor(private val statusRepo: StatusRepo) : ViewModel() {

    val statusData: StateFlow<List<FileModel>>
        get() = statusRepo.statusData

    fun fetchStatus(folderPath: String) = viewModelScope.launch(Dispatchers.IO) {
        statusRepo.fetchStatus(folderPath)
    }

}