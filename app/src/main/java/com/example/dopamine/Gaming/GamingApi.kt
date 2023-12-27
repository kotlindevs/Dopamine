package com.example.dopamine.Gaming

import retrofit2.Call
import retrofit2.http.GET

interface GamingApi {
    @GET("tracks")
    fun getGaming() : Call<List<Gaming>>
}