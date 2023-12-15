package com.example.dopamine.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.dopamine.databinding.ActivityContinueWithPhoneBinding
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class continue_with_phone : AppCompatActivity() {

    private lateinit var binding: ActivityContinueWithPhoneBinding
    private lateinit var materialSpinner: MaterialAutoCompleteTextView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var resendToken:PhoneAuthProvider.ForceResendingToken
    private lateinit var googleSession: googleSession
    lateinit var verificationId:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityContinueWithPhoneBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        googleSession = googleSession(this)
        setContentView(binding.root)

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            }

            override fun onVerificationFailed(ec: FirebaseException) {
                showToast(ec.toString())
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                verificationId=p0
                resendToken=p1
                val intent=Intent(applicationContext, continue_with_phone_otp::class.java)
                intent.putExtra("verifyId",verificationId)
                Log.d("verifyId",verificationId)
                startActivity(intent)
            }

        }


        binding.topAppBar.setOnClickListener {
            finish()
        }

        onBackPressedDispatcher.addCallback {  }

        binding.btnNext.setOnClickListener {
            if(binding.CountryNumbers!!.length()==0){
                Toast.makeText(this,"Number Can't Be Empty",Toast.LENGTH_SHORT).show()
            } else if(binding.CountryNumbers!!.length() < 10){
                Toast.makeText(this,"Phone Number Must Be 10 Digits",Toast.LENGTH_SHORT).show()
            } else if(binding!!.CountryNumbers.length() > 10){
                Toast.makeText(this,"Phone Number Can't Be More 10 Digits",Toast.LENGTH_SHORT).show()
            } else {
                val option = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setCallbacks(callbacks)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setPhoneNumber("+91${binding.CountryNumbers.text.toString()}")
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(option)
                showToast("Please Verify Number")
                startActivity(Intent(this, continue_with_phone_otp::class.java)
                    .putExtra("UserPhone", "+91${binding.CountryNumbers.text.toString()}")
                )
                googleSession.savedNum("+91${binding.CountryNumbers.text.toString()}")
                Log.d("UserVal","+91${binding.CountryNumbers.text.toString()}")
            }
        }
    }

    private fun showToast(str : String){
        Toast.makeText(applicationContext,str,Toast.LENGTH_LONG).show()
    }
}