package com.anatame.localproxy

import android.util.Log
import com.anatame.localproxy.helpers.HeadersHelper
import com.anatame.localproxy.helpers.ProxyResponseHelper
import io.github.krlvm.powertunnel.sdk.http.HttpHeaders

import io.github.krlvm.powertunnel.sdk.http.ProxyRequest
import io.github.krlvm.powertunnel.sdk.http.ProxyResponse
import io.github.krlvm.powertunnel.sdk.proxy.DNSRequest
import io.github.krlvm.powertunnel.sdk.proxy.ProxyListener
import io.github.krlvm.powertunnel.sdk.types.FullAddress
import okhttp3.*
import okhttp3.Headers.Companion.toHeaders
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.dnsoverhttps.DnsOverHttps

class MProxyListener: ProxyListener {
    override fun onClientToProxyRequest(proxyRequest: ProxyRequest) {
        Log.d("cycle", "onClientToProxyRequest")
        Log.d("clientToProxy", proxyRequest.uri)
        Log.d("clientToProxy", proxyRequest.host)
        Log.d("clientToProxy", proxyRequest.isEncrypted.toString())
        proxyRequest.headers().names().toMutableSet().toList().forEach {
            Log.d("clientToProxy", it.toString() + ": " + proxyRequest.headers().get(it))
        }

        onProxyToServerRequest(proxyRequest)
    }

    override fun onProxyToServerRequest(proxyRequest: ProxyRequest) {
        Log.d("cycle", "onProxyToServerRequest")

        onResolutionRequest(DNSRequest(proxyRequest.host, 443))

        val headers = HashMap<String,String>()

        proxyRequest.headers().names().toMutableSet().toList().forEach {
            headers.put(it.toString(), proxyRequest.headers().get(it).toString())
        }

        val request = Request.Builder()
            .headers(headers.toHeaders())
            .url("wss://" + proxyRequest.uri)
            .build()

        var fullAddress: FullAddress? = null
        var response: Response? = null

        val bootstrapClient = OkHttpClient.Builder().build()

        val dns = DnsOverHttps.Builder().client(bootstrapClient).url("https://cloudflare-dns.com/AppNetworkClientdns-query".toHttpUrl()).build()

        val client = bootstrapClient.newBuilder().dns(dns).eventListener(object : EventListener() {
            override fun connectionAcquired(call: Call, connection: Connection) {
                val addr = connection.route().socketAddress.address
                fullAddress = FullAddress(addr?.hostAddress.toString(), 443)
                super.connectionAcquired(call, connection)
            }
        }).build()

        response = client.newCall(request).execute()

        val proxyResponse = ProxyResponseHelper(fullAddress, response)
        this.onServerToProxyResponse(proxyResponse)
    }

    override fun onServerToProxyResponse(proxyResponse: ProxyResponse) {
        Log.d("cycle", "onServerToProxyResponse")
        onProxyToClientResponse(proxyResponse)
    }

    override fun onProxyToClientResponse(p0: ProxyResponse) {
        Log.d("cycle", "onProxyToClientResponse")
    }

    override fun onResolutionRequest(p0: DNSRequest): Boolean {
        Log.d("resolutionRequest", p0.host)
        Log.d("cycle", "resolutionRequest")
        return true
    }

    override fun isMITMAllowed(p0: FullAddress): Boolean {
        Log.d("cycle", "isMITMAllowed")
        return false
    }

    override fun onGetChunkSize(p0: FullAddress): Int {
        Log.d("cycle", "onGetChunkSize")
        return 517
    }

    override fun isFullChunking(p0: FullAddress): Boolean {
        Log.d("cycle", "isFullChunking")
        return false
    }


    override fun onGetSNI(p0: String): String? {
        Log.d("cycle", "onGetSNI")
        return p0
    }
}