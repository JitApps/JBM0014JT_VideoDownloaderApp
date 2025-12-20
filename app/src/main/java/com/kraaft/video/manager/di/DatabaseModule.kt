package com.kraaft.video.manager.di

import android.content.Context
import androidx.room.Room
import com.kraaft.video.manager.data.db.AppDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.jvm.java

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDB(@ApplicationContext context : Context) : AppDB{
        return Room.databaseBuilder(context, AppDB::class.java, "videoDB")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun providePlayListDao(db: AppDB) = db.getPlayListDao()
}