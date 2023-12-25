package com.example.dopamine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.dopamine.authentication.googleSession
import com.example.dopamine.databinding.ActivityDopamineLibraryBinding
import com.google.firebase.auth.FirebaseAuth


class Dopamine_library : AppCompatActivity() {
    private lateinit var binding: ActivityDopamineLibraryBinding
    private lateinit var googleSession: googleSession
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDopamineLibraryBinding.inflate(layoutInflater)
        googleSession = googleSession(this)
        firebaseAuth = FirebaseAuth.getInstance()
        setContentView(binding.root)

        binding.bottomNavigationView.setSelectedItemId(R.id.library)

        //Photo Fetching
        if (googleSession.sharedPreferences.getString("Mon","")!!.startsWith("+91")){
            val userPhoto = "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/default-profile-picture-grey-male-icon.png"
            Glide.with(this).load(userPhoto).into(binding.UserImage)
        } else {
            val userPhoto = googleSession.sharedPreferences.getString("photo","")
            Glide.with(this).load(userPhoto).into(binding.UserImage)
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){

                R.id.home -> {
                    startActivity(Intent(applicationContext,Dopamine_home::class.java))
                    finish()
                    true
                }

                R.id.search -> {
                    startActivity(Intent(applicationContext,Dopamine_search::class.java))
                    finish()
                    true
                }

                R.id.library -> {

                    true
                }

                else -> {

                }
            }
            false
        }

        binding.UserImage.setOnClickListener {
            Toast.makeText(applicationContext,googleSession.sharedPreferences.getString("name",""),
                Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,DopamineUserProfile::class.java))
        }

        binding.likedSongsUser.setOnClickListener {
            startActivity(Intent(applicationContext,Like_songs::class.java))
        }

        binding.addArtistsUser.setOnClickListener {
            Toast.makeText(this,"Under Development",Toast.LENGTH_SHORT).show()
        }

        binding.addPodcasts.setOnClickListener {
            Toast.makeText(this,"Under Development",Toast.LENGTH_SHORT).show()
        }
    }
}