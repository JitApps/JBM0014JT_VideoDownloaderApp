package com.kraaft.video.manager.api

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BaseUrlInterceptor @Inject constructor() : Interceptor {

    @Volatile
    private var baseUrl: HttpUrl? = null

    fun updateBaseUrl(url: String) {
        baseUrl = url.toHttpUrlOrNull()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newBaseUrl = baseUrl ?: return chain.proceed(request)

        val newUrl = request.url.newBuilder()
            .scheme(newBaseUrl.scheme)
            .host(newBaseUrl.host)
            .port(newBaseUrl.port)
            .build()

        return chain.proceed(
            request.newBuilder().url(newUrl).build()
        )
    }
}
