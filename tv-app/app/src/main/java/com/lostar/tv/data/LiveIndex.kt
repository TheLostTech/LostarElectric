package com.lostar.tv.data

import java.text.Normalizer

/** One logical TV channel: every duplicate link of the playlist ("HD", "HEVC", "SD", "(Hermes)"…) is a variant. */
data class LiveChannel(
    val id: String,
    val name: String,
    val logo: String?,
    val variants: List<Channel>,
    val genre: Genre,
    val number: Int,
)

/** A merged category: "Sky Calcio (DVS)", "Sky Calcio (Hermes)", "Sky Calcio SAT" → "Sky Calcio". */
data class LiveGroup(
    val id: String,
    val name: String,
    val genre: Genre,
    val channelIds: List<String>,
)

enum class Genre(val label: String) {
    SPORT("Sport"),
    CINEMA("Cinema"),
    ENTERTAINMENT("Intrattenimento"),
    KIDS("Bambini"),
    NEWS("Notizie"),
    DOCS("Documentari"),
    MUSIC("Musica"),
    OTHER("Altro"),
}

class LiveIndex(val channels: List<LiveChannel>, val groups: List<LiveGroup>) {
    val byId: Map<String, LiveChannel> = channels.associateBy { it.id }
    private val byVariant: Map<String, LiveChannel> = buildMap { channels.forEach { c -> c.variants.forEach { put(it.id, c) } } }

    /** Resolves both new logical ids and raw stream ids saved by older versions. */
    fun find(id: String): LiveChannel? = byId[id] ?: byVariant[id]

    fun channelsOf(group: LiveGroup): List<LiveChannel> = group.channelIds.mapNotNull { byId[it] }

    val genres: List<Genre> by lazy { Genre.entries.filter { g -> g != Genre.OTHER && channels.any { it.genre == g } } }

    val variantCount: Int get() = channels.sumOf { it.variants.size }
}

object ChannelDeduper {

    /** Words that describe the link, not the channel. Removed before comparing names. */
    private val noiseWords = setOf(
        "hd", "fhd", "uhd", "sd", "hq", "lq", "4k", "8k", "hdr", "hevc", "h265", "h264", "x265", "avc",
        "1080", "1080p", "1080i", "720", "720p", "576p", "480p", "50fps", "60fps", "25fps", "fps",
        "raw", "hidef", "hi", "def", "backup", "bk", "alt", "multi", "multiaudio", "vip",
        "dvs", "hermes", "sat", "skynet", "stellina", "power", "low", "high", "quality", "orig", "original",
        "test", "new", "nuovo", "server", "srv", "link", "ok", "live24",
    )

    /** Country prefixes like "IT:", "|IT|", "[ITA] ", "UK -". Italian (or none) is the default and is not kept in the key. */
    private val bracketCountry = Regex("""^\s*[|\[(]\s*([A-Za-z]{2,3})\s*[|\])]\s*[:\-]?\s*""")
    private val plainCountry = Regex("""^\s*([A-Z]{2,3})\s*[:|\-]\s*""")
    private val countryCodes = setOf(
        "it", "ita", "itl", "uk", "gb", "us", "usa", "de", "ger", "fr", "fra", "es", "esp", "pt", "nl", "be", "ch",
        "at", "al", "ro", "pl", "tr", "ar", "ru", "gr", "se", "no", "dk", "fi", "ie", "ca", "br", "mx", "in", "pk",
        "hr", "rs", "ba", "mk", "si", "hu", "cz", "sk", "bg", "ua", "ma", "tn", "dz", "eg", "sa", "ae", "ir", "il",
        "cn", "jp", "kr", "ph", "th", "vn", "id", "my", "au", "nz", "za", "ng", "latam", "lat", "ex", "exyu",
    )

    /** Returns the lowercase country code (if any) and the name without it. */
    private fun splitCountry(s: String): Pair<String?, String> {
        for (r in listOf(bracketCountry, plainCountry)) {
            val m = r.find(s) ?: continue
            val code = m.groupValues[1].lowercase()
            if (code in countryCodes) return code to s.substring(m.range.last + 1)
        }
        return null to s
    }
    private val bracketed = Regex("""[(\[{][^)\]}]*[)\]}]""")
    private val timeshift = Regex("""\+\s*(1|24)\b""")
    private val backupNumber = Regex("""\+\s*\d+\b""")
    private val nonWord = Regex("""[^\p{L}\p{N}+ ]+""")
    private val spaces = Regex("""\s+""")
    private val defaultCountries = setOf("it", "ita", "itl")

