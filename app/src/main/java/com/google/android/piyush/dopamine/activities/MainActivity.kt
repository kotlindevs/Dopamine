package com.google.android.piyush.dopamine.activities

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.authentication.utilities.GoogleAuth
import com.google.android.piyush.dopamine.authentication.repository.UserAuthRepositoryImpl
import com.google.android.piyush.dopamine.authentication.viewModel.UserAuthViewModel
import com.google.android.piyush.dopamine.authentication.viewModel.UserAuthViewModelFactory
import com.google.android.piyush.dopamine.databinding.ActivityMainBinding
import com.google.android.piyush.dopamine.utilities.NetworkUtilities

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
            if(backPressed!=true){
                showToast("Press Back Again To Exit")
                backPressed = true
            }else{
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finishAffinity()
                finish()
                System.exit(0)
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

        binding.phoneNumberLogin.setOnClickListener{
            startActivity(Intent(applicationContext, PhoneNumberAuthentication::class.java))
        }

        binding.googleSignIn.setOnClickListener{
            if(NetworkUtilities.isNetworkAvailable(this)){
                userViewModel.signInWithGoogle()
            }else{
                NetworkUtilities.showNetworkError(this)
            }
        }

        userViewModel.signInResult.observe(this) { user ->
            when(user){
                is GoogleAuth.Loading -> {
                   // Log.d(ContentValues.TAG, "Loading User !")
                }
                is GoogleAuth.Success -> {
                    showToast("Welcome , ${user.data?.name} ❤️")
                    startActivity(
                        Intent(
                            this,
                            DopamineHome::class.java
                        )
                    )
                    Log.d(ContentValues.TAG, " -> Activity : MainActivity || UserName : ${user.data?.name}")                }
                is GoogleAuth.Error -> {
                    showToast(user.message.toString())
                }
            }
        }

        binding.facebookSignIn.setOnClickListener{
            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
            materialAlertDialogBuilder.setMessage("Facebook Login Is Currently Under Development. You Can Try Google Login Or OTP Login To Enjoy Dopamine .")
                .setTitle("Under Development")
                .setCancelable(false)
                .setNegativeButton("Ok"){
                        dialog, _ ->
                    dialog.dismiss()
                }
            materialAlertDialogBuilder.create().show()
        }
    }

    private fun showToast(string : String){
        Toast.makeText(applicationContext,string, Toast.LENGTH_LONG).show()
    }
}