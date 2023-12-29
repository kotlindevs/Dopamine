package com.example.dopamine.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.dopamine.DopamineHome.Dopamine_home
import com.example.dopamine.databinding.ActivityContinueWithPhoneOtpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class continue_with_phone_otp : AppCompatActivity() {
    private lateinit var binding: ActivityContinueWithPhoneOtpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSession: googleSession
    private lateinit var mAuthNumber : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityContinueWithPhoneOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth=FirebaseAuth.getInstance()
        googleSession = googleSession(this)
        mAuthNumber= googleSession.sharedPreferences.getString("str","").toString()
        val verificationId =  intent.getStringExtra("verifyId")

        binding.receivedPhoneNo.text =mAuthNumber

        binding.topAppBar.setOnClickListener {
            finish()
        }
        onBackPressedDispatcher.addCallback {  }

        binding.btnNext.setOnClickListener {
            if(binding.otp!!.length()==0){
                Toast.makeText(this,"OTP Can't Be Empty",Toast.LENGTH_SHORT).show()
            } else if(binding.otp!!.length() > 6){
                Toast.makeText(this,"OTP Can't Be More Than 6 Digits",Toast.LENGTH_SHORT).show()
            } else if(binding.otp!!.length() < 6){
                Toast.makeText(this,"OTP Can't Be Less Than 6 Digits",Toast.LENGTH_SHORT).show()
            } else {
                val otp = binding.otp.text.toString()
                if (!otp.isEmpty()) {
                    val credential: PhoneAuthCredential =
                        PhoneAuthProvider.getCredential(verificationId.toString(), otp)
                    signInWithPhoneAuthCredentialDemo(credential)
                } else {
                    showToast("Enter Otp")
                }
            }
        }


        binding.btnResendOtp.setOnClickListener{
            Toast.makeText(this,"Under Development",Toast.LENGTH_SHORT).show()
        }

        binding.btnEditPhoneNo.setOnClickListener {
            finish()
        }
    }
    private fun signInWithPhoneAuthCredentialDemo(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    showToast("Welcome !")
                    googleSession.mobileOtpSession(mAuthNumber)
                    startActivity(Intent(this, Dopamine_home::class.java))
                } else {
                    showToast(it.exception.toString())
                }
            }
    }
    private fun showToast(str : String){
        Toast.makeText(applicationContext,str,Toast.LENGTH_LONG).show()
    }
}