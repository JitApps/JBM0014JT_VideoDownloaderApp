package com.kraaft.video.manager.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kraaft.video.manager.model.PlayList

@Database(entities = [PlayList::class], version = 1)
@TypeConverters(AppConverters::class)
abstract class AppDB : RoomDatabase() {

    abstract fun getPlayListDao() : PlayListDAO

}