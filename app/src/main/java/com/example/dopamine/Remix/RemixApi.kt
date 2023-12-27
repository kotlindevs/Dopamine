package com.example.dopamine.Remix

import retrofit2.Call
import retrofit2.http.GET

interface RemixApi {
    @GET("tracks")
    fun getRemix() : Call<List<Remix>>
}
