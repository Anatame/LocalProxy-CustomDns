package com.anatame.localproxy

import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.preference.PreferenceManager
import io.github.krlvm.powertunnel.BuildConstants
import io.github.krlvm.powertunnel.PowerTunnel
import io.github.krlvm.powertunnel.mitm.MITMAuthority
import io.github.krlvm.powertunnel.sdk.plugin.PluginInfo
import io.github.krlvm.powertunnel.sdk.proxy.ProxyAddress
import io.github.krlvm.powertunnel.sdk.types.PowerTunnelPlatform
import java.io.File
import java.util.*

class Server {
    private val LOG_TAG = "ProxyManager"
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

    private val address: ProxyAddress = ProxyAddress("127.0.0.1", 9999)

    fun run(context: Context){
        val server = PowerTunnel(
            address,
            PowerTunnelPlatform.ANDROID,
            null,
            true,
            false,
            null,
            null,
            null,
            null,
            null
        )

        server.registerProxyListener(PLUGIN_INFO, MProxyListener())


        server.start()
    }
}