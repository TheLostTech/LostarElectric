package com.lostar.tv.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class M3uParserTest {
    private val sample = """
        #EXTM3U url-tvg="http://epg.example/guide.xml"
        #EXTINF:-1 tvg-id="rai1.it" tvg-name="Rai 1 HD" tvg-logo="http://logo/rai1.png" group-title="Italia | Generalisti",Rai 1 HD
        http://srv.example:8080/user/pass/101
        #EXTINF:-1 tvg-id="" tvg-logo="" group-title="Sport, Calcio",Sky Sport, Uno
        http://srv.example:8080/user/pass/102.m3u8
        #EXTINF:-1 tvg-logo="http://img/film.jpg" group-title="Film | Azione",Il Film (2021)
        http://srv.example:8080/movie/user/pass/5001.mkv
        #EXTINF:-1 tvg-logo="http://img/s.jpg" group-title="Serie | Drama",La Serie S01 E02
        http://srv.example:8080/series/user/pass/9002.mp4
        #EXTINF:-1 group-title="Serie | Drama",La Serie S01E01
        http://srv.example:8080/series/user/pass/9001.mp4
        #EXTINF:-1,Senza gruppo
        #EXTVLCOPT:http-user-agent=VLC
        http://other.example/stream
    """.trimIndent()

    @Test
    fun splitsSections() {
        val c = M3uParser.parse(sample.lineSequence())
        assertEquals("http://epg.example/guide.xml", c.epgUrl)
        assertEquals(3, c.channels.size)
        assertEquals(1, c.movies.size)
        assertEquals(1, c.series.size)
        assertEquals(listOf("Italia | Generalisti", "Sport, Calcio", "Senza categoria"), c.liveCategories.map { it.name })
    }

    @Test
    fun readsAttributesAndCommaInsideQuotes() {
        val c = M3uParser.parse(sample.lineSequence())
        val rai = c.channels[0]
        assertEquals("Rai 1 HD", rai.name)
        assertEquals("rai1.it", rai.epgId)
        assertEquals("http://logo/rai1.png", rai.logo)
        val sky = c.channels[1]
        assertEquals("Sky Sport, Uno", sky.name)
        assertNull(sky.logo)
        assertNull(sky.epgId)
        assertEquals("live:Sport, Calcio", sky.categoryId)
    }

    @Test
    fun groupsEpisodesBySeason() {
        val c = M3uParser.parse(sample.lineSequence())
        val s = c.series.single()
        assertEquals("La Serie", s.name)
        val eps = c.m3uEpisodes[s.id]!!
        assertEquals(listOf(1, 2), eps.map { it.episode })
        assertTrue(eps.all { it.season == 1 })
    }

    @Test
    fun classifiesByUrl() {
        assertEquals(Section.LIVE, M3uParser.classify("http://h/u/p/1.ts", false))
        assertEquals(Section.MOVIES, M3uParser.classify("http://h/u/p/1.mp4", false))
        assertEquals(Section.SERIES, M3uParser.classify("http://h/u/p/1.mp4", true))
        assertEquals(Section.SERIES, M3uParser.classify("http://h/series/u/p/1.mkv?x=1", false))
    }
}
