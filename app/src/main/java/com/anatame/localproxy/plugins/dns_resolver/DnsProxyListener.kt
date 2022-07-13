package com.anatame.localproxy.plugins.dns_resolver

import android.util.Log
import android.util.Patterns
import com.anatame.localproxy.AppNetworkClient
import io.github.krlvm.powertunnel.sdk.http.ProxyRequest
import io.github.krlvm.powertunnel.sdk.proxy.DNSRequest
import io.github.krlvm.powertunnel.sdk.proxy.ProxyAdapter
import java.net.InetAddress
import java.net.InetSocketAddress

class DnsProxyListener: ProxyAdapter(){
    override fun onClientToProxyRequest(request: ProxyRequest) {
        super.onClientToProxyRequest(request)
        Log.d("PluginTester", "Dns Plugin")
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
}