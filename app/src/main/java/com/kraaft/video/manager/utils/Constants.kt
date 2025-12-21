package com.kraaft.video.manager.utils

import android.Manifest
import android.content.Context
import android.os.Build

const val PERMISSION_VIDEO = 1
const val PERMISSION_SOUND = 2

const val FILE_AUDIO = 1
const val FILE_VIDEO = 2
const val FILE_DOWNLOAD = 3
const val FILE_PLAYLIST = 4

fun getPermissionString(id: Int) = when (id) {
    PERMISSION_VIDEO -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_VIDEO else Manifest.permission.READ_EXTERNAL_STORAGE
    PERMISSION_SOUND -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE
    else -> ""
}