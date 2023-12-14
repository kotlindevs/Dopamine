package com.example.dopamine

import retrofit2.Call
import retrofit2.http.GET

interface SearchApi {
    @GET("data")
    fun browseAll() : Call<List<Data>>
}