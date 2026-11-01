package com.lostar.tv.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class XtreamParsersTest {
    private val src = PlaylistSource.Xtream("http://h:80", "u", "p")

    @Test
    fun login() {
        val l = XtreamParsers.login(
            """{"user_info":{"auth":1,"status":"Active","exp_date":"1767225600","max_connections":"2","active_cons":0,
               "allowed_output_formats":["m3u8","ts"]},"server_info":{}}"""
        )!!
        assertTrue(l.authenticated)
        assertEquals(listOf("m3u8", "ts"), l.formats)
        assertEquals(1767225600000L, l.account.expiresAtMs)
        assertEquals(2, l.account.maxConnections)
    }

    @Test
    fun liveAndVodLooseTypes() {
        val live = XtreamParsers.liveStreams(
            """[{"num":"3","name":" Rai 1 ","stream_id":101,"stream_icon":"","epg_channel_id":null,"category_id":"7"},
                {"name":"no id"}]""", "http://h:80", src, "m3u8"
        )
        assertEquals(1, live.size)
        assertEquals("Rai 1", live[0].name)
        assertEquals("http://h:80/live/u/p/101.m3u8", live[0].url)
        assertEquals("live:7", live[0].categoryId)
        assertEquals(3, live[0].number)

        val vod = XtreamParsers.vodStreams(
            """[{"name":"Dune (2021)","stream_id":"55","rating":"7,8","container_extension":"mkv","category_id":1,"added":"1700000000"}]""",
            "http://h:80", src
        )
        assertEquals("http://h:80/movie/u/p/55.mkv", vod[0].url)
        assertEquals(7.8, vod[0].rating!!, 0.001)
        assertEquals("2021", vod[0].year)
    }

    @Test
    fun seriesInfoWithObjectOrArrayEpisodes() {
        val obj = XtreamParsers.seriesDetail(
            """{"info":{"plot":"Trama","backdrop_path":["http://b.jpg"]},
               "episodes":{"2":[{"id":"22","episode_num":"1","season":2,"title":"S2E1","container_extension":"mp4","info":[]}],
                           "1":[{"id":"12","episode_num":2,"season":1,"title":"S1E2","info":{"plot":"x"}},
                                {"id":"11","episode_num":1,"season":"1","title":"S1E1"}]}}""",
            "http://h:80", src
        )
        assertEquals(listOf(1, 2), obj.seasons.keys.toList())
        assertEquals(listOf("11", "12"), obj.seasons[1]!!.map { it.id })
        assertEquals("http://b.jpg", obj.backdrop)

        val arr = XtreamParsers.seriesDetail(
            """{"info":[],"episodes":[[{"id":"1","episode_num":1,"season":1}]]}""", "http://h:80", src
        )
        assertEquals(1, arr.seasons[1]!!.size)
    }

    @Test
    fun movieInfoEmptyArray() {
        assertEquals(MovieDetail(), XtreamParsers.movieDetail("""{"info":[],"movie_data":{}}"""))
    }

    @Test
    fun shortEpgDecodesBase64() {
        // "Telegiornale" / "Notizie"
        val epg = XtreamParsers.shortEpg(
            """{"epg_listings":[{"title":"VGVsZWdpb3JuYWxl","description":"Tm90aXppZQ==","start_timestamp":"1700003600","stop_timestamp":"1700007200"},
               {"title":"UHJpbWE=","start_timestamp":"1700000000","stop_timestamp":"1700003600"}]}"""
        )
        assertEquals(listOf("Prima", "Telegiornale"), epg.map { it.title })
        assertEquals("Notizie", epg[1].description)
    }

    @Test
    fun fallbackCategory() {
        val c = XtreamParsers.withFallbackCategories(
            Catalog(channels = listOf(Channel("1", "a", categoryId = "live:99", url = "x")))
        )
        assertEquals("Altro", c.liveCategories.single().name)
    }
}
