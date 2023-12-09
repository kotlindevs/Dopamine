package com.example.dopamine

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.dopamine.databinding.ActivitySignUpFreeBinding
import com.google.firebase.auth.FirebaseAuth

class sign_up_free : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpFreeBinding
    private lateinit var email : EditText
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpFreeBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        setContentView(binding.root)
        email = findViewById(R.id.user_email)

        binding.topAppBar.setOnClickListener {
            finish()
        }
        onBackPressedDispatcher.addCallback {}

        binding.btnNext.setOnClickListener {
            if (email.length()==0){
                email.error = "Email Can't Be Empty"
            } else {
                startActivity(Intent(applicationContext,sign_up_free_pass::class.java).putExtra("SignupEmail",email.text.toString()))
            }
        }
    }
    private fun showToast(str : String){
        Toast.makeText(applicationContext,str,Toast.LENGTH_SHORT).show()
    }
}