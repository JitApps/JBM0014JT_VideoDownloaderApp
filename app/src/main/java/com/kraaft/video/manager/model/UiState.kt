package com.kraaft.video.manager.model

sealed class UiState<out T> {

    data object Loading : UiState<Nothing>()

    data class Success<T>(
        val data: T
    ) : UiState<T>()

    data class Error(
        val message: String
    ) : UiState<Nothing>()

    data object Empty : UiState<Nothing>()
}

sealed class EventUiState {
    object Loading : EventUiState()
    object Empty : EventUiState()
    data class Success(val events: List<FileEntity>) : EventUiState()
    data class Error(val message: String) : EventUiState()
}