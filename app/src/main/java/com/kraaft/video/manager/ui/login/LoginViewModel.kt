package com.kraaft.video.manager.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kraaft.video.manager.model.NetworkResult
import com.kraaft.video.manager.repo.LoginRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepo: LoginRepo) : ViewModel() {

    val loginData: LiveData<NetworkResult<ResponseBody>>
        get() = loginRepo.loginData

    fun loginApp(phoneNumber: String, password: String) {
        val jsonObject = JSONObject()
        jsonObject.put("institutionId", 1)
        jsonObject.put("phoneNumber", phoneNumber)
        jsonObject.put("password", password)
        val requestBody = jsonObject.toString().toRequestBody("text/json".toMediaTypeOrNull())
        viewModelScope.launch(Dispatchers.IO) {
            loginRepo.loginApp(requestBody)
        }
    }
}