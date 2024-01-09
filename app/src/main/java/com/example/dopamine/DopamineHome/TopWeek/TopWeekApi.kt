package com.example.dopamine.DopamineHome.TopWeek

import retrofit2.Call
import retrofit2.http.GET

interface TopWeekApi {
    @GET("tracks")
    fun getTopWeek() : Call<List<TopWeek>>
}