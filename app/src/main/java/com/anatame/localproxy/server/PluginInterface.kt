package com.anatame.localproxy.server

import io.github.krlvm.powertunnel.sdk.plugin.PluginInfo
import io.github.krlvm.powertunnel.sdk.proxy.ProxyListener

interface PluginInterface {
    fun getPluginInfo(): PluginInfo
    fun getListener(): ProxyListener
}