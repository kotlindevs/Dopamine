package com.example.dopamine.DopamineHome.Artist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.dopamine.DopamineHome.Artist.ArtistList.ArtistInterface
import com.example.dopamine.DopamineHome.TracksList.Adapter.TrackListAdapter
import com.example.dopamine.DopamineHome.TracksList.TracksDataClass.Track
import com.example.dopamine.DopamineMuiscPlayer.MasterMusicPlayer
import com.example.dopamine.authentication.googleSession
import com.example.dopamine.databinding.ActivityArtistProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ArtistProfile : AppCompatActivity() {
    private lateinit var binding: ActivityArtistProfileBinding
    private lateinit var adapter: TrackListAdapter
    private lateinit var googleSession: googleSession
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArtistProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        googleSession = googleSession(this)

        binding.recyclerView.layoutManager=LinearLayoutManager(this)

        if(intent.getStringExtra("hi_url")!=null){
            Glide.with(applicationContext)
                .load(intent.getStringExtra("hi_url"))
                .into(binding.ArtistHeaderImage)
        }else{
            Glide.with(applicationContext)
                .load("https://hesolutions.com.pk/wp-content/uploads/2019/01/picture-not-available.jpg")
                .into(binding.ArtistHeaderImage)
        }


        Glide.with(applicationContext)
            .load(intent.getStringExtra("ar_url"))
            .into(binding.ArtistProfileImage)

//        binding.ArtistProfileId.text = intent.getStringExtra("type")
        binding.ArtistProfileName.text = intent.getStringExtra("name")

        val id = intent.getStringExtra("id")
        intent.getStringExtra("base_url")?.let {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(it)
                .build()
                .create(ArtistInterface::class.java)

            if (id == "1OPqAyxsQc8mcRmoNBAnVk") {
                retrofit.getDhwaniList()
                    .enqueue(object : Callback<List<Track>> {
                        override fun onResponse(
                            call: Call<List<Track>>,
                            response: Response<List<Track>>
                        ) {
                            adapter = TrackListAdapter(applicationContext, response.body()!!)
                            binding.recyclerView.adapter = adapter
                        }

                        override fun onFailure(call: Call<List<Track>>, t: Throwable) {
                            Log.d("UserVal", t.message.toString())
                        }

                    })
            } else if (id == "4YRxDV8wJFPHPTeXepOstw") {
                retrofit.getArijitsList()
                    .enqueue(object : Callback<List<Track>> {
                        override fun onResponse(
                            call: Call<List<Track>>,
                            response: Response<List<Track>>
                        ) {
                            adapter = TrackListAdapter(
                                applicationContext,
                                response.body()!!
                            )
                            binding.recyclerView.adapter = adapter
                        }

                        override fun onFailure(call: Call<List<Track>>, t: Throwable) {
                            Log.d("UserVal", t.message.toString())
                        }

                    })
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.playArtistSong.setOnClickListener {
            startActivity(Intent(applicationContext,MasterMusicPlayer::class.java)
                .putExtra("id",intent.getStringExtra("id"))
                .putExtra("base_url",intent.getStringExtra("base_url"))
            )
        }
    }
}