package com.anatame.localproxy.server

import io.github.krlvm.powertunnel.PowerTunnel

class PluginManager(val server: PowerTunnel) {
    fun addPlugin(plugin: PluginInterface, priority: Int? = null){
        if(priority != null)
            server.registerProxyListener(
                plugin.getPluginInfo(),
                plugin.getListener(),
                priority
            )
        else
            server.registerProxyListener(
                plugin.getPluginInfo(),
                plugin.getListener(),
            )
    }
}