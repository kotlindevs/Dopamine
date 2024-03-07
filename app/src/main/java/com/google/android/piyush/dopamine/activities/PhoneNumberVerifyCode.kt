package com.google.android.piyush.dopamine.activities

import android.os.Bundle
import android.widget.Toast
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
import com.google.android.piyush.dopamine.databinding.ActivityPhoneNumberVerifyCodeBinding
import com.google.android.piyush.dopamine.utilities.NetworkUtilities

class PhoneNumberVerifyCode : AppCompatActivity() {

    private lateinit var binding: ActivityPhoneNumberVerifyCodeBinding
    private lateinit var userRepository : UserAuthRepositoryImpl
    private lateinit var userViewModelFactory: UserAuthViewModelFactory
    private lateinit var userViewModel: UserAuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPhoneNumberVerifyCodeBinding.inflate(layoutInflater)
        userRepository = UserAuthRepositoryImpl(context = this)
        userViewModelFactory = UserAuthViewModelFactory(userRepository)
        userViewModel = ViewModelProvider(this, userViewModelFactory)[UserAuthViewModel::class.java]
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.topAppBar.setOnClickListener {
            finish()
        }

        val code = binding.code.text
        binding.yourPhoneNumber.text = intent.getStringExtra("phoneNumber")

        binding.verifyCode.setOnClickListener {
            if (NetworkUtilities.isNetworkAvailable(this)) {
                if(binding.code.text.toString().isEmpty()){
                    showToast("Please enter OTP")
                }else{
                    userViewModel.verifyOtp(code.toString())
                }
            } else {
                NetworkUtilities.showNetworkError(this)
            }
        }

        userViewModel.verifyOtpResult.observe(this) { result ->
            when (result.status) {
                Status.LOADING -> {
                    // Handle loading state
                }
                Status.SUCCESS -> {
                    showToast("Verification Successful")
                }
                Status.ERROR -> {
                    showToast("Verification Failed")
                }
            }
        }
    }
    private fun showToast(str : String){
        Toast.makeText(applicationContext,str, Toast.LENGTH_LONG).show()
    }
}