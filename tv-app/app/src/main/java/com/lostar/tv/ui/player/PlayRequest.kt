package com.lostar.tv.ui.player

import com.lostar.tv.data.Channel
import com.lostar.tv.data.Section

sealed interface PlayRequest {
    /** Live TV: the whole category is passed so the remote can zap up/down. */
    data class Live(val channels: List<Channel>, val index: Int, val categoryName: String) : PlayRequest

    /** Movie or episode queue; episodes auto-advance to the next item. */
    data class Vod(val queue: List<VodItem>, val index: Int, val startPositionMs: Long = 0) : PlayRequest
}

data class VodItem(
    val key: String,
    val section: Section,
    val title: String,
    val subtitle: String?,
    val url: String,
    val image: String?,
    val seriesId: String? = null,
)
