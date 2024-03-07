package com.google.android.piyush.dopamine.activities

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.authentication.Status
import com.google.android.piyush.dopamine.authentication.repository.UserAuthRepositoryImpl
import com.google.android.piyush.dopamine.authentication.viewModel.UserAuthViewModel
import com.google.android.piyush.dopamine.authentication.viewModel.UserAuthViewModelFactory
import com.google.android.piyush.dopamine.databinding.ActivityPhoneNumberAuthenticationBinding
import com.google.android.piyush.dopamine.utilities.NetworkUtilities

class PhoneNumberAuthentication : AppCompatActivity() {

    private lateinit var binding: ActivityPhoneNumberAuthenticationBinding
    private lateinit var userRepository : UserAuthRepositoryImpl
    private lateinit var userViewModelFactory: UserAuthViewModelFactory
    private lateinit var userViewModel: UserAuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPhoneNumberAuthenticationBinding.inflate(layoutInflater)
        userRepository = UserAuthRepositoryImpl(context = this)
        userViewModelFactory = UserAuthViewModelFactory(userRepository)
        userViewModel = ViewModelProvider(this, userViewModelFactory)[UserAuthViewModel::class.java]
        setContentView(binding.root)

        enableEdgeToEdge()
        setContentView(R.layout.activity_phone_number_authentication)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.topAppBar.setOnClickListener {
            finish()
        }

        onBackPressedDispatcher.addCallback {  }

        binding.sendCode.setOnClickListener {
            if(NetworkUtilities.isNetworkAvailable(this)){
                if(binding.phoneNumber.text.toString().isEmpty()){
                    showToast("Please enter phone number")
                }else if(binding.phoneNumber.text.toString().length < 10){
                    showToast("Please enter minimum 10 digit phone number")
                }else if(binding.phoneNumber.text.toString().length > 10){
                    showToast("Please enter maximum 10 digit phone number")
                }else {
                    val phoneNumber = "+91 ${binding.phoneNumber.text}"
                    userViewModel.sendVerificationCode(
                        PhoneNumberUtils.formatNumber(phoneNumber, "IN")
                    )
                    Log.d(ContentValues.TAG, "onCreate: $phoneNumber")
                }
            }else{
                NetworkUtilities.showNetworkError(this)
            }
        }

        userViewModel.sendVerificationCodeResult.observe(this){
            when(it.status) {
                Status.SUCCESS -> {
                    Log.d(ContentValues.TAG, "onSendVerificationCode : Success")
                    startActivity(
                        Intent(
                            this,
                            PhoneNumberVerifyCode::class.java
                        ).putExtra("phoneNumber", "+91 ${binding.phoneNumber.text}")
                    )
                }

                Status.ERROR -> {
                    showToast(it.message.toString())
                }

                Status.LOADING -> {
                    Log.d(ContentValues.TAG, "onSendVerificationCode : Loading")
                }
            }
        }
    }

    private fun showToast(str : String){
        Toast.makeText(applicationContext,str, Toast.LENGTH_LONG).show()
    }
}