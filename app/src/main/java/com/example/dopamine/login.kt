package com.example.dopamine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PatternMatcher
import android.util.Patterns
import android.widget.Toast
import androidx.activity.addCallback
import com.example.dopamine.databinding.ActivityLoginBinding

class login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setOnClickListener {
            finish()
        }
        onBackPressedDispatcher.addCallback{}

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
                //
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