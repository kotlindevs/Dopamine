package com.example.dopamine

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dopamine.databinding.ActivityDopamineHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth


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
    }
}