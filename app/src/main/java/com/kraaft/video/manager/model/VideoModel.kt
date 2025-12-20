package com.kraaft.video.manager.model

import android.R.attr.data
import java.io.Serializable

data class VideoModel(
    var folderName: String = "",
    var videoFiles: MutableList<VideoFile> = mutableListOf()
)

data class VideoFile(
    val id: Long,
    val name: String,
    val uri: android.net.Uri,
    val duration: Long,
    val size: Long,
    val dateModified: Long
)