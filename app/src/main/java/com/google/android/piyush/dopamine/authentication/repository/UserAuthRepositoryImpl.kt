package com.google.android.piyush.dopamine.authentication.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.piyush.dopamine.activities.DopamineHome
import com.google.android.piyush.dopamine.authentication.utilities.GoogleAuth
import com.google.android.piyush.dopamine.authentication.utilities.PhoneNumberAuth
import com.google.android.piyush.dopamine.authentication.utilities.SignInUtils
import com.google.android.piyush.dopamine.authentication.User
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class UserAuthRepositoryImpl(
    private val context: Context
) : UserAuthRepository {

    private lateinit var signInClient: GoogleSignInClient
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val activity: AppCompatActivity = context as AppCompatActivity
    private var verificationId : String? = null
    private val sharedPreferences = context.getSharedPreferences("verificationId", Context.MODE_PRIVATE)

    private val _signInResult : MutableLiveData<GoogleAuth<User>> = MutableLiveData()
    val signInResult : LiveData<GoogleAuth<User>> = _signInResult

    init {
        setUpGoogleSignInClient()
    }

    private fun setUpGoogleSignInClient() {
        val signInOptions = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken(SignInUtils.REQUEST_TOKEN)
            .requestEmail()
            .build()
        signInClient = GoogleSignIn.getClient(context, signInOptions)
        activityResultLauncher = (context as AppCompatActivity).registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ::handleSignInResult
        )
    }

    private fun handleSignInResult(result: ActivityResult) {
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if(account != null){
                    getSignUserData(account.idToken!!)
                }else{
                    _signInResult.value = GoogleAuth.Error("Account is null", null)
                }
            }catch (e : Exception){
                _signInResult.value = GoogleAuth.Error(e.message!!, null)
            }
        }else{
            _signInResult.value = GoogleAuth.Error("Sign in failed", null)
        }
    }

    private fun getSignUserData(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = firebaseAuth.currentUser
                    val userInfo = User(
                        firebaseUser!!.uid,
                        firebaseUser.displayName!!,
                        firebaseUser.email!!,
                        firebaseUser.photoUrl.toString()
                    )
                    _signInResult.value = GoogleAuth.Success(userInfo)
                } else {
                    _signInResult.value = GoogleAuth.Error(task.exception!!.message!!, null)
                }
            }
    }

    fun signInWithGoogle(){
        val signInIntent = signInClient.signInIntent
        activityResultLauncher.launch(signInIntent)
    }

    override suspend fun sendVerificationCode(phoneNumber: String): PhoneNumberAuth<Unit> {
        return try {
            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(
                    object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                            Toast.makeText(context, "Verification Completed", Toast.LENGTH_SHORT).show()
                        }

                        override fun onVerificationFailed(p0: FirebaseException) {
                            Toast.makeText(context, p0.message.toString(), Toast.LENGTH_SHORT).show()
                        }

                        override fun onCodeSent(
                            p0: String,
                            p1: PhoneAuthProvider.ForceResendingToken
                        ) {
                            super.onCodeSent(p0, p1)
                            verificationId = p0
                            sharedPreferences.edit().putString("storedVerificationId", verificationId).apply()
                            Toast.makeText(context, "Code Sent", Toast.LENGTH_SHORT).show()
                        }
                    }
                ).build()
            PhoneAuthProvider.verifyPhoneNumber(options)
            PhoneNumberAuth.success(Unit)
        }catch (e : Exception){
            PhoneNumberAuth.error(e.localizedMessage ?: "Unknown Error", null)
        }
    }

    override suspend fun verifyCode(code: String): PhoneNumberAuth<Unit> {
        return try {
            val storedVerificationId = sharedPreferences.getString("storedVerificationId", null)
            if(verificationId == null){
                verificationId = storedVerificationId
            }
            val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener (activity){
                    if(it.isSuccessful){
                        context.startActivity(Intent(context, DopamineHome::class.java))
                    }else{
                        PhoneNumberAuth.error(it.exception?.message.toString(), null)
                        Toast.makeText(context, it.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            PhoneNumberAuth.success(Unit)
        }catch (e : Exception){
            PhoneNumberAuth.error("Verification ID Null " + e.message.toString(), null)
        }
    }

    fun currentUser() : FirebaseUser? {
        return firebaseAuth.currentUser
    }
}