package com.example.dopamine.DopamineHome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dopamine.DopamineHome.Artist.Adapter.ArtistAdapter
import com.example.dopamine.DopamineHome.Artist.ArtistData.Artist
import com.example.dopamine.DopamineHome.Artist.ArtistList.ArtistInterface
import com.example.dopamine.DopamineHome.OldButGold.Chart
import com.example.dopamine.DopamineHome.OldButGold.ChartsApi
import com.example.dopamine.DopamineHome.OldButGold.MusicChartAdapter
import com.example.dopamine.DopamineLibrary.Dopamine_library
import com.example.dopamine.DopamineSearch.Dopamine_search
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
    private lateinit var adapter: MusicChartAdapter
    val baseUrl = "https://api.npoint.io/504ec4f9cb720cbeb8df/"

    private lateinit var googleSession: googleSession
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var artistAdapter: ArtistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDopamineHomeBinding.inflate(layoutInflater)
        googleSession = googleSession(this)
        firebaseAuth = FirebaseAuth.getInstance()
        setContentView(binding.root)

        //Recycler View Work
        val recyclerViewLeft = findViewById<RecyclerView>(R.id.left_bar)
        val recyclerViewRight = findViewById<RecyclerView>(R.id.right_bar)
        binding.musicArtist.layoutManager = LinearLayoutManager(this,binding.musicArtist.horizontalFadingEdgeLength,false)

        recyclerViewLeft.layoutManager = LinearLayoutManager(this)
        recyclerViewRight.layoutManager = LinearLayoutManager(this)

        val data_left = ArrayList<ItemsViewModel>()
        data_left.add(ItemsViewModel(R.drawable.dopamine,"Dopamine's Picks"))
        data_left.add(ItemsViewModel(R.drawable.bollywood,"Bollywood"))
        data_left.add(ItemsViewModel(R.drawable.phonk,"Phonk"))
        data_left.add(ItemsViewModel(R.drawable.gaming,"Gaming and chill"))
        val adapter_left = MusicAdapter(applicationContext,data_left)
        recyclerViewLeft.adapter = adapter_left

        val data_right = ArrayList<ItemsViewModel>()
        data_right.add(ItemsViewModel(R.drawable.trending,"Trending"))
        data_right.add(ItemsViewModel(R.drawable.travel,"Travelling"))
        data_right.add(ItemsViewModel(R.drawable.remix,"Remix"))
        data_right.add(ItemsViewModel(R.drawable.gym,"Gym & Workout"))
        val adapter_right = MusicAdapter(applicationContext,data_right)
        recyclerViewRight.adapter = adapter_right

        //Recycler View Old But Gold
        val recyclerViewChart = findViewById<RecyclerView>(R.id.music_chart)
        recyclerViewChart.setHasFixedSize(true)
        recyclerViewChart.layoutManager = LinearLayoutManager(this,recyclerViewChart.horizontalFadingEdgeLength,false)
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChartsApi::class.java)
            .getCharts()
            .enqueue(object : Callback<List<Chart>>{
                override fun onResponse(call: Call<List<Chart>>, response: Response<List<Chart>>) {
                    adapter = MusicChartAdapter(applicationContext,response.body()!!)
                    recyclerViewChart.adapter = adapter
                }

                override fun onFailure(call: Call<List<Chart>>, t: Throwable) {
                    //
                }
            })


        //Greeting Message Backend
        val fullTime  = Calendar.getInstance().time
        val time = fullTime.hours
        if(time >= 6 && time < 12 ){
            binding.greeting.text = "Good Morning"
        }else if(time >= 12 && time < 16  ){
            binding.greeting.text = "Good Afternoon"
        }else if (time >= 16 && time < 20 ){
            binding.greeting.text = "Good Evening"
        }else{
            binding.greeting.text = "Good Night"
        }


        //Photo Fetching
        if(googleSession.sharedPreferences.getString("Mon","")!!.startsWith("+91")){
            val userPhoto = "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/default-profile-picture-grey-male-icon.png"
            Glide.with(this).load(userPhoto).into(binding.UserImage)
        }else{
            val userPhoto = googleSession.sharedPreferences.getString("photo","")
            Glide.with(this).load(userPhoto).into(binding.UserImage)
        }

        binding.bottomNavigationView.setSelectedItemId(R.id.home)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){

                R.id.home -> {
                    true
                }

                R.id.search -> {
                    startActivity(Intent(applicationContext, Dopamine_search::class.java))
                    finish()
                    true
                }

                R.id.library -> {
                    startActivity(Intent(applicationContext, Dopamine_library::class.java))
                    finish()
                    true
                }

                else -> {

                }
            }
            false
        }

        binding.settings.setOnClickListener {
            Toast.makeText(applicationContext,"Settings",Toast.LENGTH_SHORT).show()
        }

        binding.notifications.setOnClickListener {
            Toast.makeText(applicationContext,"Notifications",Toast.LENGTH_SHORT).show()
        }

        binding.UserImage.setOnClickListener {
            Toast.makeText(applicationContext,googleSession.sharedPreferences.getString("name",""),Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, DopamineUserProfile::class.java))
        }

        Retrofit.Builder()
            .baseUrl("https://api.npoint.io/a3ea088449c3a010fb5d/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ArtistInterface::class.java)
            .getArtist()
            .enqueue(object : Callback<List<Artist>>{
                override fun onResponse(
                    call: Call<List<Artist>>,
                    response: Response<List<Artist>>
                ) {
                    artistAdapter = ArtistAdapter(applicationContext,response.body()!!)
                    binding.musicArtist.adapter = artistAdapter
                    Log.d("Artists", response.body()!!.toString())
                }

                override fun onFailure(call: Call<List<Artist>>, t: Throwable) {
                    Log.d("Artists", t.message.toString())
                }

            })
    }
}