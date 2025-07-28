package com.kraaft.video.manager.di

import com.kraaft.video.manager.api.RetroAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun providesRetrofitClient(): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl("https://acharyaadmin.in")
    }

    @Singleton
    @Provides
    fun providesClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

    }

    @Singleton
    @Provides
    fun providesRetroAPI(retrofit: Retrofit.Builder, okHttpClient: OkHttpClient): RetroAPI {
        return retrofit.addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(RetroAPI::class.java)
    }

}