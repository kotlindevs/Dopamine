package com.example.dopamine.DopamineHome.Gym

import retrofit2.Call
import retrofit2.http.GET

interface GymApi {
    @GET("tracks")
    fun getGym() : Call<List<Gym>>
}