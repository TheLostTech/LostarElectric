package com.lostar.tv.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChannelDeduperTest {
    private fun key(s: String) = ChannelDeduper.channelKey(s)

    @Test
    fun mergesQualityAndSourceVariants() {
        val names = listOf(
            "Sky Sport F1 HD", "Sky Sport F1 HEVC", "Sky Sport F1 SD", "Sky Sport F1", "Sky Sport F1 HD (Q)",
            "Sky Sport F1 RAW 4K (Richiede 30 Mbps)", "IT: Sky Sport F1 FHD", "|IT| Sky Sport F1 ᴴᴰ",
            "Sky Sport F1 HD +2", "Sky Sport F1 H.265",
        )
        assertEquals(setOf("sky sport f1"), names.map(::key).toSet())
    }

    @Test
    fun keepsDifferentChannelsApart() {
        assertNotEquals(key("Sky Sport F1"), key("Sky Sport MotoGP"))
        assertNotEquals(key("Sky Sport 251"), key("Sky Sport 252"))
        assertNotEquals(key("Sky Cinema Uno"), key("Sky Cinema Uno +24"))
        assertNotEquals(key("Rai 1"), key("Rai 2"))
        assertNotEquals(key("UK: Sky Sports F1"), key("Sky Sports F1"))
        assertEquals(key("Rai 1 HD"), key("ITA - Rai 1"))
    }

    @Test
    fun displayNames() {
        assertEquals("Sky Sport F1", ChannelDeduper.displayName("IT: Sky Sport F1 HEVC (Hermes)"))
        assertEquals("Sky Cinema Uno +24", ChannelDeduper.displayName("Sky Cinema Uno +24 HD"))
        assertEquals("Sky Calcio", ChannelDeduper.categoryName("Sky Calcio (RAW / Power)"))
        assertEquals("Sky Calcio", ChannelDeduper.categoryName("Sky Calcio SAT"))
        assertEquals("Top Italia", ChannelDeduper.categoryName("Top Italia [HI-DEF]"))
        assertEquals(ChannelDeduper.categoryKey("Sky Calcio (DVS)"), ChannelDeduper.categoryKey("Sky Calcio (SkyNet+)"))
    }

    @Test
    fun separators() {
        assertTrue(ChannelDeduper.isSeparator("--F1 SAT-QUALITY--"))
        assertTrue(ChannelDeduper.isSeparator("--- F1 LIVE SkyNet ---"))
        assertTrue(ChannelDeduper.isSeparator("#####"))
        assertFalse(ChannelDeduper.isSeparator("Sky Sport F1"))
    }

    @Test
    fun buildsIndexAcrossCategories() {
        val cats = listOf(
            Category("live:1", "Sky Sport (Hermes)", Section.LIVE),
            Category("live:2", "Sky Sport (SAT)", Section.LIVE),
            Category("live:3", "Top Italia", Section.LIVE),
        )
        fun ch(id: String, name: String, cat: String) = Channel(id, name, categoryId = cat, url = "http://x/$id")
        val catalog = Catalog(
            liveCategories = cats,
            channels = listOf(
                ch("1", "Sky Sport F1 HD", "live:1"),
                ch("2", "--F1 SAT-QUALITY--", "live:2"),
                ch("3", "Sky Sport F1 SD", "live:2"),
                ch("4", "Sky Sport Uno HEVC", "live:2"),
                ch("5", "Sky Sport F1 HEVC", "live:3"),
                ch("6", "Canale 5 HD", "live:3"),
            ),
        )
        val index = catalog.live
        assertEquals(listOf("Sky Sport F1", "Sky Sport Uno", "Canale 5"), index.channels.map { it.name })
        assertEquals(listOf("1", "3", "5"), index.byId.getValue("sky sport f1").variants.map { it.id })
        assertEquals(listOf("Sky Sport", "Top Italia"), index.groups.map { it.name })
        assertEquals(listOf("sky sport f1", "sky sport uno"), index.groups[0].channelIds)
        assertEquals(Genre.SPORT, index.byId.getValue("sky sport f1").genre)
        assertEquals(Genre.ENTERTAINMENT, index.byId.getValue("canale 5").genre)
        assertEquals("sky sport f1", index.find("3")!!.id)
    }
}
