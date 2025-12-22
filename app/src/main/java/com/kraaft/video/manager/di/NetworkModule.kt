package com.kraaft.video.manager.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kraaft.video.manager.api.BaseUrlInterceptor
import com.kraaft.video.manager.api.RetroAPI
import com.kraaft.video.manager.api.VpnInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder().setLenient().create()

    @Provides
    @Singleton
    fun provideOkHttpClient(
        baseUrlInterceptor: BaseUrlInterceptor,
        vpnInterceptor: VpnInterceptor
    ): OkHttpClient {

        val dispatcher = Dispatcher().apply {
            maxRequests = 1
        }

        return OkHttpClient.Builder()
            .addInterceptor(baseUrlInterceptor)
            .addInterceptor(vpnInterceptor)
            .connectTimeout(20, TimeUnit.SECONDS)
            .dispatcher(dispatcher)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        gson: Gson,
        okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://google.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideRetroApi(retrofit: Retrofit): RetroAPI =
        retrofit.create(RetroAPI::class.java)
}
