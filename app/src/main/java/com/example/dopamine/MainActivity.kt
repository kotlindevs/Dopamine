package com.example.dopamine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dopamine.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpFree.setOnClickListener{
            startActivity(Intent(applicationContext,sign_up_free::class.java))
        }

        binding.signPhone.setOnClickListener{
            startActivity(Intent(applicationContext,continue_with_phone::class.java))
        }

        binding.signGoogle.setOnClickListener{

        }

        binding.signFb.setOnClickListener{

        }

        binding.login.setOnClickListener{
            startActivity(Intent(applicationContext,login::class.java))
        }
    }
}