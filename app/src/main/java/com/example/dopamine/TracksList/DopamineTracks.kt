package com.example.dopamine.TracksList

import android.os.Bundle
import android.util.Log
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dopamine.TracksList.Adapter.TrackListAdapter
import com.example.dopamine.TracksList.TrackListApi.TracksApi
import com.example.dopamine.TracksList.TracksDataClass.Track
import com.example.dopamine.authentication.googleSession
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
    private lateinit var googleSession: googleSession
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDopamineTracksBinding.inflate(layoutInflater)
        setContentView(binding.root)
        googleSession = googleSession(this)

        binding.topAppBar.setNavigationOnClickListener{
            finish()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TracksApi::class.java)
            .getTracks()
            .enqueue(object :  Callback<List<Track>> {
                override fun onResponse(call: Call<List<Track>>, response: Response<List<Track>>) {
                    adapter = TrackListAdapter(applicationContext,response.body()!!,googleSession)
                    binding.recyclerView.adapter = adapter
                    Log.d("Tracks", response.body()!!.toString())
                }

                override fun onFailure(call: Call<List<Track>>, t: Throwable) {
                    Log.d("Tracks", t.message.toString())
                }

            })



    }
}