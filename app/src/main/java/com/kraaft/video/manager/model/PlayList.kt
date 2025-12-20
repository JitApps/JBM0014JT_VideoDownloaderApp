package com.kraaft.video.manager.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlayList(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    val playName: String,
    val itemId: Long,
    val name: String,
    val uri: android.net.Uri,
    val duration: Long,
    val size: Long,
    val dateModified: Long
)