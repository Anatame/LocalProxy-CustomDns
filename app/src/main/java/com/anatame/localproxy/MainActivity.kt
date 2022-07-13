package com.anatame.localproxy


import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.anatame.localproxy.databinding.ActivityMainBinding
import com.anatame.localproxy.server.Server


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        Server().run()

        Proxify(binding.webView, "127.0.0.1", 9999)

//        binding.webView.settings.domStorageEnabled = true;
//        binding.webView.settings.javaScriptEnabled = true

        var startTime: Long = 0
        var endTime: Long = 0

        binding.webView.webViewClient = object: WebViewClient() {
//            override fun onReceivedSslError(
//                view: WebView?,
//                handler: SslErrorHandler,
//                error: SslError
//            ) {
//                Log.d("dambBruh", "not callled")
//                handler.proceed() // Ignore SSL certificate errors
//            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                startTime = System.currentTimeMillis()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                endTime = System.currentTimeMillis()
                val totalTime = endTime.minus(startTime)

                Log.d("totalTimeTaken: ", totalTime.toString())
            }
        }
        binding.webView.loadUrl("https://9anime.to")
    }

}