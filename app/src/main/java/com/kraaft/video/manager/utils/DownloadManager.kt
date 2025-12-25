package com.kraaft.video.manager.utils

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object DownloadManager {

    private val maxConcurrentDownloads = 3 // set your limit
    private val downloadQueue = ArrayDeque<DownloadRequest>()
    private var activeDownloads = 0

    private val downloadDispatcher = Executors.newFixedThreadPool(maxConcurrentDownloads)
        .asCoroutineDispatcher()

    data class DownloadRequest(
        val context: Context,
        val fileUrl: String,
        val folderPath: String,
        val fileName: String,
        val startCallBack: (Long) -> Unit,
        val progressCallBack: (Long, Long) -> Unit,
        val callback: (Boolean, String) -> Unit
    )

    fun enqueueDownload(request: DownloadRequest) {
        downloadQueue.add(request)
        startNext()
    }

    private fun startNext() {
        if (activeDownloads >= maxConcurrentDownloads) return
        val request = downloadQueue.removeFirstOrNull() ?: return

        activeDownloads++

        CoroutineScope(downloadDispatcher).launch {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build()

                val response =
                    client.newCall(Request.Builder().url(request.fileUrl).build()).execute()

                if (!response.isSuccessful && response.body == null) {
                    withContext(Dispatchers.Main) {
                        request.callback(false, "Download failed")
                    }
                    activeDownloads--
                    startNext()
                    return@launch
                }

                val body = response.body!!
                val totalBytes = body.contentLength()
                val file = File(request.folderPath, request.fileName)

                request.startCallBack.invoke(totalBytes)

                body.byteStream().use { input ->
                    file.outputStream().use { output ->
                        val buffer = ByteArray(8 * 1024)
                        var bytesRead: Int
                        var downloadedBytes: Long = 0

                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            downloadedBytes += bytesRead
                            withContext(Dispatchers.Main) {
                                request.progressCallBack(downloadedBytes, totalBytes)
                            }
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    request.callback(true, file.absolutePath)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    request.callback(false, "Download failed: ${e.message}")
                }
            } finally {
                activeDownloads--
                startNext() // start next download in queue
            }
        }
    }
}
