package com.example.dopamine.TracksList

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dopamine.SqliteDb.SqliteHelper
import com.example.dopamine.TracksList.Adapter.TrackListAdapter
import com.example.dopamine.TracksList.TrackListApi.TracksApi
import com.example.dopamine.TracksList.TracksDataClass.Track
import com.example.dopamine.databinding.ActivityDopamineTracksBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DopamineTracks : AppCompatActivity() {
    private lateinit var binding: ActivityDopamineTracksBinding
    private lateinit var adapter: TrackListAdapter
    val baseUrl = "https://api.npoint.io/a2bbf40c66d86d855cda/"
    private lateinit var sqliteHelper: SqliteHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDopamineTracksBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sqliteHelper = SqliteHelper(applicationContext)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TracksApi::class.java)
            .getTracks()
            .enqueue(object :  Callback<List<Track>> {
                override fun onResponse(call: Call<List<Track>>, response: Response<List<Track>>) {
                    adapter = TrackListAdapter(applicationContext,response.body()!!)
                    binding.recyclerView.adapter = adapter
                    Log.d("track2",response.body().toString())
                    val stringBuilder = StringBuilder()
                    for(track in response.body()!!){
                        val tracks = Track(
                            id = track.id,
                            artist_name = track.artist_name,
                            song_name = track.song_name,
                            type = track.type,
                            is_playable = track.is_playable,
                            rc_url = track.rc_url,
                            mp_url = track.mp_url,
                            preview_url = track.preview_url,
                            release_date = track.release_date
                        )
                        stringBuilder.append(tracks)
                        if(!sqliteHelper.Exists(track.id)){
                            Log.d("track3",track.id)
                            sqliteHelper.addTracks(tracks)
                        }
                    }
                    Log.d("track1",stringBuilder.toString())
                }

                override fun onFailure(call: Call<List<Track>>, t: Throwable) {
                    Log.d("Tracks", t.message.toString())
                }

            })
    }
}