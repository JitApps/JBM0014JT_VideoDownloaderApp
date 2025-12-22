package com.kraaft.video.manager.api

import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.net.NetworkInterface
import java.net.SocketException
import java.util.Collections
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VpnInterceptor @Inject constructor() : Interceptor {


    fun isVpnConnected(): Boolean {
        var iFace = ""
        try {
            for (networkInterface in Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp())
                    iFace = networkInterface.name

                if (iFace.contains("tun") || iFace.contains("ppp") || iFace.contains("pptp")) {
                    return true
                }
            }
        } catch (e1: Exception) {
            e1.printStackTrace()
        }
        return false
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return if (isVpnConnected()) {
            Response.Builder()
                .code(404)
                .protocol(Protocol.HTTP_2)
                .message("not-verified")
                .request(chain.request())
                .body("".toResponseBody(null))
                .build()
        } else {
            chain.proceed(chain.request())
        }
    }
}
