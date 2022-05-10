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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.HandlerCompat.postDelayed
import androidx.webkit.ProxyConfig
import androidx.webkit.ProxyController
import androidx.webkit.WebViewFeature
import com.anatame.localproxy.databinding.ActivityMainBinding
import com.anatame.localproxy.iiberty_tunnel.LibertyTunnel
import io.github.krlvm.powertunnel.BuildConstants
import io.github.krlvm.powertunnel.PowerTunnel
import io.github.krlvm.powertunnel.mitm.MITMAuthority
import io.github.krlvm.powertunnel.plugin.PluginLoader
import io.github.krlvm.powertunnel.sdk.plugin.PluginInfo
import io.github.krlvm.powertunnel.sdk.proxy.ProxyAddress
import io.github.krlvm.powertunnel.sdk.types.PowerTunnelPlatform
import java.io.File
import java.util.*
import java.util.concurrent.Executor


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

        server.registerProxyListener(PLUGIN_INFO, MProxyListener())

        Handler(Looper.getMainLooper()).postDelayed({
            Proxify(
                binding.webView,
                "127.0.0.1",
                8085,
            )

            binding.webView.loadUrl("https://google.com")
        }, 10000)

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