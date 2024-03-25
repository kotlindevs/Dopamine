package com.google.android.piyush.dopamine.authentication

data class SignInResult(
    val userData : User?,
    val errorMessage : String?
)

data class User(
    val userId : String,
    val userName : String,
    val userEmail : String,
    val userImage : String?
)

data class SignInState(
    val isSignInSuccessful : Boolean = false,
    val signInError : String? = null
)