package com.lostar.tv.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

private val Context.userDataStore by preferencesDataStore(name = "user")

/** A title the user started watching; enough to resume it straight from the home screen. */
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

class UserStore(context: Context) {
    private val store = context.applicationContext.userDataStore
    private val json = Json { ignoreUnknownKeys = true }

    private val sourceKey = stringPreferencesKey("source")
    private val favoritesKey = stringSetPreferencesKey("favorites")
    private val historyKey = stringPreferencesKey("history")
    private val recentChannelsKey = stringPreferencesKey("recent_channels")

    private val historySerializer = ListSerializer(WatchEntry.serializer())
    private val stringList = ListSerializer(String.serializer())

    /** `null` until DataStore answered; then [SourceState.None] or [SourceState.Some]. */
    val source: Flow<SourceState> = store.data.map { p ->
        val raw = p[sourceKey] ?: return@map SourceState.None
        runCatching { json.decodeFromString(PlaylistSource.serializer(), raw) }
            .map { SourceState.Some(it) }
            .getOrDefault(SourceState.None)
    }

    val favorites: Flow<Set<String>> = store.data.map { it[favoritesKey] ?: emptySet() }

    val history: Flow<List<WatchEntry>> = store.data.map { p -> decodeHistory(p) }

    val recentChannels: Flow<List<String>> = store.data.map { p ->
        p[recentChannelsKey]?.let { runCatching { json.decodeFromString(stringList, it) }.getOrNull() } ?: emptyList()
    }

    suspend fun setSource(source: PlaylistSource?) {
        store.edit {
            if (source == null) it.remove(sourceKey) else it[sourceKey] = json.encodeToString(PlaylistSource.serializer(), source)
        }
    }

    suspend fun toggleFavorite(key: String) {
        store.edit {
            val current = it[favoritesKey] ?: emptySet()
            it[favoritesKey] = if (key in current) current - key else current + key
        }
    }

    /** A merged channel is a favorite if its logical id or any of its links was starred. */
    suspend fun toggleLiveFavorite(channel: LiveChannel) {
        val keys = (listOf(channel.id) + channel.variants.map { it.id }).map { liveKey(it) }.toSet()
        store.edit {
            val current = it[favoritesKey] ?: emptySet()
            it[favoritesKey] = if (current.any { k -> k in keys }) current - keys else current + liveKey(channel.id)
        }
    }

    suspend fun saveProgress(entry: WatchEntry) {
        store.edit { p ->
            val list = decodeHistory(p).filterNot { it.key == entry.key }
            p[historyKey] = json.encodeToString(historySerializer, (listOf(entry) + list).take(MAX_HISTORY))
        }
    }

    suspend fun removeFromHistory(key: String) {
        store.edit { p ->
            p[historyKey] = json.encodeToString(historySerializer, decodeHistory(p).filterNot { it.key == key })
        }
    }

    suspend fun pushRecentChannel(id: String) {
        store.edit { p ->
            val list = p[recentChannelsKey]?.let { runCatching { json.decodeFromString(stringList, it) }.getOrNull() } ?: emptyList()
            p[recentChannelsKey] = json.encodeToString(stringList, (listOf(id) + list.filterNot { it == id }).take(MAX_RECENT))
        }
    }

    suspend fun clearHistory() {
        store.edit {
            it.remove(historyKey)
            it.remove(recentChannelsKey)
        }
    }

    private fun decodeHistory(p: Preferences): List<WatchEntry> =
        p[historyKey]?.let { runCatching { json.decodeFromString(historySerializer, it) }.getOrNull() } ?: emptyList()

    companion object {
        private const val MAX_HISTORY = 60
        private const val MAX_RECENT = 20

        fun liveKey(id: String) = "live:$id"
        fun isLiveFavorite(channel: LiveChannel, favorites: Set<String>): Boolean =
            liveKey(channel.id) in favorites || channel.variants.any { liveKey(it.id) in favorites }
        fun movieKey(id: String) = "movie:$id"
        fun seriesKey(id: String) = "series:$id"
        fun episodeKey(id: String) = "episode:$id"
    }
}

sealed interface SourceState {
    data object None : SourceState
    data class Some(val source: PlaylistSource) : SourceState
}
