package com.example.dopamine

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dopamine.Artist.Adapter.ArtistAdapter
import com.example.dopamine.Artist.ArtistData.Artist
import com.example.dopamine.Artist.ArtistList.ArtistInterface
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
        data_left.add(ItemsViewModel(R.drawable.likedsongs,"Liked Songs"))
        data_left.add(ItemsViewModel(R.drawable.tth,"Today's Top Hits"))
        data_left.add(ItemsViewModel(R.drawable.mix,"2010s Mix"))
        val adapter_left = MusicAdapter(applicationContext,data_left)
        recyclerViewLeft.adapter = adapter_left

        val data_right = ArrayList<ItemsViewModel>()
        data_right.add(ItemsViewModel(R.drawable.blade_runner,"Blade Runner"))
        data_right.add(ItemsViewModel(R.drawable.imagine,"Imagine Dragons"))
        data_right.add(ItemsViewModel(R.drawable.lofi,"chill lofi study \n beats"))
        val adapter_right = MusicAdapter(applicationContext,data_right)
        recyclerViewRight.adapter = adapter_right

        //Recycler View Charts
        val recyclerViewChart = findViewById<RecyclerView>(R.id.music_chart)
        recyclerViewChart.setHasFixedSize(true)
        recyclerViewChart.layoutManager = LinearLayoutManager(this,recyclerViewChart.horizontalFadingEdgeLength,false)
        val data_chart = ArrayList<ItemsViewModel>()
        data_chart.add(ItemsViewModel((R.drawable.sia),"Sia"))
        data_chart.add(ItemsViewModel((R.drawable.tth),"Top Hits"))
        data_chart.add(ItemsViewModel((R.drawable.mix),"2010s Mix"))
        data_chart.add(ItemsViewModel((R.drawable.blade_runner),"Narvent Fainted"))
        data_chart.add(ItemsViewModel((R.drawable.imagine),"Thunder"))
        data_chart.add(ItemsViewModel((R.drawable.lofi),"Binaural Beats"))
        data_chart.add(ItemsViewModel((R.drawable.eminem),"Superman"))
        val adapter_chart = MusicChartAdapter(applicationContext,data_chart)
        recyclerViewChart.adapter = adapter_chart


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
                    startActivity(Intent(applicationContext,Dopamine_search::class.java))
                    finish()
                    true
                }

                R.id.library -> {
                    startActivity(Intent(applicationContext,Dopamine_library::class.java))
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
            startActivity(Intent(this,DopamineUserProfile::class.java))
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