package com.lostar.tv.data

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class StreamHealthTest {
    private fun ch(id: String, name: String) = Channel(id, name, categoryId = "c", url = "u$id")

    @Test
    fun staticOrderPrefersHdAndAvoidsHeavyLinks() {
        val h = StreamHealth(null)
        val ranked = h.rank(
            listOf(ch("4k", "Sky Sport F1 RAW 4K"), ch("sd", "Sky Sport F1 SD"), ch("hd", "Sky Sport F1 HD"), ch("hevc", "Sky Sport F1 HEVC")),
            hevcSupported = true,
        )
        assertEquals(listOf("hd", "hevc", "sd", "4k"), ranked.map { it.id })
        val noHevc = h.rank(listOf(ch("hevc", "X HEVC"), ch("sd", "X SD")), hevcSupported = false)
        assertEquals("sd", noHevc.first().id)
    }

    @Test
    fun recentFailureDemotesAndHistoryPromotes() {
        var now = 1_000_000_000L
        val h = StreamHealth(null) { now }
        val vars = listOf(ch("hd", "X HD"), ch("sd", "X SD"))
        h.recordFailure("hd")
        assertEquals("sd", h.rank(vars, true).first().id)
        now += 60 * 60 * 1000 // an hour later the penalty has faded, but history still counts
        repeat(3) { h.recordSuccess("sd", 1500) }
        assertEquals("sd", h.rank(vars, true).first().id)
        repeat(6) { h.recordSuccess("hd", 1500) }
        assertEquals("hd", h.rank(vars, true).first().id)
    }

    @Test
    fun persists() {
        val f = File.createTempFile("health", ".json").apply { delete() }
        val a = StreamHealth(f)
        a.recordFailure("x")
        a.saveIfNeeded()
        val b = StreamHealth(f)
        assertEquals(1, b.stat("x")!!.failures)
        f.delete()
    }
}
