package com.google.android.piyush.youtube.repository

import com.google.android.piyush.youtube.BROWSE
import com.google.android.piyush.youtube.TRENDING_GAMING
import com.google.android.piyush.youtube.TRENDING_MOVIES
import com.google.android.piyush.youtube.TRENDING_MUSIC
import com.google.android.piyush.youtube.Youtube
import com.google.android.piyush.youtube.model.BrowseBody
import com.google.android.piyush.youtube.model.BrowseResponse
import com.google.android.piyush.youtube.model.Context
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class YoutubeRepositoryImpl : YoutubeRepository {
    override suspend fun browseNow(browseId: String): BrowseResponse {
        return Youtube.post(BROWSE) {
            setBody(
                BrowseBody(
                    context = Context.WEB,
                    browseId = browseId
                )
            )
        }.body()
    }

    override suspend fun browseMusic(browseId: String): BrowseResponse {
        return Youtube.post(BROWSE) {
            setBody(
                BrowseBody(
                    context = Context.WEB,
                    browseId = browseId,
                    params = TRENDING_MUSIC
                )
            )
        }.body()
    }

    override suspend fun browseGaming(browseId: String): BrowseResponse {
        return Youtube.post(BROWSE) {
            setBody(
                BrowseBody(
                    context = Context.WEB,
                    browseId = browseId,
                    params = TRENDING_GAMING
                )
            )
        }.body()
    }

    override suspend fun browseMovies(browseId: String): BrowseResponse {
        return Youtube.post(BROWSE) {
            setBody(
                BrowseBody(
                    context = Context.WEB,
                    browseId = browseId,
                    params = TRENDING_MOVIES
                )
            )
        }.body()
    }
}