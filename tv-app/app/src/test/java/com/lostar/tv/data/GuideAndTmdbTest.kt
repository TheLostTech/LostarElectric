package com.lostar.tv.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GuideAndTmdbTest {

    @Test
    fun cleansTitlesForSearch() {
        assertEquals(TitleCleaner.Query("Matrix", "1999"), TitleCleaner.forSearch("IT: Matrix (1999) [4K HDR] SUB-ITA"))
        assertEquals(TitleCleaner.Query("Il Gladiatore", "2000"), TitleCleaner.forSearch("Il Gladiatore - 2000"))
        assertEquals(TitleCleaner.Query("Dune Parte Due", null), TitleCleaner.forSearch("|4K| Dune Parte Due HEVC"))
        assertEquals(TitleCleaner.Query("Breaking Bad", null), TitleCleaner.forSearch("Breaking Bad"))
    }

    @Test
    fun parsesTmdbSearchPreferringExactTitle() {
        val body = """{"results":[
            {"id":1,"title":"Matrix Resurrections","poster_path":"/r.jpg","vote_average":5.6,"vote_count":5000,"release_date":"2021-12-16"},
            {"id":603,"title":"Matrix","original_title":"The Matrix","poster_path":"/m.jpg","backdrop_path":"/b.jpg","vote_average":8.2,"vote_count":25000,"overview":"Neo…","release_date":"1999-03-30"}]}"""
        val info = TmdbClient.parseSearch(body, "Matrix")!!
        assertEquals(603, info.id)
        assertEquals("https://image.tmdb.org/t/p/w342/m.jpg", info.poster)
        assertEquals("1999", info.year)
        assertEquals("8,2", info.ratingLabel)
        assertNull(TmdbClient.parseSearch("""{"results":[]}""", "x"))
    }

    @Test
    fun parsesXmltvTimes() {
        assertEquals(1730489400000L, Xmltv.parseTime("20241101203000 +0100"))
        assertEquals(1730493000000L, Xmltv.parseTime("20241101203000 +0000"))
        assertEquals("Tom & Jerry ’", Xmltv.unescape("Tom &amp; Jerry &#8217;"))
    }

    private val now = Xmltv.parseTime("20241101200000 +0100")!!

    private val xml = """<?xml version="1.0" encoding="UTF-8"?>
        <tv><channel id="SkyCinemaUno.it"><display-name>Sky Cinema Uno</display-name></channel>
        <programme start="20241101190000 +0100" stop="20241101213000 +0100" channel="SkyCinemaUno.it"><title lang="it">Matrix</title><desc lang="it">Neo scopre la verità.</desc></programme>
        <programme start="20241101213000 +0100" stop="20241101233000 +0100" channel="SkyCinemaUno.it"><title>Matrix Reloaded</title></programme>
        <programme start="20241101204500 +0100" stop="20241101224500 +0100" channel="SkySportCalcio.it"><title>Serie A: Juventus - Inter</title><desc>Diretta del big match</desc></programme>
        <programme start="20241101180000 +0100" stop="20241101190000 +0100" channel="SkyCinemaUno.it"><title>Passato</title></programme>
        <programme start="20241101200000 +0100" stop="20241101210000 +0100" channel="Altro.de"><title>Matrix</title></programme>
        </tv>"""

    private fun index(): LiveIndex {
        val cats = listOf(Category("live:1", "Sky Cinema", Section.LIVE), Category("live:2", "Sky Sport", Section.LIVE))
        return Catalog(
            liveCategories = cats,
            channels = listOf(
                Channel("1", "Sky Cinema Uno HD", categoryId = "live:1", url = "u1", epgId = "SkyCinemaUno.it"),
                Channel("2", "Sky Cinema Uno SD", categoryId = "live:1", url = "u2", epgId = "SkyCinemaUno.it"),
                Channel("3", "Sky Sport Calcio", categoryId = "live:2", url = "u3", epgId = "SkySportCalcio.it"),
            ),
        ).live
    }

    @Test
    fun parsesOnlyWantedChannelsInWindow() {
        val map = Xmltv.parse(xml.reader(), setOf("skycinemauno.it", "skysportcalcio.it"), now - 30 * 60_000, now + 6 * 3600_000)
        assertEquals(listOf("Matrix", "Matrix Reloaded"), map.getValue("skycinemauno.it").map { it.title })
        assertEquals(1, map.getValue("skysportcalcio.it").size)
        assertTrue("altro.de" !in map)
    }

    @Test
    fun findsFilmAndMatchAcrossChannels() {
        val guide = Guide(Xmltv.parse(xml.reader(), null, now - 3600_000, now + 6 * 3600_000), now)
        val idx = index()
        val matrix = GuideSearch.search(guide, idx, "matrix", now)
        assertEquals("Sky Cinema Uno", matrix.first().channel.name)
        assertTrue(matrix.first().onAir(now))
        assertEquals(listOf("Matrix", "Matrix Reloaded"), matrix.map { it.program.title })

        for (q in listOf("juventus inter", "juve inter", "Inter Juventus", "juve-inter")) {
            val hits = GuideSearch.search(guide, idx, q, now)
            assertEquals(q, "Sky Sport Calcio", hits.single().channel.name)
        }
        assertTrue(GuideSearch.search(guide, idx, "big match", now).isNotEmpty())
    }
}
