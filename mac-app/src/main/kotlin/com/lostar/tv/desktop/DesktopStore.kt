package com.lostar.tv.desktop

import com.lostar.tv.data.LiveChannel
import com.lostar.tv.data.PlaylistSource
import com.lostar.tv.data.Section
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class WatchEntry(
    val key: String,
    val section: Section,
    val title: String,
    val subtitle: String? = null,
    val image: String? = null,
    val url: String,
    val positionMs: Long,
    val durationMs: Long,
    val updatedAt: Long,
    val seriesId: String? = null,
) {
    val progress: Float get() = if (durationMs <= 0) 0f else (positionMs.toFloat() / durationMs).coerceIn(0f, 1f)
    val isInProgress: Boolean get() = durationMs > 0 && progress in 0.02f..0.95f
}

@Serializable
data class StoreData(
    val source: PlaylistSource? = null,
    val favorites: Set<String> = emptySet(),
    val history: List<WatchEntry> = emptyList(),
    val recentChannels: List<String> = emptyList(),
    val volume: Int = 90,
)

/** Settings, favorites and history saved as one JSON file in ~/Library/Application Support/LostTV. */
class DesktopStore(dir: File, private val scope: CoroutineScope) {
    private val file = File(dir, "settings.json")
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    private val _data = MutableStateFlow(load())
    val data: StateFlow<StoreData> = _data.asStateFlow()

    private fun load(): StoreData =
        runCatching { json.decodeFromString(StoreData.serializer(), file.readText()) }.getOrDefault(StoreData())

    private fun edit(block: (StoreData) -> StoreData) {
        _data.update(block)
        val snapshot = _data.value
        scope.launch(Dispatchers.IO) {
            synchronized(this@DesktopStore) {
                runCatching {
                    val tmp = File(file.parentFile, "settings.tmp")
                    tmp.writeText(json.encodeToString(StoreData.serializer(), snapshot))
                    tmp.renameTo(file)
                }
            }
        }
    }

    fun setSource(source: PlaylistSource?) = edit { it.copy(source = source) }
    fun setVolume(v: Int) = edit { it.copy(volume = v.coerceIn(0, 150)) }

    fun toggleFavorite(key: String) = edit {
        it.copy(favorites = if (key in it.favorites) it.favorites - key else it.favorites + key)
    }

    fun toggleLiveFavorite(c: LiveChannel) = edit {
        val keys = (listOf(c.id) + c.variants.map { v -> v.id }).map(::liveKey).toSet()
        it.copy(favorites = if (it.favorites.any { k -> k in keys }) it.favorites - keys else it.favorites + liveKey(c.id))
    }

    fun saveProgress(entry: WatchEntry) = edit {
        it.copy(history = (listOf(entry) + it.history.filterNot { h -> h.key == entry.key }).take(60))
    }

    fun pushRecentChannel(id: String) = edit {
        it.copy(recentChannels = (listOf(id) + it.recentChannels.filterNot { r -> r == id }).take(20))
    }

    fun clearHistory() = edit { it.copy(history = emptyList(), recentChannels = emptyList()) }

    companion object {
        fun liveKey(id: String) = "live:$id"
        fun movieKey(id: String) = "movie:$id"
        fun seriesKey(id: String) = "series:$id"
        fun episodeKey(id: String) = "episode:$id"
        fun isLiveFavorite(c: LiveChannel, favorites: Set<String>) =
            liveKey(c.id) in favorites || c.variants.any { liveKey(it.id) in favorites }
    }
}
