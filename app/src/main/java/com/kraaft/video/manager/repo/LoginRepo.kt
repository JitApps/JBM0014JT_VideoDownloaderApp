package com.kraaft.video.manager.repo

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kraaft.video.manager.R
import com.kraaft.video.manager.api.RetroAPI
import com.kraaft.video.manager.model.NetworkResult
import com.kraaft.video.manager.utils.handleResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.RequestBody
import okhttp3.ResponseBody
import javax.inject.Inject

class LoginRepo @Inject constructor(
    @ApplicationContext val context: Context,
    private val retroAPI: RetroAPI
) {

    private val _loginData = MutableLiveData<NetworkResult<ResponseBody>>()
    val loginData: LiveData<NetworkResult<ResponseBody>>
        get() = _loginData

    suspend fun loginApp(requestBody: RequestBody) {
        _loginData.postValue(NetworkResult.Loading())
        try {
            context.handleResponse(retroAPI.loginApp("X_API_KEY",requestBody), _loginData)
        } catch (e: Exception) {
            e.printStackTrace()
            _loginData.postValue(NetworkResult.Error(context.getString(R.string.kk_error_unknown)))
        }
    }

}