package com.anatame.localproxy.helpers

import io.github.krlvm.powertunnel.sdk.http.HttpHeaders
import okhttp3.Response

class HeadersHelper(val response: Response?): HttpHeaders {

    val headers = response?.headers?.toHashSet()?: HashSet()

    override fun get(p0: String?): String? {
        return headers.toMap().get(p0)
    }

    override fun set(p0: String, p1: String) {
        headers.add(Pair(p0, p1))
    }

    override fun getInt(p0: String?): Int? {
        return headers.toMap().get(p0)?.toInt()
    }

    override fun setInt(p0: String, p1: Int) {
        headers.add(Pair(p0, p1.toString()))
    }

    override fun getShort(p0: String?): Short? {
        return headers.toMap().get(p0)?.toShort()
    }

    override fun setShort(p0: String, p1: Short) {
        headers.add(Pair(p0, p1.toString()))
    }

    override fun names(): MutableSet<String> {
        return response?.headers?.names()?.toMutableSet() ?: HashSet()
    }

    override fun contains(item: String): Boolean {
        return headers.toMap().containsValue(item)
    }

    override fun remove(p0: String) {
    }

    override fun isEmpty(): Boolean {
        return headers.isEmpty()
    }

    override fun size(): Int {
        return headers.size
    }
}