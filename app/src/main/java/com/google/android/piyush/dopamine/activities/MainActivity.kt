package com.google.android.piyush.dopamine.activities

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.authentication.repository.UserAuthRepositoryImpl
import com.google.android.piyush.dopamine.authentication.utilities.GoogleAuth
import com.google.android.piyush.dopamine.authentication.viewModel.UserAuthViewModel
import com.google.android.piyush.dopamine.authentication.viewModel.UserAuthViewModelFactory
import com.google.android.piyush.dopamine.databinding.ActivityMainBinding
import com.google.android.piyush.dopamine.utilities.NetworkUtilities
import com.google.android.piyush.dopamine.utilities.ToastUtilities.showToast
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userRepository : UserAuthRepositoryImpl
    private lateinit var userViewModelFactory: UserAuthViewModelFactory
    private lateinit var userViewModel: UserAuthViewModel
    private var backPressed = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
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

        onBackPressedDispatcher.addCallback {
            if(!backPressed){
                showToast(context = applicationContext,"Press Back Again To Exit")
                backPressed = true
            }else{
                @Suppress("DEPRECATION")
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finishAffinity()
                finish()
                exitProcess(0)
            }
        }

        if(userViewModel.currentUser()!=null){
            startActivity(
                Intent(
                    this,
                    DopamineHome::class.java
                )
            )
        }

        val launcher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ){ result ->
            if(result.resultCode == RESULT_OK) {
                lifecycleScope.launch {
                    val signInResult = userRepository.signInWithIntent(
                        intent = result.data ?: return@launch
                    )
                    userViewModel.onSignInResult(signInResult)
                }
            }else{
                Snackbar.make(
                    binding.main, "Something went wrong", Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        lifecycleScope.launch {
            userViewModel.state.collect { state ->
                if(state.isSignInSuccessful){
                    startActivity(
                        Intent(this@MainActivity, DopamineHome::class.java)
                    )
                    userViewModel.resetSignInState()
                }

                state.signInError?.let { error ->
                    Snackbar.make(
                        binding.main, error, Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.googleSignIn.setOnClickListener{
            lifecycleScope.launch {
                val signInIntentSender = userRepository.googleSignIn()
                launcher.launch(
                    IntentSenderRequest.Builder(
                        signInIntentSender ?: return@launch
                    ).build()
                )
            }
        }

        binding.phoneNumberLogin.setOnClickListener{
            startActivity(Intent(applicationContext, PhoneNumberAuthentication::class.java))
        }
    }
}