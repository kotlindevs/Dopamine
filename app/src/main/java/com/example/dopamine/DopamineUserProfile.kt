package com.example.dopamine

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dopamine.databinding.ActivityDopamineUserProfileBinding
import com.google.firebase.auth.FirebaseAuth

class DopamineUserProfile : AppCompatActivity() {
    private lateinit var googleSession: googleSession
    private lateinit var binding: ActivityDopamineUserProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDopamineUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        googleSession = googleSession(this)
        firebaseAuth = FirebaseAuth.getInstance()

        if(googleSession.sharedPreferences.getString("Mon","")!!.startsWith("+91")){
            val userPhoto = "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/default-profile-picture-grey-male-icon.png"
            Glide.with(this).load(userPhoto).into(binding.UserPhoto)
        }else{
            val userPhoto = googleSession.sharedPreferences.getString("photo","")
            Glide.with(this).load(userPhoto).into(binding.UserPhoto)
        }
        binding.UserEmail.text = googleSession.sharedPreferences.getString("email","")
        binding.UserName.text = googleSession.sharedPreferences.getString("name","")
        binding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.logout ->{
                    googleSession.userLogOut()
                    firebaseAuth.signOut()
                    Toast.makeText(applicationContext,"Good bye 🥺",Toast.LENGTH_LONG).show()
                    true
                }

                else -> {
                    false
                }
            }
        }
    }
}