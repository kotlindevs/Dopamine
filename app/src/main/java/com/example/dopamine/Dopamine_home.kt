package com.example.dopamine

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dopamine.databinding.ActivityDopamineHomeBinding
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar


class Dopamine_home : AppCompatActivity() {
    private lateinit var binding: ActivityDopamineHomeBinding
    private lateinit var googleSession: googleSession
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDopamineHomeBinding.inflate(layoutInflater)
        googleSession = googleSession(this)
        firebaseAuth = FirebaseAuth.getInstance()
        setContentView(binding.root)

        //Recycler View Work
        val recyclerViewLeft = findViewById<RecyclerView>(R.id.left_bar)
        val recyclerViewRight = findViewById<RecyclerView>(R.id.right_bar)

        recyclerViewLeft.layoutManager = LinearLayoutManager(this)
        recyclerViewRight.layoutManager = LinearLayoutManager(this)

        val data_left = ArrayList<ItemsViewModel>()
        data_left.add(ItemsViewModel(R.drawable.likedsongs,"Liked Songs"))
        data_left.add(ItemsViewModel(R.drawable.baseline_favorite_24,"Today's Top Hits"))
        data_left.add(ItemsViewModel(R.drawable.baseline_favorite_24,"2010s Mix"))
        val adapter_left = MusicAdapter(data_left)
        recyclerViewLeft.adapter = adapter_left

        val data_right = ArrayList<ItemsViewModel>()
        data_right.add(ItemsViewModel(R.drawable.baseline_favorite_24,"Blade Runner"))
        data_right.add(ItemsViewModel(R.drawable.baseline_favorite_24,"Imagine Dragons"))
        data_right.add(ItemsViewModel(R.drawable.baseline_favorite_24,"chill lofi study \n beats"))
        val adapter_right = MusicAdapter(data_right)
        recyclerViewRight.adapter = adapter_right


        //Greeting Message Backend
        val df = SimpleDateFormat("HH:mm:ss")
        val formattedDate = df.format(Calendar.getInstance().time)
        if(formattedDate == "00:00:00" || formattedDate == "12:00:00"){
            binding.greeting.text = "Good Morning"
        } else {
            binding.greeting.text = "Good Evening"
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
    }
}