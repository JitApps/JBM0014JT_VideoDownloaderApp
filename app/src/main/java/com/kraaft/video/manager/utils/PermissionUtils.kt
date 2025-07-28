package com.kraaft.video.manager.utils

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.kraaft.video.manager.R

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