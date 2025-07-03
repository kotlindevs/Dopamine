package com.google.android.piyush.youtube

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val Youtube = HttpClient(OkHttp) {

    expectSuccess = true

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                Log.v("Logger Ktor =>", message)
            }
        }
        level = LogLevel.NONE
    }
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            }
        )
    }

    defaultRequest {
        url(scheme = "https", host = "www.youtube.com") {
            headers.append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }
    }
}

const val BROWSE = "/youtubei/v1/browse"
const val PLAYER = "/youtubei/v1/player"
const val SEARCH = "/youtubei/v1/search"
const val TRENDING = "FEtrending"
const val TRENDING_MUSIC = "4gINGgt5dG1hX2NoYXJ0cw%3D%3D"
const val TRENDING_GAMING = "4gIcGhpnYW1pbmdfY29ycHVzX21vc3RfcG9wdWxhcg%3D%3D"
const val TRENDING_MOVIES = "4gIKGgh0cmFpbGVycw%3D%3D"

const val CHANNEL_HOME = "EghmZWF0dXJlZPIGBAoCMgA%3D"
const val CHANNEL_SHORTS = "EgZzaG9ydHPyBgUKA5oBAA%3D%3D"
const val CHANNEL_VIDEOS = "EgZ2aWRlb3PyBgQKAjoA"
const val CHANNEL_PLAYLISTS = "EglwbGF5bGlzdHPyBgQKAkIA"
const val CHANNEL_POSTS = "EgVwb3N0c_IGBAoCSgA%3D"