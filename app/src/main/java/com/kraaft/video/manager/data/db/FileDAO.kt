package com.kraaft.video.manager.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.kraaft.video.manager.model.FileEntity
import com.kraaft.video.manager.model.FolderCount
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDAO {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(files: List<FileEntity>)

    @Transaction
    @Update
    suspend fun updateAll(files:  List<FileEntity>)

    @Transaction
    @Delete
    suspend fun deleteAll(files: List<FileEntity>)


    @Transaction
    @Query("SELECT * FROM FileEntity WHERE fileType = :mediaType")
    suspend fun getAllFiles(mediaType: Int): List<FileEntity>

    @Transaction
    @Query("""
        SELECT folderPath, COUNT(*) AS totalCount
        FROM FileEntity WHERE fileType = :mediaType
        GROUP BY folderPath
    """)
    fun getAllFolders(mediaType: Int): List<FolderCount>

    @Transaction
    @Query("SELECT * FROM FileEntity WHERE fileType = :mediaType")
    fun getFilesByType(mediaType: Int): Flow<List<FileEntity>>

    @Transaction
    @Query("SELECT * FROM FileEntity WHERE folderPath = :folderPath AND fileType = :mediaType")
    fun getFolderFilesByType(folderPath:String,mediaType: Int): Flow<List<FileEntity>>

    @Transaction
    @Query("""
        SELECT folderPath, COUNT(*) AS totalCount
        FROM FileEntity WHERE fileType = :mediaType
        GROUP BY folderPath
    """)
    fun getFoldersByType(mediaType: Int): Flow<List<FolderCount>>

    @Transaction
    @Query("SELECT * FROM FileEntity WHERE playName = :playName")
    fun getFileByPlay(playName: String): Flow<List<FileEntity>>

}