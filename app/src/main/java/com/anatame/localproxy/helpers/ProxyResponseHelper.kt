package com.anatame.localproxy.helpers

import android.util.Log
import io.github.krlvm.powertunnel.sdk.http.HttpHeaders
import io.github.krlvm.powertunnel.sdk.http.ProxyResponse
import io.github.krlvm.powertunnel.sdk.types.FullAddress
import okhttp3.Response

class ProxyResponseHelper(
    private val fullAddress: FullAddress?,
    private val response: Response?
): ProxyResponse {

    private var rawBody: String = response?.body?.string() ?: ""
    private var responseCode: Int = response?.code ?: 200

    override fun address(): FullAddress? {
        Log.d("responseAddress", fullAddress?.host.toString())
        return fullAddress
    }

    override fun headers(): HttpHeaders {
        return HeadersHelper(response)
    }

    override fun setRaw(p0: String) {
        rawBody = p0
    }

    override fun raw(): String {
        Log.d("responseBody", rawBody)
        return rawBody
    }

    override fun code(): Int {
        return responseCode
    }

    override fun setCode(p0: Int) {
        responseCode = p0
    }

    override fun isDataPacket(): Boolean {
        return true
    }

}