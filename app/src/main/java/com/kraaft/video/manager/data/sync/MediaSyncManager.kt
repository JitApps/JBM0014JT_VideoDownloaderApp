package com.kraaft.video.manager.data.sync

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.kraaft.video.manager.model.SoundFile
import com.kraaft.video.manager.model.SoundModel
import com.kraaft.video.manager.model.VideoFile
import com.kraaft.video.manager.model.VideoModel
import com.kraaft.video.manager.utils.PERMISSION_SOUND
import com.kraaft.video.manager.utils.PERMISSION_VIDEO
import com.kraaft.video.manager.utils.hasPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import java.io.File


class MediaSyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun fetchVideos(): List<VideoModel> {
        if (!context.hasPermission(listOf(PERMISSION_VIDEO))) return emptyList()

        val folderMap = mutableMapOf<String, VideoModel>() // HashMap for fast folder lookup

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_MODIFIED
        )

        val selection =
            "${MediaStore.Video.Media.MIME_TYPE} LIKE 'video/%'" // Correct selection for videos

        context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            "${MediaStore.Video.Media.DATE_ADDED} DESC"
        )?.use { cursor ->

            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val name = cursor.getString(nameCol)
                val path = cursor.getString(dataCol)
                val duration = cursor.getLong(durationCol)
                val size = cursor.getLong(sizeCol)
                val dateModified = cursor.getLong(dateCol)

                val file = File(path)
                if (!file.exists()) continue

                val folderName = file.parentFile?.name ?: "Unknown"

                val uri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val videoFile = VideoFile(
                    id = id,
                    name = name,
                    uri = uri,
                    duration = duration,
                    size = size,
                    dateModified = dateModified
                )

                val folder = folderMap.getOrPut(folderName) {
                    VideoModel(folderName = folderName, videoFiles = mutableListOf())
                }
                folder.videoFiles.add(videoFile)
            }
        }

        return folderMap.values.toList()
    }

    fun fetchSounds(): List<SoundModel> {

        if (!context.hasPermission(listOf(PERMISSION_SOUND))) return emptyList()

        val folderMap = mutableMapOf<String, SoundModel>() // HashMap for fast folder lookup

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATE_MODIFIED
        )

        val selection =
            "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.MIME_TYPE} LIKE 'audio/%'"

        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            "${MediaStore.Audio.Media.DATE_ADDED} DESC"
        )?.use { cursor ->

            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)

            while (cursor.moveToNext()) {

                val id = cursor.getLong(idCol)
                val name = cursor.getString(nameCol)
                val path = cursor.getString(dataCol)
                val duration = cursor.getLong(durationCol)
                val size = cursor.getLong(sizeCol)
                val dateModified = cursor.getLong(dateCol)

                val file = File(path)

                if (!file.exists()) continue

                val folderName = file.parentFile?.name ?: "Unknown"

                val uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val soundFile = SoundFile(
                    id = id,
                    name = name,
                    uri = uri,
                    duration = duration,
                    size = size,
                    dateModified = dateModified
                )

                val folder = folderMap.getOrPut(folderName) {
                    SoundModel(folderName = folderName, soundFiles = mutableListOf())
                }
                folder.soundFiles.add(soundFile)
            }
        }

        return folderMap.values.toList()
    }

}