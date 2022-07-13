package com.anatame.localproxy.testutils

import android.util.Log
import android.util.Patterns
import com.anatame.localproxy.AppNetworkClient

import io.github.krlvm.powertunnel.sdk.http.ProxyRequest
import io.github.krlvm.powertunnel.sdk.http.ProxyResponse
import io.github.krlvm.powertunnel.sdk.proxy.DNSRequest
import io.github.krlvm.powertunnel.sdk.proxy.ProxyListener
import io.github.krlvm.powertunnel.sdk.types.FullAddress
import java.net.InetAddress
import java.net.InetSocketAddress

class MProxyListener: ProxyListener {
    override fun onClientToProxyRequest(proxyRequest: ProxyRequest) {
        Log.d("cycle", "onClientToProxyRequest")
        Log.d("clientToProxy", proxyRequest.uri)
        Log.d("clientToProxy", proxyRequest.host)
        Log.d("clientToProxy", proxyRequest.isEncrypted.toString())
        proxyRequest.headers().names().toMutableSet().toList().forEach {
            Log.d("clientToProxy", it.toString() + ": " + proxyRequest.headers().get(it))
        }
    }

    override fun onProxyToServerRequest(proxyRequest: ProxyRequest) {
        Log.d("cycle", "onProxyToServerRequest")
    }

    override fun onServerToProxyResponse(proxyResponse: ProxyResponse) {
        Log.d("cycle", "onServerToProxyResponse")
//        onProxyToClientResponse(proxyResponse)
    }

    override fun onProxyToClientResponse(p0: ProxyResponse) {
        Log.d("cycle", "onProxyToClientResponse")
    }

    override fun onResolutionRequest(dnsRequest: DNSRequest): Boolean {
        Log.d("resolutionRequest", dnsRequest.host)
        Log.d("cycle", "resolutionRequest")

        val records = AppNetworkClient.getClient().dns.lookup(dnsRequest.host)
        var addr: InetAddress? = null
        records.forEach{
            Log.d("dnsResponse", it.hostAddress?.toString().toString())
            if(Patterns.IP_ADDRESS.matcher(it.hostAddress?.toString().toString()).matches()){
                if(addr == null){
                    addr = it
                }
            }
        }

        Log.d("currentAddr", addr?.hostAddress.toString())

        addr?.let { dnsRequest.response = InetSocketAddress(it, 443) }

        return true
    }

    override fun isMITMAllowed(p0: FullAddress): Boolean {
        Log.d("cycle", "isMITMAllowed")
        return false
    }

    override fun onGetChunkSize(p0: FullAddress): Int {
        Log.d("cycle", "onGetChunkSize")
        return 2
    }

    override fun isFullChunking(p0: FullAddress): Boolean {
        Log.d("cycle", "isFullChunking")
        return false
    }

    override fun onGetSNI(p0: String): String? {
        Log.d("cycle", "onGetSNI")
        return null
    }
}