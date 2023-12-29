package com.example.dopamine.DopamineHome.Artist.ArtistList

import com.example.dopamine.DopamineHome.Artist.ArtistData.Artist
import com.example.dopamine.DopamineHome.TracksList.TracksDataClass.Track
import retrofit2.Call
import retrofit2.http.GET

interface ArtistInterface {
    @GET("artist")
    fun getArtist() : Call<List<Artist>>
    @GET("dhwani")
    fun getDhwaniList() : Call<List<Track>>

    @GET("arijit")
    fun getArijitsList() : Call<List<Track>>
}