    /** Separator rows like "----- SPORT -----" or "--F1 SAT-QUALITY--" are not real channels. */
    fun isSeparator(name: String): Boolean {
        val t = name.trim()
        if (t.isEmpty()) return true
        val starts = Regex("""^[\-=#*~_•●★|<>.]{2,}""").containsMatchIn(t)
        val ends = Regex("""[\-=#*~_•●★|<>.]{2,}$""").containsMatchIn(t)
        return (starts && ends) || t.none { it.isLetterOrDigit() }
    }

    private fun fold(s: String): String =
        Normalizer.normalize(s, Normalizer.Form.NFKD).replace(Regex("""\p{Mn}+"""), "")

    /** Grouping key: "IT: Sky Sport F1 HEVC (Hermes)" and "Sky Sport F1 HD" both give "sky sport f1". */
    fun channelKey(rawName: String): String {
        val (code, rest) = splitCountry(fold(rawName))
        val country = if (code != null && code !in defaultCountries) "$code|" else ""
        var s = rest.lowercase()
        s = bracketed.replace(s, " ")
        val shift = timeshift.find(s)?.groupValues?.get(1)
        s = timeshift.replace(s, " ")
        s = backupNumber.replace(s, " ")
        s = s.replace("h.265", " ").replace("h.264", " ").replace("hi-def", " ").replace("full hd", " ").replace("ultra hd", " ")
        s = nonWord.replace(s, " ")
        val words = s.split(' ').filter { it.isNotBlank() && it !in noiseWords }
        val base = words.joinToString(" ").ifEmpty { s.trim() }
        return country + base + (shift?.let { " +$it" } ?: "")
    }

    /** Human name without quality/source tags, keeping the original capitalisation. */
    fun displayName(rawName: String): String {
        var s = splitCountry(fold(rawName)).second
        s = bracketed.replace(s, " ")
        val shift = timeshift.find(s)?.value?.replace(" ", "")
        s = timeshift.replace(s, " ")
        s = backupNumber.replace(s, " ")
        s = s.replace(Regex("""(?i)h\.26[45]|hi-def|full hd|ultra hd"""), " ")
        s = s.replace(Regex("""[|•●★]"""), " ")
        val words = s.split(spaces).filter { w ->
            val k = w.lowercase().trim('-', '_', '.', ':', ',')
            k.isNotEmpty() && k !in noiseWords
        }
        val name = words.joinToString(" ").trim(' ', '-', ':', '.', '_').ifEmpty { rawName.trim() }
        return if (shift != null) "$name $shift" else name
    }

    fun categoryKey(rawName: String): String = channelKey(rawName)

    fun categoryName(rawName: String): String = displayName(rawName)

    private val genreKeywords: List<Pair<Genre, List<String>>> = listOf(
        Genre.KIDS to listOf("kids", "bambin", "cartoon", "cartoni", "junior", "disney", "nick", "boing", "yoyo", "gulp", "frisbee", "k2", "super!", "baby", "teen"),
        Genre.SPORT to listOf("sport", "calcio", "football", "dazn", "eurosport", "f1", "motogp", "moto gp", "tennis", "golf", "nba", "serie a", "champions", "bein", "sportitalia", "arena", "uefa", "ppv", "inter tv", "milan tv", "juventus", "racing", "boxe", "wwe"),
        Genre.NEWS to listOf("news", "notizie", "tg24", "tg ", "rainews", "rai news", "euronews", "cnn", "bbc world", "class cnbc", "meteo"),
        Genre.DOCS to listOf("documentar", "discovery", "history", "nat geo", "national geographic", "geo ", "focus", "rai storia", "animal", "crime", "investigation"),
        Genre.MUSIC to listOf("music", "musica", "mtv", "radio", "rtl 102", "deejay", "vh1", "kiss kiss", "m2o", "rds"),
        Genre.CINEMA to listOf("cinema", "film", "movie", "cine", "premium", "action", "comedy", "thriller", "romance"),
        Genre.ENTERTAINMENT to listOf("intrattenimento", "entertainment", "serie", "show", "atlantic", "real time", "comedy central", "fox", "uno", "cielo", "tv8", "nove", "la5", "italia 1", "canale 5", "rete 4", "rai", "mediaset", "top italia", "generalist"),
    )

