package com.kraaft.video.manager.model

import android.R.attr.data
import java.io.Serializable

data class SoundModel(
    var folderName: String = "",
    var soundFiles: MutableList<SoundFile> = mutableListOf()
)

data class SoundFile(
    val id: Long,
    val name: String,
    val uri: android.net.Uri,
    val duration: Long,
    val size: Long,
    val dateModified: Long
)