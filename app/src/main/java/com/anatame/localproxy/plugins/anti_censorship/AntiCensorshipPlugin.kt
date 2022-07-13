package com.anatame.localproxy.plugins.anti_censorship

import com.anatame.localproxy.BuildConfig
import com.anatame.localproxy.server.PluginInterface
import io.github.krlvm.powertunnel.BuildConstants
import io.github.krlvm.powertunnel.sdk.plugin.PluginInfo
import io.github.krlvm.powertunnel.sdk.proxy.ProxyListener

object AntiCensorshipPlugin: PluginInterface {
    override fun getPluginInfo(): PluginInfo {
        return PluginInfo(
            "anti_censorship",
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE,
            "Anti-Censorship-Android",
            "Censorship Fighter",
            "",
            "",
            null,
            BuildConstants.VERSION_CODE,
            null
        )
    }
    override fun getListener(): ProxyListener{
        return AntiCensorshipProxyListener(
            arrayOf<String>(),
            false,
            false,
            false,
            false,
            false,
            false,
            0,
            2,
            false,
            null,
            "",
        )
    }
}