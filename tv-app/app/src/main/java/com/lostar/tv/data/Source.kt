package com.lostar.tv.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Where the paid playlist comes from. */
@Serializable
sealed interface PlaylistSource {
    val label: String

    @Serializable
    @SerialName("xtream")
    data class Xtream(val server: String, val username: String, val password: String) : PlaylistSource {
        override val label: String get() = "Xtream Codes · ${server.removePrefix("http://").removePrefix("https://")}"
    }

    @Serializable
    @SerialName("m3u")
    data class M3u(val url: String) : PlaylistSource {
        override val label: String get() = "Lista M3U"
    }

    companion object {
        /** Adds a scheme when missing and strips trailing slashes / known endpoints. */
        fun normalizeServer(raw: String): String {
            var s = raw.trim()
            if (s.isEmpty()) return s
            if (!s.startsWith("http://", ignoreCase = true) && !s.startsWith("https://", ignoreCase = true)) {
                s = "http://$s"
            }
            val schemeEnd = s.indexOf("://") + 3
            val pathStart = s.indexOf('/', schemeEnd)
            val hostPart = if (pathStart == -1) s else s.substring(0, pathStart)
            var path = if (pathStart == -1) "" else s.substring(pathStart)
            path = path.substringBefore('?')
            for (endpoint in listOf("/player_api.php", "/get.php", "/xmltv.php", "/panel_api.php")) {
                if (path.endsWith(endpoint, ignoreCase = true)) path = path.dropLast(endpoint.length)
            }
            return (hostPart + path).trimEnd('/')
        }

        /**
         * Most paid lists are Xtream panels exposed as `.../get.php?username=X&password=Y&type=m3u_plus`.
         * Using the Xtream API instead gives real categories, posters, plots and EPG.
         */
        fun fromM3uUrl(raw: String): PlaylistSource {
            val url = raw.trim()
            val query = url.substringAfter('?', "")
            val base = url.substringBefore('?')
            if (base.endsWith("/get.php", ignoreCase = true) && query.isNotEmpty()) {
                val params = query.split('&').mapNotNull {
                    val k = it.substringBefore('=', "")
                    if (k.isEmpty()) null else k.lowercase() to decodeUrlComponent(it.substringAfter('=', ""))
                }.toMap()
                val user = params["username"]
                val pass = params["password"]
                if (!user.isNullOrBlank() && !pass.isNullOrBlank()) {
                    return Xtream(normalizeServer(base), user, pass)
                }
            }
            val withScheme = if (url.contains("://")) url else "http://$url"
            return M3u(withScheme)
        }

        private fun decodeUrlComponent(s: String): String = try {
            java.net.URLDecoder.decode(s, "UTF-8")
        } catch (_: Exception) {
            s
        }
    }
}
