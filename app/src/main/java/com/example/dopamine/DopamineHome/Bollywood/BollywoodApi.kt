package com.example.dopamine.DopamineHome.Bollywood

import retrofit2.Call
import retrofit2.http.GET

interface BollywoodApi {
    @GET("tracks")
    fun getBollywood() : Call<List<Bollywood>>
}