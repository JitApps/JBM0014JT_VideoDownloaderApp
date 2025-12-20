package com.kraaft.video.manager.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.kraaft.video.manager.R
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.ForwardToSettingsCallback
import com.permissionx.guolindev.callback.RequestCallback
import com.permissionx.guolindev.request.ForwardScope


fun getPermissions(): ArrayList<String> {
    val perms: ArrayList<String> = ArrayList()
    perms.add(Manifest.permission.ACCESS_NETWORK_STATE)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        perms.add(Manifest.permission.POST_NOTIFICATIONS)
    }
    if (Build.VERSION.SDK_INT >= 28) {
        perms.add(Manifest.permission.FOREGROUND_SERVICE)
    }
    return perms
}

fun AppCompatActivity.askPermissions(permissions: List<Int>, callBack: () -> Unit) {
    if (hasPermission(permissions)) {
        callBack.invoke()
    } else {
        PermissionX.init(this)
            .permissions(permissions.map { getPermissionString(it) })
            .explainReasonBeforeRequest()
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "You need to allow necessary permissions in Settings manually",
                    "OK",
                    "Cancel"
                )
            }.request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    callBack.invoke()
                } else if (!deniedList.isEmpty()) {
                    showToast("These Permissions are Denied: " + deniedList[0])
                } else {
                    showToast("Permissions are Denied")
                }
            }
    }
}

fun Context.hasPermission(permissions: List<Int>): Boolean {
    return permissions.all { perm ->
        ContextCompat.checkSelfPermission(
            this,
            getPermissionString(perm)
        ) == PackageManager.PERMISSION_GRANTED
    }
}

fun Context.checkInternetMain(action: () -> Unit) {
    if (isNetworkAvailable()) {
        action.invoke()
    } else {
        showForceDialog(getString(R.string.kk_error_no_internet), "Retry") {
            checkInternetMain(action)
        }
    }
}

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetwork
    if (activeNetwork != null) {
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork)
        if (networkCapabilities != null) {
            return (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
        }
    } else {
        return false
    }
    return false
}