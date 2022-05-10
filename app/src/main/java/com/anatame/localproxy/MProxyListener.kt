package com.anatame.localproxy

import android.util.Log
import com.anatame.localproxy.iiberty_tunnel.SNITrick
import io.github.krlvm.powertunnel.sdk.http.ProxyRequest
import io.github.krlvm.powertunnel.sdk.http.ProxyResponse
import io.github.krlvm.powertunnel.sdk.proxy.DNSRequest
import io.github.krlvm.powertunnel.sdk.proxy.ProxyListener
import io.github.krlvm.powertunnel.sdk.types.FullAddress

class MProxyListener: ProxyListener {
    override fun onClientToProxyRequest(p0: ProxyRequest) {
        Log.d("clientToProxy", p0.uri)
    }

    override fun onProxyToServerRequest(p0: ProxyRequest) {

    }

    override fun onServerToProxyResponse(p0: ProxyResponse) {

    }

    override fun onProxyToClientResponse(p0: ProxyResponse) {

    }

    override fun onResolutionRequest(p0: DNSRequest): Boolean {
        return true
    }

    override fun onGetChunkSize(p0: FullAddress): Int {
        return 1
    }

    override fun isFullChunking(p0: FullAddress): Boolean {
        return true
    }

    override fun isMITMAllowed(p0: FullAddress): Boolean {
        return true
    }

    override fun onGetSNI(p0: String): String? {
        return p0
    }
}