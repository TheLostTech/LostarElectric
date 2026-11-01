package com.lostar.tv.data

import kotlinx.serialization.Serializable
import java.io.Reader

/** Full programme guide (XMLTV), indexed by EPG channel id, used for "what's on" and for searching titles. */
@Serializable
class Guide(val byChannel: Map<String, List<EpgProgram>>, val loadedAt: Long) {
    fun programmes(epgId: String?): List<EpgProgram> = epgId?.let { byChannel[it.lowercase()] }.orEmpty()
    val size: Int get() = byChannel.values.sumOf { it.size }
}

data class GuideHit(val channel: LiveChannel, val program: EpgProgram) {
    fun onAir(now: Long) = program.startMs <= now && program.endMs > now
}

object Xmltv {
    private val attr = Regex("""([a-zA-Z_]+)\s*=\s*"([^"]*)"""")
    private val titleRe = Regex("""<title[^>]*>(.*?)</title>""", RegexOption.DOT_MATCHES_ALL)
    private val descRe = Regex("""<desc[^>]*>(.*?)</desc>""", RegexOption.DOT_MATCHES_ALL)
    private val entity = Regex("""&(#x?[0-9a-fA-F]+|amp|lt|gt|quot|apos);""")

    /**
     * Streams the file (it can be hundreds of MB) keeping only programmes of [wanted] channels
     * that overlap the [fromMs]–[toMs] window.
     */
    fun parse(reader: Reader, wanted: Set<String>?, fromMs: Long, toMs: Long): Map<String, List<EpgProgram>> {
        val out = HashMap<String, MutableList<EpgProgram>>()
        val buf = StringBuilder()
        val chunk = CharArray(64 * 1024)
        while (true) {
            val n = reader.read(chunk)
            if (n < 0) break
            buf.append(chunk, 0, n)
            var cursor = 0
            while (true) {
                val start = buf.indexOf("<programme", cursor)
                if (start < 0) {
                    cursor = maxOf(cursor, buf.length - 16)
                    break
                }
                val end = buf.indexOf("</programme>", start)
                if (end < 0) {
                    cursor = start
                    break
                }
                handle(buf.substring(start, end), wanted, fromMs, toMs, out)
                cursor = end + "</programme>".length
            }
            if (cursor > 0) buf.delete(0, minOf(cursor, buf.length))
        }
        return out.mapValues { (_, l) -> l.sortedBy { it.startMs }.distinctBy { it.startMs } }
    }

    private fun handle(block: String, wanted: Set<String>?, fromMs: Long, toMs: Long, out: HashMap<String, MutableList<EpgProgram>>) {
        val headEnd = block.indexOf('>')
        if (headEnd < 0) return
        val attrs = attr.findAll(block.substring(0, headEnd)).associate { it.groupValues[1] to it.groupValues[2] }
        val channel = attrs["channel"]?.lowercase() ?: return
        if (wanted != null && channel !in wanted) return
        val start = parseTime(attrs["start"] ?: return) ?: return
        val stop = attrs["stop"]?.let(::parseTime) ?: (start + 30 * 60_000)
        if (stop < fromMs || start > toMs) return
        val title = titleRe.find(block)?.groupValues?.get(1)?.let(::unescape)?.trim().orEmpty()
        if (title.isEmpty()) return
        val desc = descRe.find(block)?.groupValues?.get(1)?.let(::unescape)?.trim()?.takeIf { it.isNotEmpty() }
        out.getOrPut(channel) { mutableListOf() } += EpgProgram(title, desc, start, stop)
    }

    /** "20241101203000 +0100" → epoch ms. */
    fun parseTime(s: String): Long? {
        val t = s.trim()
        if (t.length < 12) return null
        return runCatching {
            val y = t.substring(0, 4).toInt()
            val mo = t.substring(4, 6).toInt()
            val d = t.substring(6, 8).toInt()
            val h = t.substring(8, 10).toInt()
            val mi = t.substring(10, 12).toInt()
            val se = if (t.length >= 14 && t[12].isDigit()) t.substring(12, 14).toInt() else 0
            val tz = t.substring(minOf(t.length, 14)).trim()
            var offsetMin = 0
            if (tz.length >= 5 && (tz[0] == '+' || tz[0] == '-')) {
                offsetMin = tz.substring(1, 3).toInt() * 60 + tz.substring(3, 5).toInt()
                if (tz[0] == '-') offsetMin = -offsetMin
            }
            val days = daysFromCivil(y, mo, d)
            ((days * 86400L + h * 3600L + mi * 60L + se) - offsetMin * 60L) * 1000L
        }.getOrNull()
    }

    // Howard Hinnant's algorithm: works on every API level without java.time.
    private fun daysFromCivil(y0: Int, m: Int, d: Int): Long {
        val y = if (m <= 2) y0 - 1 else y0
        val era = (if (y >= 0) y else y - 399) / 400
        val yoe = y - era * 400
        val doy = (153 * (m + (if (m > 2) -3 else 9)) + 2) / 5 + d - 1
        val doe = yoe * 365 + yoe / 4 - yoe / 100 + doy
        return era * 146097L + doe - 719468
    }

    fun unescape(s: String): String {
        var t = s
        if (t.startsWith("<![CDATA[")) t = t.removePrefix("<![CDATA[").removeSuffix("]]>")
        return entity.replace(t) { m ->
            when (val e = m.groupValues[1]) {
                "amp" -> "&"
                "lt" -> "<"
                "gt" -> ">"
                "quot" -> "\""
                "apos" -> "'"
                else -> {
                    val code = if (e.startsWith("#x") || e.startsWith("#X")) e.substring(2).toIntOrNull(16) else e.substring(1).toIntOrNull()
                    code?.let { String(Character.toChars(it)) } ?: m.value
                }
            }
        }
    }
}
