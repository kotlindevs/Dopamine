package com.example.dopamine

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.dopamine.databinding.ActivitySignUpFreeBinding

class sign_up_free : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpFreeBinding
    private lateinit var email : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpFreeBinding.inflate(layoutInflater)
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
}