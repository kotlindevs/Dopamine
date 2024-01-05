package com.example.dopamine.DopamineHome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dopamine.DopamineHome.Artist.Adapter.ArtistAdapter
import com.example.dopamine.DopamineHome.Artist.ArtistData.Artist
import com.example.dopamine.DopamineHome.Artist.ArtistList.ArtistInterface
import com.example.dopamine.DopamineHome.Fragments.Home
import com.example.dopamine.DopamineHome.Fragments.Library
import com.example.dopamine.DopamineHome.Fragments.Search
import com.example.dopamine.DopamineHome.OldButGold.Chart
import com.example.dopamine.DopamineHome.OldButGold.ChartsApi
import com.example.dopamine.DopamineHome.OldButGold.MusicChartAdapter
import com.example.dopamine.DopamineNotifications.DopamineNotifications
import com.example.dopamine.DopamineSettings.DopamineSettings
import com.example.dopamine.R
import com.example.dopamine.authentication.googleSession
import com.example.dopamine.databinding.ActivityDopamineHomeBinding
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar


class Dopamine_home : AppCompatActivity() {
    private lateinit var binding: ActivityDopamineHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDopamineHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        defaultScreen(Home())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){

                R.id.home -> {
                    defaultScreen(Home())
                    true
                }

                R.id.search -> {
                    defaultScreen(Search())
                    true
                }

                R.id.library -> {
                    defaultScreen(Library())
                    true
                }

                else -> {
                    false
                }
            }
        }
    }
    private fun defaultScreen(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
    }
}