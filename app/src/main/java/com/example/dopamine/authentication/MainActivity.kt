package com.example.dopamine.authentication

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.dopamine.Dopamine_home
import com.example.dopamine.R
import com.example.dopamine.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSession: googleSession
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        googleSession = googleSession(this)
        setContentView(binding.root)

        if(googleSession.isUserLogin()){
            startActivity(Intent(this, Dopamine_home::class.java))
            finish()
        }

        binding.signPhone.setOnClickListener{
            startActivity(Intent(applicationContext, continue_with_phone::class.java))
        }

        binding.signGoogle.setOnClickListener{
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this) {
                    try {
                        activityResult.launch(IntentSenderRequest.Builder(it.pendingIntent.intentSender).build())
                    } catch (e: IntentSender.SendIntentException) {
                        e.message?.let { it1 -> Log.e("UserVal", it1) }
                    }
                }
                .addOnFailureListener(this) { e ->
                    e.message?.let { it1 -> Log.e("UserVal", it1) }
                }
        }

        binding.signFb.setOnClickListener{
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

        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.web_client_id))
                    .setFilterByAuthorizedAccounts(true)
                    .build())
            .setAutoSelectEnabled(true)
            .build()
    }

    private val activityResult = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()){
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(it.data)
            val idToken = credential.googleIdToken

            if(idToken!=null){
                googleSession
                    .googleLogin(
                        credential.googleIdToken.toString(),
                        credential.profilePictureUri.toString(),
                        credential.displayName.toString()
                    )
                startActivity(Intent(this, Dopamine_home::class.java)
                    .putExtra("UserEmail", credential.googleIdToken.toString())
                    .putExtra("UserPhoto",credential.profilePictureUri.toString())
                    .putExtra("UserName",credential.displayName.toString()))
            }
        } catch (e: ApiException) {
            e.message?.let { it1 -> Log.d("UserVal", it1) }
        }
    }
    private fun showToast(str : String){
        Toast.makeText(applicationContext,str,Toast.LENGTH_LONG).show()
    }
}