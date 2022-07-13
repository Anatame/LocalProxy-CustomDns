package com.anatame.localproxy.server

import com.anatame.localproxy.BuildConfig
import com.anatame.localproxy.testutils.MProxyListener
import com.anatame.localproxy.plugins.anti_censorship.AntiCensorshipPlugin
import com.anatame.localproxy.plugins.dns_resolver.DnsResolverPlugin
import io.github.krlvm.powertunnel.BuildConstants
import io.github.krlvm.powertunnel.PowerTunnel
import io.github.krlvm.powertunnel.sdk.plugin.PluginInfo
import io.github.krlvm.powertunnel.sdk.proxy.ProxyAddress
import io.github.krlvm.powertunnel.sdk.types.PowerTunnelPlatform

class Server {
    private val PLUGIN_INFO_ANTICENSORSHIP = PluginInfo(
        "anti_censhorship",
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

    fun run(){
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

        val manager = PluginManager(server)
        manager.addPlugin(AntiCensorshipPlugin, 2)
        manager.addPlugin(DnsResolverPlugin, 1)

        //server.registerProxyListener(PLUGIN_INFO, MProxyListener())
        server.start()
    }
}