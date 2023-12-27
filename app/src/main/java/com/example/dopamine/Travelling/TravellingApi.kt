package com.example.dopamine.Travelling

import retrofit2.Call
import retrofit2.http.GET

interface TravellingApi {
    @GET("tracks")
    fun getTravelling() : Call<List<Travelling>>
}