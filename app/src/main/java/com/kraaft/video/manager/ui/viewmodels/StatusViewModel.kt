package com.kraaft.video.manager.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kraaft.video.manager.data.sync.FileSyncManager
import com.kraaft.video.manager.model.FileModel
import com.kraaft.video.manager.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatusViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val syncManager: FileSyncManager
) : ViewModel() {

    private val _statusData = MutableStateFlow<UiState<List<FileModel>>?>(null)
    val statusData: StateFlow<UiState<List<FileModel>>?>
        get() = _statusData

    fun fetchStatus(folderPath: String) = viewModelScope.launch {
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
}