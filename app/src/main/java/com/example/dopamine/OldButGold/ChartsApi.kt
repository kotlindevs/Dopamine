package com.example.dopamine.OldButGold

import retrofit2.Call
import retrofit2.http.GET

interface ChartsApi {
    @GET("tracks")
    fun getCharts() : Call<List<Chart>>
}