package com.anatame.localproxy.plugins.dns_resolver

import com.anatame.localproxy.BuildConfig
import com.anatame.localproxy.server.PluginInterface
import io.github.krlvm.powertunnel.BuildConstants
import io.github.krlvm.powertunnel.sdk.plugin.PluginInfo
import io.github.krlvm.powertunnel.sdk.proxy.ProxyListener

object DnsResolverPlugin: PluginInterface {
    override fun getPluginInfo(): PluginInfo {
        return PluginInfo(
            "dns_resolver",
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE,
            "Dns Resolver Android",
            "Uses DOH to resolve dns queries",
            "",
            "",
            null,
            BuildConstants.VERSION_CODE,
            null
        )
    }
    override fun getListener(): ProxyListener{
        return DnsProxyListener()
    }
}