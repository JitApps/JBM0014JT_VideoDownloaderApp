package com.kraaft.video.manager.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface RetroAPI {
    @Headers("Content-Type: application/json")
    @POST("/auth/login")
    suspend fun loginApp(
        @Header("X-API-KEY") authHeader: String,
        @Body body: RequestBody
    ): Response<ResponseBody>
}