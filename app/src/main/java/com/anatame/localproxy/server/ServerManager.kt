package com.anatame.localproxy.server

import android.util.Log
import io.github.krlvm.powertunnel.PowerTunnel
import io.github.krlvm.powertunnel.sdk.proxy.ProxyAddress
import io.github.krlvm.powertunnel.sdk.types.PowerTunnelPlatform

private const val TAG = "ServerStatus"

class ServerManager private constructor() {

    private var server: PowerTunnel? = null

    class BuildServer{
        private val serverManager = ServerManager()
        fun createServer(
            host: String = "127.0.0.1",
            port: Int = 9999,
            pluginManager: PluginManager = PluginManager()
        ) = apply {
            serverManager.createServer(host, port, pluginManager)
        }

        fun build(): ServerManager{
            return serverManager
        }
    }

    private fun createServer(
        host: String,
        port: Int,
        pluginManager: PluginManager
    ) {
        initializeServer(ProxyAddress(host, port))
        initializePlugins(pluginManager)
    }

    fun startServer(){
        server?.let {
            if(!it.isRunning)
                it.start()
        }
    }

    fun stopServer(){
        server?.let {
            if(it.isRunning)
                it.stop()
        }
    }

    private fun initializeServer(address: ProxyAddress): PowerTunnel{
        Log.d(TAG, "Initializing server at " + address.host + ": " + address.port)
        return server ?: PowerTunnel(
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
        ).also {server = it}
    }

    private fun initializePlugins(pluginManager: PluginManager){
        pluginManager.getPlugins().forEach {
            if(it.priority != null)
                server?.registerProxyListener(
                    it.Plugin.getPluginInfo(),
                    it.Plugin.getListener(),
                    it.priority
                )
            else
                server?.registerProxyListener(
                    it.Plugin.getPluginInfo(),
                    it.Plugin.getListener()
                )
        }
    }
}