package com.kraaft.video.manager.data.sync

import android.R.attr.path
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import com.kraaft.video.manager.model.FileModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import java.io.File
import java.util.Arrays
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile


class FileSyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun fetchStatus(folderPath: String): List<FileModel> =
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            getQStatusData(folderPath);
        } else {
            getStatusData(folderPath);
        }

    fun fetchDownloads(folderPath: String): List<FileModel> = getStatusData(folderPath)

    private fun getStatusData(folderPath: String): List<FileModel> {
        val listFiles = mutableListOf<FileModel>()
        val folder = File(folderPath)
        if (folder.exists() && folder.isDirectory) {
            folder.listFiles()?.onEach { file ->
                if (file.isFile && !file.absolutePath.toString().contains(".nomedia")) {
                    listFiles.add(
                        FileModel(
                            file.name,
                            file.absolutePath
                        )
                    )
                }
            }
        }
        return listFiles
    }

    private fun getQStatusData(folderPath: String): List<FileModel> {
        val listFiles = mutableListOf<FileModel>()
        val folder = DocumentFile.fromTreeUri(context, folderPath.toUri());
        folder?.listFiles()?.onEach { file ->
            if (file.isFile && !file.uri.toString().contains(".nomedia")) {
                file.name?.apply {
                    listFiles.add(FileModel(this, file.uri.toString()))
                }
            }
        }
        return listFiles
    }


}