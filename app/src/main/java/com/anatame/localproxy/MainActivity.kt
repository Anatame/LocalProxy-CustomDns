package com.anatame.localproxy


import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.anatame.localproxy.databinding.ActivityMainBinding
import com.anatame.localproxy.dns.DNSListener
import io.github.krlvm.powertunnel.BuildConstants
import io.github.krlvm.powertunnel.PowerTunnel
import io.github.krlvm.powertunnel.mitm.MITMAuthority
import io.github.krlvm.powertunnel.sdk.plugin.PluginInfo
import io.github.krlvm.powertunnel.sdk.proxy.DNSResolver
import io.github.krlvm.powertunnel.sdk.proxy.ProxyAddress
import io.github.krlvm.powertunnel.sdk.types.PowerTunnelPlatform
import okhttp3.*
import okhttp3.Cache
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.dnsoverhttps.DnsOverHttps
import org.xbill.DNS.*
import java.io.File
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.UnknownHostException
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var server: PowerTunnel

    private val PLUGIN_INFO = PluginInfo(
        "android-app",
        BuildConfig.VERSION_NAME,
        BuildConfig.VERSION_CODE,
        "PowerTunnel-Android",
        "Powerful and extensible proxy server",
        "krlvm",
        "https://github.com/krlvm/PowerTunnel-Android",
        null,
        BuildConstants.VERSION_CODE,
        null
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initServer(this)
        server.start()
        Log.d("serverStatus", server.isRunning.toString())

        val resolver = SimpleResolver("1.1.1.1")

        server.registerProxyListener(PLUGIN_INFO, DNSListener(object: DNSResolver{
            override fun resolve(host: String, port: Int): InetSocketAddress {
                // lookupDnsWithDnsJava(host, resolver, port)
                return lookupDnsWithOkHttp(host, port)
            }
        }))


        Handler(Looper.getMainLooper()).postDelayed({
            Proxify(
                binding.webView,
                "127.0.0.1",
                8085,
            )
            binding.webView.loadUrl("https://fmovies.to")
        }, 5000)


    }

    private fun lookupDnsWithOkHttp(host: String, port: Int): InetSocketAddress{
        val appCache = Cache(File("cacheDir", "okhttpcache"), 10 * 1024 * 1024L)

        val bootstrapClient = OkHttpClient.Builder().cache(appCache).build()

        val dns = DnsOverHttps.Builder().client(bootstrapClient)
            .url("https://cloudflare-dns.com/dns-query".toHttpUrl())
            .build()

        val client = bootstrapClient.newBuilder().dns(dns).build()

        try {
            val records = client.dns.lookup(host)
            Log.d("logOkHttpResponse", records[0].toString())
            return InetSocketAddress(records[0].hostAddress, port)
        } catch(e: Exception) {
            e.printStackTrace()
            throw UnknownHostException()
        }


//        Log.d("logOkHttpResponse", client.dns.lookup(host).toString())
//
//        val request = Request.Builder()
//            .url("https://$host")
//            .build()
//
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                Log.d("logOkHttpResponse", "failure")
//                e.printStackTrace()
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                Log.d("logOkHttpResponse", response.code.toString())
//                Log.d("logOkHttpResponse",  response.body?.string().toString())
//            }
//        })
    }

    private fun lookupDnsWithDnsJava(
        host: String,
        resolver: SimpleResolver,
        port: Int
    ): InetSocketAddress {
        val lookup: Lookup

        Log.d("lookupFrom", host)

        try {
            lookup = Lookup(host, Type.ANY)
        } catch (ex: TextParseException) {
            throw UnknownHostException()
        }

        lookup.setResolver(resolver)
        val records = lookup.run()
        if (lookup.result == Lookup.SUCCESSFUL) {

            Log.d(
                "lookupFrom", """
                            FUCK YEAH!
                            
                            ADDRESS: ${(records[0] as ARecord).address}
                            PORT: $port
                            
                            ${(records[0] as ARecord).name}
                            ${(records[0] as ARecord).additionalName}
                          
                        """.trimIndent()
            )

            return InetSocketAddress(
                (records[0] as ARecord).address,
                port
            )
        } else {
            throw UnknownHostException()
        }
    }


    private fun initServer(context: Context){
        server = PowerTunnel(
            getAddress(),
            PowerTunnelPlatform.ANDROID,
            context.getFilesDir(),
            true,
            true,
            getDefaultDNS(this),
            getDNSDomainsSearchPath(context),
            MITMAuthority.create(
                File(context.getFilesDir(), "cert"),
            getMitmCertificatePassword()?.toCharArray()
        ),
            if (ConfigurationManager.isUseExternalConfigs(context)) ConfigurationManager.getExternalConfigsDirectory(
                context
            ) else null,
            null
        )
    }

    fun getAddress(): ProxyAddress? {
        return ProxyAddress(
            "127.0.0.1",
            8085
        )
    }

    fun getDefaultDNS(context: Context): List<String>? {
        val servers: MutableList<String> = ArrayList()

            val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = cm.activeNetwork
            if (network != null) {
                val properties = cm.getLinkProperties(network)
                if (properties != null) {
                    val addresses = properties.dnsServers
                    for (address in addresses) {
                        val host = address.hostAddress
                        if (TextUtils.isEmpty(host)) continue
                        servers.add(host)
                    }
                }
            }

        return servers
    }

    fun getDNSDomainsSearchPath(context: Context): String? {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = cm.activeNetwork
            if (network != null) {
                val properties = cm.getLinkProperties(network)
                if (properties != null) {
                    return properties.domains
                }
            }
        }
        return null
    }

    private fun getMitmCertificatePassword(): String? {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (!prefs.contains("cert_password")) {
            val password = UUID.randomUUID().toString()
            prefs.edit().putString("cert_password", password).apply()
            return password
        }
        return prefs.getString("cert_password", null)
    }

}