package com.example.dopamine

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dopamine.databinding.ActivityDopamineHomeBinding
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

        binding.UserName.text = googleSession.sharedPreferences.getString("email","")
        val userPhoto = googleSession.sharedPreferences.getString("photo","")
        Log.d("UserImage",userPhoto.toString())
        Glide
            .with(this)
            .load(userPhoto)
            .into(binding.UserImage)
        binding.UserSignOut.setOnClickListener {
            googleSession.userLogOut()
            Toast.makeText(applicationContext,"Good Bye ☹️", Toast.LENGTH_LONG).show()
            firebaseAuth.signOut()
        }
    }
}