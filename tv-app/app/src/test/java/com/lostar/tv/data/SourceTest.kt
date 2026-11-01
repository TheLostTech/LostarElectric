package com.lostar.tv.data

import org.junit.Assert.assertEquals
import org.junit.Test

class SourceTest {
    @Test
    fun convertsXtreamGetPhpLinks() {
        val s = PlaylistSource.fromM3uUrl("http://line.example.com:8080/get.php?username=mario&password=p%40ss&type=m3u_plus&output=ts")
        assertEquals(PlaylistSource.Xtream("http://line.example.com:8080", "mario", "p@ss"), s)
    }

    @Test
    fun keepsPlainM3u() {
        assertEquals(PlaylistSource.M3u("https://example.com/list.m3u"), PlaylistSource.fromM3uUrl(" https://example.com/list.m3u "))
        assertEquals(PlaylistSource.M3u("http://example.com/list.m3u"), PlaylistSource.fromM3uUrl("example.com/list.m3u"))
    }

    @Test
    fun normalizesServer() {
        assertEquals("http://host:8080", PlaylistSource.normalizeServer("host:8080/"))
        assertEquals("https://host", PlaylistSource.normalizeServer("https://host/player_api.php?username=a"))
        assertEquals("http://host/sub", PlaylistSource.normalizeServer("http://host/sub/"))
    }
}
