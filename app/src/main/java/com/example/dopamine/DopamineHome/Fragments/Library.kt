package com.example.dopamine.DopamineHome.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.dopamine.DopamineHome.DopamineUserProfile
import com.example.dopamine.DopamineLibrary.Like_songs
import com.example.dopamine.authentication.googleSession
import com.example.dopamine.databinding.FragmentLibraryBinding
import com.google.firebase.auth.FirebaseAuth

class Library : Fragment() {
    private lateinit var binding: FragmentLibraryBinding
    private lateinit var googleSession: googleSession
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLibraryBinding.inflate(inflater,container,false)

        googleSession = context?.let { googleSession(it) }!!
        firebaseAuth = FirebaseAuth.getInstance()

        if (googleSession.sharedPreferences.getString("Mon","")!!.startsWith("+91")){
            val userPhoto = "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/default-profile-picture-grey-male-icon.png"
            Glide.with(this).load(userPhoto).into(binding.UserImage)
        } else {
            val userPhoto = googleSession.sharedPreferences.getString("photo","")
            Glide.with(this).load(userPhoto).into(binding.UserImage)
        }

        binding.UserImage.setOnClickListener {
            Toast.makeText(context,googleSession.sharedPreferences.getString("name",""),
                Toast.LENGTH_SHORT).show()
            startActivity(Intent(context, DopamineUserProfile::class.java))
        }

        binding.likedSongsUser.setOnClickListener {
            startActivity(Intent(context, Like_songs::class.java))
        }

        binding.dopamineArtists.setOnClickListener {
            Toast.makeText(context,"Under Development", Toast.LENGTH_SHORT).show()
        }

        binding.dopaminePodcasters.setOnClickListener {
            Toast.makeText(context,"Under Development", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }
}