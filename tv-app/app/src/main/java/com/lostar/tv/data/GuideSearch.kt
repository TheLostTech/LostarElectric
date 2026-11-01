package com.lostar.tv.data

object GuideSearch {
    /** Common short forms people type for teams and channels. */
    private val aliases = mapOf(
        "juve" to "juventus", "inter" to "inter", "milan" to "milan", "roma" to "roma",
        "napoli" to "napoli", "lazio" to "lazio", "atalanta" to "atalanta", "fiore" to "fiorentina",
        "vs" to "", "v" to "", "contro" to "", "-" to "",
    )

    fun terms(query: String): List<String> =
        TitleCleaner.normalize(query).split(' ').filter { it.isNotBlank() }
            .map { aliases[it] ?: it }.filter { it.isNotBlank() }

    fun search(guide: Guide, index: LiveIndex, query: String, now: Long, limit: Int = 60): List<GuideHit> {
        val terms = terms(query)
        if (terms.isEmpty() || terms.all { it.length < 2 }) return emptyList()
        data class Scored(val hit: GuideHit, val score: Int)
        val out = ArrayList<Scored>()
        for ((epgId, programmes) in guide.byChannel) {
            val channel = index.byEpgId[epgId] ?: continue
            for (p in programmes) {
                if (p.endMs <= now) continue
                val title = TitleCleaner.normalize(p.title)
                val inTitle = terms.all { it in title }
                val inAll = inTitle || run {
                    val all = title + " " + TitleCleaner.normalize(p.description.orEmpty())
                    terms.all { it in all }
                }
                if (!inAll) continue
                var score = if (inTitle) 100 else 40
                if (p.startMs <= now) score += 50
                if (title == terms.joinToString(" ")) score += 30
                out += Scored(GuideHit(channel, p), score)
            }
        }
        // One result per programme+time: the same match on several channels of the same merged group is one hit.
        return out.sortedWith(compareByDescending<Scored> { it.hit.onAir(now) }.thenBy { it.hit.program.startMs }.thenByDescending { it.score })
            .distinctBy { it.hit.channel.id + "|" + it.hit.program.startMs }
            .take(limit)
            .map { it.hit }
    }
}
