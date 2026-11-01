package com.lostar.tv.data

import java.text.Normalizer

/** Turns provider titles like "IT: Matrix (1999) [4K HDR] SUB-ITA" into a search query + year. */
object TitleCleaner {
    private val prefix = Regex("""^\s*(?:[|\[(]\s*[A-Za-z0-9+]{2,5}\s*[|\])]\s*[:\-]?|[A-Z0-9+]{2,5}\s*[:|]|[A-Z]{2,3}\s+-)\s*""")
    private val yearParen = Regex("""[(\[]\s*((?:19|20)\d{2})\s*[)\]]""")
    private val yearTail = Regex("""[\s\-–:]+((?:19|20)\d{2})\s*$""")
    private val bracketed = Regex("""[(\[{][^)\]}]*[)\]}]""")
    private val noise = setOf(
        "4k", "uhd", "fhd", "hd", "sd", "hq", "hevc", "h265", "h264", "x264", "x265", "1080p", "720p", "2160p", "480p",
        "hdr", "hdr10", "dv", "dolby", "vision", "atmos", "dts", "ac3", "aac", "5.1", "7.1", "ita", "eng", "sub", "subita",
        "sub-ita", "multi", "multisub", "dual", "audio", "webdl", "web-dl", "webrip", "bluray", "bdrip", "dvdrip", "remux",
        "extended", "director's", "cut", "uncut", "imax", "3d", "vost", "vostfr", "cam", "ts",
    )

    data class Query(val text: String, val year: String?)

    fun forSearch(raw: String): Query {
        var s = raw.trim()
        repeat(2) { s = prefix.replace(s, "") }
        val year = yearParen.find(s)?.groupValues?.get(1) ?: yearTail.find(s)?.groupValues?.get(1)
        s = yearParen.replace(s, " ")
        if (year != null) s = yearTail.replace(s, " ")
        s = bracketed.replace(s, " ")
        s = s.replace(Regex("""[|_]"""), " ")
        val words = s.split(Regex("""\s+""")).filter { w ->
            val k = w.lowercase().trim('-', '.', ',', ':', '!')
            k.isNotEmpty() && k !in noise
        }
        val text = words.joinToString(" ").trim(' ', '-', ':', '.', '–').ifEmpty { raw.trim() }
        return Query(text, year)
    }

    fun normalize(s: String): String =
        Normalizer.normalize(s.lowercase(), Normalizer.Form.NFD).replace(Regex("""\p{Mn}+"""), "")
            .replace(Regex("""[^\p{L}\p{N}]+"""), " ").trim()
}
