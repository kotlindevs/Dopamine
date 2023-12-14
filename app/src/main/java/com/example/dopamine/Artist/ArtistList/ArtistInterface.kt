package com.example.dopamine.Artist.ArtistList

import com.example.dopamine.Artist.ArtistData.Artist
import retrofit2.Call
import retrofit2.http.GET

interface ArtistInterface {
    @GET("artist")
    fun getArtist() : Call<List<Artist>>
}