    fun genreOf(vararg texts: String): Genre {
        for (t in texts) {
            val s = " " + fold(t).lowercase() + " "
            for ((g, words) in genreKeywords) if (words.any { it in s }) return g
        }
        return Genre.OTHER
    }

    fun build(catalog: Catalog): LiveIndex {
        val categoryById = catalog.liveCategories.associateBy { it.id }
        // Category order follows the playlist; merged categories keep the first position seen.
        val groupOrder = LinkedHashMap<String, Pair<String, MutableList<String>>>()
        val variantsByKey = LinkedHashMap<String, MutableList<Channel>>()

        val ordered = catalog.liveCategories.flatMap { catalog.channelsByCategory[it.id].orEmpty() } +
            catalog.channels.filter { it.categoryId !in categoryById }

        for (ch in ordered) {
            if (isSeparator(ch.name)) continue
            val key = channelKey(ch.name)
            if (key.isBlank()) continue
            variantsByKey.getOrPut(key) { mutableListOf() } += ch
            val rawCat = categoryById[ch.categoryId]?.name ?: "Altro"
            if (isSeparator(rawCat)) continue
            val catKey = categoryKey(rawCat).ifBlank { "altro" }
            val entry = groupOrder.getOrPut(catKey) { categoryName(rawCat) to mutableListOf() }
            if (key !in entry.second) entry.second += key
        }

        val catNameOfVariant = { ch: Channel -> categoryById[ch.categoryId]?.name.orEmpty() }
        var n = 0
        val channels = variantsByKey.map { (key, variants) ->
            val first = variants.first()
            LiveChannel(
                id = key,
                name = displayName(first.name),
                logo = variants.firstNotNullOfOrNull { it.logo },
                variants = variants,
                genre = genreOf(displayName(first.name), catNameOfVariant(first)),
                number = ++n,
            )
        }
        val groups = groupOrder.map { (key, v) ->
            LiveGroup(id = "g:$key", name = v.first, genre = genreOf(v.first), channelIds = v.second)
        }
        return LiveIndex(channels, groups)
    }

    // ----- link quality ------------------------------------------------------------------------

    enum class Quality(val label: String) { UHD("4K"), FHD("Full HD"), HD("HD"), HEVC("HEVC"), STANDARD("Standard"), SD("SD") }

    fun qualityOf(rawName: String): Quality {
        val s = " " + fold(rawName).lowercase().replace(Regex("""[^\p{L}\p{N}]+"""), " ") + " "
        return when {
            " 4k " in s || " uhd " in s || " 2160p " in s || " raw " in s -> Quality.UHD
            " hevc " in s || " h265 " in s || " h 265 " in s || " x265 " in s -> Quality.HEVC
            " fhd " in s || " 1080p " in s || " 1080 " in s || " full hd " in s -> Quality.FHD
            " hd " in s || " 720p " in s || " hi def " in s -> Quality.HD
            " sd " in s || " 576p " in s || " lq " in s -> Quality.SD
            else -> Quality.STANDARD
        }
    }

    /**
     * Prior preference before any history: Full HD / HD first, HEVC when the TV decodes it,
     * 4K/RAW last because those links need a lot of bandwidth and freeze more often.
     */
    fun staticScore(ch: Channel, hevcSupported: Boolean): Double {
        var score = when (qualityOf(ch.name)) {
            Quality.FHD -> 6.0
            Quality.HD -> 5.5
            Quality.HEVC -> if (hevcSupported) 5.0 else -4.0
            Quality.STANDARD -> 4.0
            Quality.SD -> 3.0
            Quality.UHD -> 1.5
        }
        val lower = ch.name.lowercase()
        if ("mbps" in lower || "richiede" in lower) score -= 1.5
        if (backupNumber.containsMatchIn(lower) && !timeshift.containsMatchIn(lower)) score -= 0.5
        if ("backup" in lower || Regex("""\bbk\b""").containsMatchIn(lower)) score -= 0.5
        if (!ch.epgId.isNullOrBlank()) score += 0.5
        if (!ch.logo.isNullOrBlank()) score += 0.25
        return score
    }
}
