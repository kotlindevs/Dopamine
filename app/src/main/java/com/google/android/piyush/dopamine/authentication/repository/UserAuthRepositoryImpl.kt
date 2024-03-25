package com.google.android.piyush.dopamine.authentication.repository

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.activities.DopamineHome
import com.google.android.piyush.dopamine.authentication.SignInResult
import com.google.android.piyush.dopamine.authentication.User
import com.google.android.piyush.dopamine.authentication.utilities.GoogleAuth
import com.google.android.piyush.dopamine.authentication.utilities.PhoneNumberAuth
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class UserAuthRepositoryImpl(
    private val context: Context
) : UserAuthRepository {

    private val oneTapClient: SignInClient = Identity.getSignInClient(context)
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val activity: AppCompatActivity = context as AppCompatActivity
    private var verificationId : String? = null
    private val sharedPreferences = context.getSharedPreferences("verificationId", Context.MODE_PRIVATE)
    private val auth = Firebase.auth

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(false)
            .build()
    }

    suspend fun googleSignIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        } catch(e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user
            SignInResult(
                userData = user?.run {
                    User(
                        userId = uid,
                        userName = displayName!!,
                        userEmail = email!!,
                        userImage = photoUrl?.toString()
                    )
                },
                errorMessage = null
            )
        } catch(e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
            SignInResult(
                userData = null,
                errorMessage = e.message
            )
        }
    }

    suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        } catch(e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
        }
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