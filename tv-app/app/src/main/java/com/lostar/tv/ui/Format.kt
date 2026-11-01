package com.lostar.tv.ui

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Format {
    private val time = SimpleDateFormat("HH:mm", Locale.ITALY)
    private val date = SimpleDateFormat("d MMMM yyyy", Locale.ITALY)

    fun time(ms: Long): String = synchronized(time) { time.format(Date(ms)) }
    fun date(ms: Long): String = synchronized(date) { date.format(Date(ms)) }

    fun duration(secs: Int?): String? {
        if (secs == null || secs <= 0) return null
        val h = secs / 3600
        val m = (secs % 3600) / 60
        return if (h > 0) "$h h ${m.toString().padStart(2, '0')} min" else "$m min"
    }

    fun clock(ms: Long): String {
        val total = (ms / 1000).coerceAtLeast(0)
        val h = total / 3600
        val m = (total % 3600) / 60
        val s = total % 60
        return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
    }

    fun rating(r: Double?): String? = r?.takeIf { it > 0 }?.let { "★ " + String.format(Locale.ITALY, "%.1f", if (it > 10) it / 10 else it) }

    /** Removes provider prefixes like "IT: " or "|IT| " that clutter titles. */
    fun cleanTitle(name: String): String =
        name.replace(Regex("""^\s*(\|[A-Z]{2,4}\||[A-Z]{2,4}\s*[:|\-])\s*"""), "").trim().ifEmpty { name }
}
