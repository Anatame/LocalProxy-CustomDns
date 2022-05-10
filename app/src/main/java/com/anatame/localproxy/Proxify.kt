package com.anatame.localproxy

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.ProxyConfig
import androidx.webkit.ProxyController
import androidx.webkit.WebViewFeature
import java.util.concurrent.Executor

class Proxify(
    private val webview: WebView,
    private val host: String,
    private val port: Int,
    applicationClassName: String = "ProxyApplication"
) {

    init {
        webview.settings.javaScriptEnabled = true
        webview.webViewClient = object: WebViewClient(){
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                return super.shouldInterceptRequest(view, request)
            }
        }

        setProxy(host, port)
    }

    private fun setProxy(host: String, port: Int) {
        if (WebViewFeature.isFeatureSupported(WebViewFeature.PROXY_OVERRIDE)) {
            val proxyUrl = "${host}:${port}"
            val proxyConfig: ProxyConfig = ProxyConfig.Builder()
                .addProxyRule(proxyUrl)
                .addDirect()//when proxy is not working, use direct connect, maybe?
                .build()
            ProxyController.getInstance().setProxyOverride(proxyConfig, object : Executor {
                override fun execute(command: Runnable) {

                }
            }, Runnable { Log.w("fromProxy", "WebView proxy") })
        } else {
            // use the solution of other anwsers
        }
    }
}