package com.kraaft.video.manager.data.repo

import android.content.Context
import com.kraaft.video.manager.data.db.DbHelper
import com.kraaft.video.manager.model.DownloadProgress
import com.kraaft.video.manager.model.FileEntity
import com.kraaft.video.manager.model.MediaUrl
import com.kraaft.video.manager.utils.DOWNLOAD_COMPLETE
import com.kraaft.video.manager.utils.DOWNLOAD_FAILED
import com.kraaft.video.manager.utils.DOWNLOAD_RUNNING
import com.kraaft.video.manager.utils.DOWNLOAD_STARTED
import com.kraaft.video.manager.utils.DownloadManager
import com.kraaft.video.manager.utils.FILE_OTHER_DOWNLOAD
import com.kraaft.video.manager.utils.getDownloadsPath
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepo @Inject constructor(
    @ApplicationContext val context: Context,
    private val dbHelper: DbHelper,
) {

    private val repoScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _downloadProgressMap = MutableStateFlow<Pair<String, DownloadProgress>?>(null)
    val downloadProgressMap: StateFlow<Pair<String, DownloadProgress>?> = _downloadProgressMap


    private val _downloadListData = MutableStateFlow<List<MediaUrl>>(emptyList())
    val downloadListData: StateFlow<List<MediaUrl>>
        get() = _downloadListData


    fun updateFile(fileName: String, filePath: String, downloadProgress: DownloadProgress) {

        /*  _downloadListData.value = _downloadListData.value.map { item ->
              if (item.fileName == fileName) item.copy(progress = downloadProgress) else item
          }*/

        _downloadProgressMap.value = Pair(fileName, downloadProgress)
        if (downloadProgress.status == DOWNLOAD_COMPLETE) {
            downloadComplete(FILE_OTHER_DOWNLOAD, filePath)
        }
    }

    fun downloadComplete(fileType: Int, filePath: String) = repoScope.launch {
        val file = File(filePath)
        val folderName = file.parentFile?.absolutePath ?: "Unknown"
        dbHelper.insertAll(
            listOf(
                FileEntity(
                    itemId = -1,
                    name = file.name,
                    filePath = file.absolutePath,
                    folderPath = folderName,
                    fileType = fileType,
                    size = 0,
                    duration = 0,
                    playName = emptyList(),
                    dateModified = file.lastModified()
                )
            )
        )
    }

    fun addMediaFiles(mediaList: List<MediaUrl>) {
        val oldList = _downloadListData.value
        _downloadListData.value = mediaList + oldList

        mediaList.forEach {
            startDownload(it)
        }
    }

    fun startDownload(mediaFile: MediaUrl) {
        val filePath = getDownloadsPath(FILE_OTHER_DOWNLOAD) + "/" + mediaFile.fileName

        DownloadManager.enqueueDownload(
            DownloadManager.DownloadRequest(
                context = context,
                fileUrl = mediaFile.url,
                folderPath = getDownloadsPath(FILE_OTHER_DOWNLOAD),
                fileName = mediaFile.fileName,
                startCallBack = { total ->
                    repoScope.launch {
                        updateFile(
                            mediaFile.fileName, filePath, DownloadProgress(
                                current = 0,
                                total = total,
                                status = DOWNLOAD_STARTED
                            )
                        )
                    }
                },
                progressCallBack = { current, total ->
                    repoScope.launch {
                        updateFile(
                            mediaFile.fileName, filePath, DownloadProgress(
                                current = current,
                                total = total,
                                status = DOWNLOAD_RUNNING
                            )
                        )
                    }
                },
                callback = { success, path ->
                    repoScope.launch {
                        updateFile(
                            mediaFile.fileName, filePath, DownloadProgress(
                                current = 0,
                                total = 0,
                                status = if (success) DOWNLOAD_COMPLETE else DOWNLOAD_FAILED
                            )
                        )
                    }
                }
            )
        )

        /* context.downloadFile(
             mediaFile.url,
             getDownloadsPath(FILE_OTHER_DOWNLOAD),
             mediaFile.fileName,
             progressCallBack = { current, total ->
                 repoScope.launch {
                     updateFile(
                         mediaFile.fileName, filePath, DownloadProgress(
                             current = current,
                             total = total,
                             status = DOWNLOAD_RUNNING
                         )
                     )
                 }
             },
             callback = { success, message ->
                 repoScope.launch {
                     updateFile(
                         mediaFile.fileName, filePath, DownloadProgress(
                             current = 0,
                             total = 0,
                             status = if (success) DOWNLOAD_COMPLETE else DOWNLOAD_FAILED
                         )
                     )
                 }
             }
         )*/
    }
}