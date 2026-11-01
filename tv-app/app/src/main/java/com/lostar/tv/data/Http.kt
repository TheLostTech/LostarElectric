package com.lostar.tv.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class HttpException(val code: Int, message: String) : IOException(message)

internal suspend fun OkHttpClient.getString(url: String): String = withContext(Dispatchers.IO) {
    newCall(Request.Builder().url(url).build()).execute().use { response ->
        if (!response.isSuccessful) throw HttpException(response.code, "HTTP ${response.code}")
        response.body?.string() ?: ""
    }
}

internal suspend fun <T> OkHttpClient.getLines(url: String, block: (Sequence<String>) -> T): T =
    withContext(Dispatchers.IO) {
        newCall(Request.Builder().url(url).build()).execute().use { response ->
            if (!response.isSuccessful) throw HttpException(response.code, "HTTP ${response.code}")
            val body = response.body ?: throw IOException("Risposta vuota")
            body.charStream().buffered().useLines(block)
        }
    }
