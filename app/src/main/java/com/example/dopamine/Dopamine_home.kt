package com.example.dopamine

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dopamine.databinding.ActivityDopamineHomeBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class Dopamine_home : AppCompatActivity() {
    private lateinit var binding: ActivityDopamineHomeBinding
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSession: googleSession
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDopamineHomeBinding.inflate(layoutInflater)
        googleSession = googleSession(this)
        firebaseAuth = FirebaseAuth.getInstance()
        setContentView(binding.root)

        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions)

        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
        if(googleSignInAccount!=null){
            binding.UserName.text = googleSignInAccount.displayName
            Glide
                .with(this)
                .load(googleSignInAccount.photoUrl)
                .into(binding.UserImage)
        }
        binding.UserSignOut.setOnClickListener {
            googleSession.userLogOut()
            Toast.makeText(applicationContext,"Good Bye ☹️",Toast.LENGTH_LONG).show()
            firebaseAuth.signOut()
        }
    }
}