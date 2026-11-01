package com.lostar.tv.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * Remembers how each link behaved (starts, failures, freezes, start-up time) so the app can pick
 * the most stable variant of a channel first and avoid the ones that just broke.
 */
class StreamHealth(private val file: File?, private val clock: () -> Long = System::currentTimeMillis) {

    @Serializable
    data class Stat(
        val attempts: Int = 0,
        val successes: Int = 0,
        val failures: Int = 0,
        val lastFailureAt: Long = 0,
        val lastSuccessAt: Long = 0,
        val avgStartMs: Long = 0,
    )

    private val json = Json { ignoreUnknownKeys = true }
    private val serializer = MapSerializer(String.serializer(), Stat.serializer())
    private val stats = ConcurrentHashMap<String, Stat>()
    @Volatile private var dirty = false

    init {
        val f = file
        if (f != null && f.exists()) {
            runCatching { stats.putAll(json.decodeFromString(serializer, f.readText())) }
        }
    }

    fun stat(id: String): Stat? = stats[id]

    fun recordSuccess(id: String, startMs: Long) {
        stats.compute(id) { _, s ->
            val old = s ?: Stat()
            val avg = if (old.successes == 0) startMs else (old.avgStartMs * 3 + startMs) / 4
            old.copy(attempts = old.attempts + 1, successes = old.successes + 1, lastSuccessAt = clock(), avgStartMs = avg)
        }
        dirty = true
    }

    fun recordFailure(id: String) {
        stats.compute(id) { _, s ->
            val old = s ?: Stat()
            old.copy(attempts = old.attempts + 1, failures = old.failures + 1, lastFailureAt = clock())
        }
        dirty = true
    }

    /** History-based adjustment added to [ChannelDeduper.staticScore]. */
    fun score(id: String): Double {
        val s = stats[id] ?: return 0.0
        val now = clock()
        var score = 0.0
        if (s.attempts > 0) {
            // Laplace-smoothed reliability in [-1, 1], weighted strongly.
            val reliability = (s.successes - s.failures).toDouble() / (s.attempts + 2)
            score += reliability * 6
        }
        if (s.lastFailureAt > 0) {
            val minutes = (now - s.lastFailureAt) / 60_000.0
            if (minutes < 15) score -= 8 * (1 - minutes / 15)
        }
        if (s.successes > 0 && s.avgStartMs > 0) score -= (s.avgStartMs / 1000.0 - 2).coerceIn(0.0, 6.0) * 0.3
        return score
    }

    /** Variants sorted from most to least promising. */
    fun rank(variants: List<Channel>, hevcSupported: Boolean): List<Channel> =
        variants.withIndex()
            .sortedWith(
                compareByDescending<IndexedValue<Channel>> { ChannelDeduper.staticScore(it.value, hevcSupported) + score(it.value.id) }
                    .thenBy { it.index }
            )
            .map { it.value }

    fun saveIfNeeded() {
        val f = file ?: return
        if (!dirty) return
        dirty = false
        runCatching {
            // Keep the file small: drop entries untouched for 60 days.
            val cutoff = clock() - 60L * 24 * 60 * 60 * 1000
            val keep = stats.filterValues { maxOf(it.lastFailureAt, it.lastSuccessAt) > cutoff }
            val tmp = File(f.parentFile, f.name + ".tmp")
            tmp.writeText(json.encodeToString(serializer, keep))
            tmp.renameTo(f)
        }
    }
}
