package com.example.dopamine

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.dopamine.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSession: googleSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        googleSession = googleSession(this)
        setContentView(binding.root)

        if(googleSession.isUserLogin()){
            startActivity(Intent(this,Dopamine_home::class.java))
            finish()
        }

        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this,signInOptions)

        binding.signUpFree.setOnClickListener{
            startActivity(Intent(applicationContext,sign_up_free::class.java))
        }

        binding.signPhone.setOnClickListener{
            startActivity(Intent(applicationContext,continue_with_phone::class.java))
        }

        binding.signGoogle.setOnClickListener{
           networkPermission()
        }

        binding.signFb.setOnClickListener{

        }

        binding.login.setOnClickListener{
            startActivity(Intent(applicationContext,login::class.java))
        }
    }

    private fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result -> if(result.resultCode==Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResult(task)
        }
    }

    private fun handleResult(task : Task<GoogleSignInAccount>){
        if(task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if(account!=null){
                updateUI(account)
            }else{
                showToast(task.exception.toString())
            }
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    showToast("Welcome")
                    val userDetail = GoogleSignIn.getLastSignedInAccount(this)
                    googleSession
                        .googleLogin(
                            userDetail?.email.toString(),
                            userDetail?.photoUrl.toString(),
                            userDetail?.displayName.toString()
                        )
                    startActivity(Intent(this,Dopamine_home::class.java)
                        .putExtra("UserEmail",userDetail?.email)
                        .putExtra("UserPhoto",userDetail?.photoUrl)
                        .putExtra("UserName",userDetail?.displayName))
                }else{
                    showToast(it.toString())
                }
            }
    }

    private fun showToast(str : String){
        Toast.makeText(applicationContext,str,Toast.LENGTH_LONG).show()
    }

    private fun networkPermission() {
        if(checkSelfPermission(android.Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED){
            googleSignIn()
        }else if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.INTERNET)){
            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
                .setTitle("Permission required")
                .setCancelable(false)
                .setPositiveButton("OK") {
                        dialog, _ ->
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.INTERNET),100)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel"){
                        dialog, _ -> dialog.dismiss()
                }
                materialAlertDialogBuilder.create().show()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.INTERNET),100)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==100){
            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                googleSignIn()
            }else if (!ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.INTERNET)){
                val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
                materialAlertDialogBuilder.setMessage("This feature is unavailable because this feature permission that you have denied."+ "Please allow network permission from setting to proceed further")
                    .setTitle("Permission Required")
                    .setCancelable(false)
                    .setPositiveButton("Setting"){
                            dialog,msg ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package",packageName,null)
                        intent.data= uri
                        startActivity(intent)

                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel"){
                            dialog, _ ->
                        dialog.dismiss()
                    }
                    materialAlertDialogBuilder.create().show()
            }
        }
    }
}