package com.example.dopamine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import com.example.dopamine.databinding.ActivityContinueWithPhoneBinding

class continue_with_phone : AppCompatActivity() {

    private lateinit var binding: ActivityContinueWithPhoneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityContinueWithPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setOnClickListener {
            finish()
        }

        onBackPressedDispatcher.addCallback{}

        binding.btnNext.setOnClickListener {
            if(binding.CountryNumbers!!.length()==0){
                Toast.makeText(this,"Number Can't Be Empty",Toast.LENGTH_SHORT).show()
            } else if(binding.CountryNumbers!!.length() < 10){
                Toast.makeText(this,"Phone Number Must Be 10 Digits",Toast.LENGTH_SHORT).show()
            } else if(binding!!.CountryNumbers.length() > 10){
                Toast.makeText(this,"Phone Number Can't Be More 10 Digits",Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(applicationContext,continue_with_phone_otp::class.java)
                    .putExtra("send_phone","+91${binding.CountryNumbers.text.toString()}"))
            }
        }
    }
}