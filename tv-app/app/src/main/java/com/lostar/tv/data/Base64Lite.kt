package com.lostar.tv.data

/** Minimal Base64 decoder (java.util.Base64 needs API 26, android.util.Base64 breaks JVM tests). */
internal object Base64Lite {
    private val table = IntArray(128) { -1 }.also { t ->
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".forEachIndexed { i, c -> t[c.code] = i }
        t['-'.code] = 62
        t['_'.code] = 63
    }

    fun decodeToStringOrNull(input: String?): String? {
        if (input.isNullOrEmpty()) return null
        val out = java.io.ByteArrayOutputStream(input.length * 3 / 4)
        var buffer = 0
        var bits = 0
        for (c in input) {
            if (c == '=') break
            if (c.isWhitespace()) continue
            val v = if (c.code < 128) table[c.code] else -1
            if (v < 0) return null
            buffer = (buffer shl 6) or v
            bits += 6
            if (bits >= 8) {
                bits -= 8
                out.write((buffer shr bits) and 0xFF)
            }
        }
        return out.toByteArray().toString(Charsets.UTF_8)
    }
}
