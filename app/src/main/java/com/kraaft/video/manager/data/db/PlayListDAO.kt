package com.kraaft.video.manager.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kraaft.video.manager.model.PlayList
import kotlinx.coroutines.flow.Flow
import retrofit2.http.DELETE

@Dao
interface PlayListDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMedia(products: List<PlayList>)

    @Delete
    fun deleteMedia(products: List<PlayList>)

    @Query("DELETE FROM PlayList WHERE playName = :playName")
    fun deleteMediaByType(playName: String)

    @Query("SELECT * FROM PlayList WHERE playName = :playName")
    fun getMedia(playName: String): Flow<List<PlayList>>

}