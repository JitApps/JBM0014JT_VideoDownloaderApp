package com.kraaft.video.manager.model

import com.kraaft.video.manager.utils.DOWNLOAD_NOT_STARTED

data class DownloadModel(
    val mediaUrls: List<MediaUrl>,
    val message: String,
    val success: Boolean
)

data class MediaUrl(
    val fileName: String,
    var progress: DownloadProgress = DownloadProgress(),
    val type: String,
    val url: String
)

data class DownloadProgress(
    val current: Long = 0,
    val total: Long = 0,
    val status: Int = DOWNLOAD_NOT_STARTED
)