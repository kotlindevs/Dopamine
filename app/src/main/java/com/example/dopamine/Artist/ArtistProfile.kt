package com.example.dopamine.Artist

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.dopamine.Artist.Adapter.ArtistAdapter
import com.example.dopamine.R
import com.example.dopamine.TracksList.Adapter.TrackListAdapter
import com.example.dopamine.databinding.ActivityArtistProfileBinding
import retrofit2.Retrofit

class ArtistProfile : AppCompatActivity() {
    private lateinit var binding: ActivityArtistProfileBinding
    private lateinit var adapter: TrackListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArtistProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager=LinearLayoutManager(this)

        if(intent.getStringExtra("header_image")!=null){
            Glide.with(applicationContext)
                .load(intent.getStringExtra("header_image"))
                .into(binding.ArtistHeaderImage)
        }else{
            Glide.with(applicationContext)
                .load("https://hesolutions.com.pk/wp-content/uploads/2019/01/picture-not-available.jpg")
                .into(binding.ArtistHeaderImage)
        }


        Glide.with(applicationContext)
            .load(intent.getStringExtra("profile_image"))
            .into(binding.ArtistProfileImage)

//        binding.ArtistProfileId.text = intent.getStringExtra("type")
        binding.ArtistProfileName.text = intent.getStringExtra("artist_name")
    }
}