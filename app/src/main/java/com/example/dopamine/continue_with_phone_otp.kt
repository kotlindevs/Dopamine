package com.example.dopamine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import com.example.dopamine.databinding.ActivityContinueWithPhoneOtpBinding

class continue_with_phone_otp : AppCompatActivity() {

    private lateinit var binding: ActivityContinueWithPhoneOtpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityContinueWithPhoneOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.receivedPhoneNo.setText(intent.getStringExtra("send_phone"))

        binding.topAppBar.setOnClickListener {
            finish()
        }
        onBackPressedDispatcher.addCallback{}

        binding.btnNext.setOnClickListener {
            if(binding.otp!!.length()==0){
                Toast.makeText(this,"OTP Can't Be Empty",Toast.LENGTH_SHORT).show()
            } else if(binding.otp!!.length() > 6){
                Toast.makeText(this,"OTP Can't Be More Than 6 Digits",Toast.LENGTH_SHORT).show()
            } else if(binding.otp!!.length() < 6){
                Toast.makeText(this,"OTP Can't Be Less Than 6 Digits",Toast.LENGTH_SHORT).show()
            } else {
                //startActivity()
            }
        }

        binding.btnResendOtp.setOnClickListener{
            Toast.makeText(this,"Under Development",Toast.LENGTH_SHORT).show()
        }

        binding.btnEditPhoneNo.setOnClickListener {
            finish()
        }
    }
}