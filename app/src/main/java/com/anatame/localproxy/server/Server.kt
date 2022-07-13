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
    fun run(){
      val serverClient = ServerManager.BuildServer()
          .createServer()
          .build()

        serverClient.startServer()
    }
}