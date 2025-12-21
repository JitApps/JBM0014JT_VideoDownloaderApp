package com.kraaft.video.manager.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["filePath"], unique = true)
    ]
)
data class FileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val itemId: Long,
    val name: String,
    val filePath: String,
    val folderPath : String,
    val playName: List<String>,
    val fileType: Int,
    val duration: Long,
    val size: Long,
    val dateModified: Long
)