package com.kraaft.video.manager.data.db

import android.content.Context
import com.kraaft.video.manager.data.sync.MediaSyncManager
import com.kraaft.video.manager.model.FileEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DbHelper @Inject constructor(
    @ApplicationContext val context: Context,
    private val fileDao: FileDAO
) {

    suspend fun insertAll(event: List<FileEntity>) = fileDao.insertAll(event)
    suspend fun updateAll(event: List<FileEntity>) = fileDao.updateAll(event)
    suspend fun deleteAll(event: List<FileEntity>) = fileDao.deleteAll(event)

    suspend fun addToPlayList(fileEntity: FileEntity, playName: String) {
        fileDao.updateAll(listOf(fileEntity.copy(playName = listOf(playName))))
    }

    suspend fun getAllFiles(fileType: Int) = fileDao.getAllFiles(fileType)
    suspend fun getAllFolders(fileType: Int) = fileDao.getAllFolders(fileType)
    suspend fun getFileByPlay(playName: String) = fileDao.getFileByPlay(playName)


    fun getFilesByType(fileType: Int) = fileDao.getFilesByType(fileType)
    fun getFoldersByType(fileType: Int) = fileDao.getFoldersByType(fileType)
    fun getFolderFilesByType(folderPath: String, fileType: Int) =
        fileDao.getFolderFilesByType(folderPath, fileType)

    suspend fun removeFromPlaylist(playFile: FileEntity) {
        fileDao.updateAll(listOf(playFile))
    }

    suspend fun diffFiles(
        dbFiles: List<FileEntity>, systemFiles: List<FileEntity>
    ): DiffResult {
        val systemMap = systemFiles.associateBy { it.itemId }
        val dbMap = dbFiles.associateBy { it.itemId }

        val toInsert = mutableListOf<FileEntity>()
        val toUpdate = mutableListOf<FileEntity>()
        val toDelete = mutableListOf<FileEntity>()
        for ((id, sysFile) in systemMap) {
            val dbFile = dbMap[id]
            if (dbFile == null) {
                toInsert.add(sysFile)
            } else if (hasFileChanged(dbFile, sysFile)) {
                toUpdate.add(sysFile)
            }
        }
        for ((id, dbFile) in dbMap) {
            if (!systemMap.containsKey(id))
                toDelete.add(dbFile)
        }
        return DiffResult(toInsert, toUpdate, toDelete)
    }

    private fun hasFileChanged(dbFile: FileEntity, sysFile: FileEntity): Boolean {
        return dbFile.name != sysFile.name
                || dbFile.folderPath != sysFile.folderPath
                || dbFile.filePath != sysFile.filePath
                || dbFile.size != sysFile.size
                || dbFile.duration != sysFile.duration
                || dbFile.dateModified != sysFile.dateModified
    }

    data class DiffResult(
        val toInsert: List<FileEntity>,
        val toUpdate: List<FileEntity>,
        val toDelete: List<FileEntity>
    )
}