package com.example.dopamine.DopamineHome.IndiaBest

import retrofit2.Call
import retrofit2.http.GET

interface IndiaBestApi {
    @GET("tracks")
    fun getIndiaBest() : Call<List<IndiaBest>>
}