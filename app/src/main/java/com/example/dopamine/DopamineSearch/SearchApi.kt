package com.example.dopamine.DopamineSearch

import retrofit2.Call
import retrofit2.http.GET

interface SearchApi {
    @GET("data")
    fun browseAll() : Call<List<Data>>
}