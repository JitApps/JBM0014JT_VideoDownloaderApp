package com.kraaft.video.manager.utils

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
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import java.io.File


fun getStatusFilesFolder(): String {
    return if (Build.VERSION.SDK_INT <= 30) {
        Environment.getExternalStorageDirectory()
            .toString() + File.separator + "WhatsApp" + File.separator + "Media" + File.separator + ".Statuses"
    } else {
        Environment.getExternalStorageDirectory()
            .toString() + File.separator + "Android/media/com.whatsapp/WhatsApp/Media/.Statuses"
    }
}

fun getBusinessFilesFolder(): String {
    return if (Build.VERSION.SDK_INT <= 30) {
        Environment.getExternalStorageDirectory()
            .toString() + File.separator + "WhatsApp Business" + File.separator + "Media" + File.separator + ".Statuses"
    } else {
        Environment.getExternalStorageDirectory()
            .toString() + File.separator + "Android/media/com.whatsapp.w4b/WhatsApp Business/Media/.Statuses"
    }
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

fun Activity.gotoActivity(activityClass: Class<*>?, isFinish: Boolean) {
    sendIntent(this, Intent(this, activityClass), isFinish)
}

fun Activity.gotoIntent(intent: Intent?, isFinish: Boolean) {
    sendIntent(this, intent, isFinish)
}

fun sendIntent(activity: Activity, intent: Intent?, isFinish: Boolean) {
    activity.startActivity(intent)
    if (isFinish) activity.finish()
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

fun Context.handleResponse(
    response: Response<ResponseBody>,
    data: MutableLiveData<NetworkResult<ResponseBody>>
) {
    if (response.isSuccessful && response.body() != null) {
        data.postValue(NetworkResult.Success(response.body()!!))
    } else if (response.errorBody() != null) {
        try {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            data.postValue(NetworkResult.Error(errorObj.getString("message")))
        } catch (e: Exception) {
            e.printStackTrace()
            data.postValue(NetworkResult.Error(resources.getString(R.string.kk_error_unknown)))
        }
    } else {
        data.postValue(NetworkResult.Error(resources.getString(R.string.kk_error_unknown)))
    }
}

fun View.onSingleClick(debounceTime: Long = 1500, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action()
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
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