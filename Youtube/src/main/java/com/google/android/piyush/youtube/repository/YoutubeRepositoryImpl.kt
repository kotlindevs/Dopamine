package com.google.android.piyush.youtube.repository

import com.google.android.piyush.youtube.utilities.YoutubeClient
import com.google.android.piyush.youtube.model.Youtube
import io.ktor.client.call.body
import io.ktor.client.request.get

class YoutubeRepositoryImpl : YoutubeRepository {
    override suspend fun getHomeVideos(): Youtube {
        val response = YoutubeClient.CLIENT.get(YoutubeClient.VIDEO){
            url {
                parameters.append("part", YoutubeClient.PART)
                parameters.append("chart", YoutubeClient.CHART)
                parameters.append("regionCode", YoutubeClient.REGION_CODE)
                parameters.append("maxResults", YoutubeClient.MAX_RESULTS)
                parameters.append("key", YoutubeClient.API_KEY)
            }
        }
        return response.body()
    }

    override suspend fun getLibraryVideos(playListId: String): Youtube {
        val response = YoutubeClient.CLIENT.get(YoutubeClient.PLAYLIST){
            url {
                parameters.append("part", YoutubeClient.PLAYLIST_PART)
                parameters.append("playlistId", playListId)
                parameters.append("maxResults", YoutubeClient.MAX_RESULTS)
                parameters.append("key", YoutubeClient.API_KEY)
            }
        }
        return response.body()
    }
}