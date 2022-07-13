package com.anatame.localproxy.server

import com.anatame.localproxy.plugins.anti_censorship.AntiCensorshipPlugin
import com.anatame.localproxy.plugins.dns_resolver.DnsResolverPlugin
import io.github.krlvm.powertunnel.sdk.plugin.PluginInfo
import io.github.krlvm.powertunnel.sdk.proxy.ProxyListener

class PluginManager(private val withDefaultPlugins: Boolean = true) {
    private val pluginHolder: ArrayList<PluginPocket> = ArrayList()

    init {
        if(withDefaultPlugins) initDefaultPlugins()
    }

    fun addPlugin(plugin: PluginInterface, priority: Int? = null) {
        pluginHolder.add(PluginPocket(plugin, priority))
    }

    fun getPlugins(): List<PluginPocket> = pluginHolder

    private fun initDefaultPlugins(){
        pluginHolder.add(PluginPocket(AntiCensorshipPlugin, 2))
        pluginHolder.add(PluginPocket(DnsResolverPlugin, 1))
    }
}

interface PluginInterface {
    fun getPluginInfo(): PluginInfo
    fun getListener(): ProxyListener
}

data class PluginPocket(
    val Plugin: PluginInterface,
    val priority: Int? = null
)