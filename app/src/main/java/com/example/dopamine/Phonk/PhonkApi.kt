package com.example.dopamine.Phonk

import retrofit2.Call
import retrofit2.http.GET

interface PhonkApi {
    @GET("tracks")
    fun getPhonk() : Call<List<Phonk>>
}