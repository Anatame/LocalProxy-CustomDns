package com.anatame.localproxy

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.anatame.localproxy.databinding.ActivityMainBinding
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.HttpObject
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponse
import org.littleshoot.proxy.HttpFilters
import org.littleshoot.proxy.HttpFiltersAdapter
import org.littleshoot.proxy.HttpFiltersSourceAdapter
import org.littleshoot.proxy.impl.DefaultHttpProxyServer


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val server = DefaultHttpProxyServer.bootstrap()
            .withPort(8080)
            .withFiltersSource(object : HttpFiltersSourceAdapter() {
                override fun filterRequest(
                    originalRequest: HttpRequest?,
                    ctx: ChannelHandlerContext?
                ): HttpFilters? {
                    return object : HttpFiltersAdapter(originalRequest) {
                        override fun clientToProxyRequest(httpObject: HttpObject): HttpResponse? {
                            Log.d("clientToProxy", httpObject.toString())
                            return null
                        }

                        override fun serverToProxyResponse(httpObject: HttpObject): HttpObject? {
                            Log.d("serverToProxyResponse", httpObject.toString())
                            return null
                        }
                    }
                }
            })
            .start()


        Proxify(binding.webView, "localhost", 8080)

        Handler(Looper.getMainLooper()).postDelayed({
            binding.webView.loadUrl("https://www.google.com/")
        }, 5000)
    }
}