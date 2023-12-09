package com.example.dopamine

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.dopamine.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSession: googleSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        googleSession = googleSession(this)
        setContentView(binding.root)

        binding.topAppBar.setOnClickListener {
            finish()
        }
        onBackPressedDispatcher.addCallback {  }

        binding.btnNext.setOnClickListener {
            if(binding.userEmail!!.length()==0){
                ShowToast("Email Can't Be Empty")
            } else if(!Patterns.EMAIL_ADDRESS.matcher(binding.userEmail.text.toString()).matches()){
                ShowToast("Email Format is Wrong")
            } else if(binding.userPass!!.length()==0){
                ShowToast("Password Can't Be Empty")
            } else if(binding.userPass!!.length() < 8) {
                ShowToast("Password Must Be 8 Characters")
            } else if(binding.userPass!!.length() > 8){
                ShowToast("Password Must Be 8 Characters")
            } else {
                val email = binding.userEmail.text.toString()
                val passwd = binding.userPass.text.toString()
                firebaseAuth.signInWithEmailAndPassword(email,passwd)
                    .addOnSuccessListener {
                        Toast.makeText(applicationContext,"Successfully login !",Toast.LENGTH_LONG).show()
                        startActivity(Intent(applicationContext,Dopamine_home::class.java))
                        googleSession.emailPasswordSession(email,passwd)
                    }
                    .addOnFailureListener {
                        Toast.makeText(applicationContext,it.message.toString(),Toast.LENGTH_LONG).show()
                    }
            }
        }

        binding.signUpFree.setOnClickListener{
            startActivity(Intent(applicationContext,sign_up_free::class.java))
            finish()
        }

    }
    private fun ShowToast(str : String){
        Toast.makeText(applicationContext,str,Toast.LENGTH_SHORT).show()
    }
}