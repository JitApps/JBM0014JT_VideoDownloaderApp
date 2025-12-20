package com.kraaft.video.manager.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.kraaft.video.manager.R
import com.kraaft.video.manager.model.NetworkResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import java.io.File


fun getWhatsPath(): String {
    val folderPath =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + File.pathSeparator + "Whatsapp"
    if (!File(folderPath).exists()) {
        File(folderPath).mkdirs()
    }
    return folderPath
}


fun getDownloadsPath(): String {
    val folderPath =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + File.pathSeparator + "Other"
    if (!File(folderPath).exists()) {
        File(folderPath).mkdirs()
    }
    return folderPath
}

fun getStatusFolder(): String {
    return if (Build.VERSION.SDK_INT <= 30) {
        "WhatsApp%2FMedia%2F.Statuses"
    } else {
        "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses"
    }
}

fun getBusinessFolder(): String {
    return if (Build.VERSION.SDK_INT <= 30) {
        "WhatsApp%20Business%2FMedia%2F.Statuses"
    } else {
        "Android%2Fmedia%2Fcom.whatsapp.w4b%2FWhatsApp%20Business%2FMedia%2F.Statuses"
    }
}

fun Activity.gotoActivity(activityClass: Class<*>, isFinish: Boolean) {
    sendIntent(Intent(this, activityClass), isFinish)
}

fun Activity.gotoIntent(intent: Intent, isFinish: Boolean) {
    sendIntent( intent, isFinish)
}

private fun Activity.sendIntent( intent: Intent, isFinish: Boolean) {
    startActivity(intent)
    if (isFinish) finish()
}

fun delayTask(timer: Long = 2000, action: () -> Unit) {
    CoroutineScope(Dispatchers.Default).launch {
        delay(timer)
        action.invoke()
    }
}

fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun showLog(text: String = "log") {
    try {
        Log.e("logError", text)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

suspend fun Context.handleResponse(
    response: Response<ResponseBody>,
    data: MutableStateFlow<NetworkResult<ResponseBody>>
) {
    response.body()?.let {
        data.emit(NetworkResult.Success(it))
    } ?: response.errorBody()?.let {
        val jsonObject = JSONObject(it.toString())
        data.emit(
            NetworkResult.Error(
                jsonObject.optString("message")
                    .ifEmpty {
                        resources.getString(R.string.kk_error_unknown)
                    })
        )
    } ?: run {
        data.emit(NetworkResult.Error(resources.getString(R.string.kk_error_unknown)))
    }
}

fun Context.getProgressImage(): CircularProgressDrawable {
    return CircularProgressDrawable(this).apply {
        strokeWidth = 10f
        centerRadius = 50f
        setColorSchemeColors(
            getColor(R.color.colorMain)
        )
        start()
    }
}

fun Context.isPackageInstalled(packageName: String): Boolean {
    try {
        packageManager.getPackageInfo(packageName, 0)
        return true
    } catch (e: PackageManager.NameNotFoundException) {
        return false
    }
}

@SuppressLint("DefaultLocale")
fun Long.formatDuration(): String {
    val totalSeconds = this / 1000
    val seconds = (totalSeconds % 60).toInt()
    val minutes = ((totalSeconds / 60) % 60).toInt()
    val hours = (totalSeconds / 3600).toInt()

    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
