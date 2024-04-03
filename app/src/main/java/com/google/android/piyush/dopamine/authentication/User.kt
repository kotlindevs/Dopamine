package com.google.android.piyush.dopamine.authentication

data class SignInResult(
    val userData : User?,
    val errorMessage : String?
)

data class User(
    val userId : String? = null,
    val userName : String? = null,
    val userEmail : String? = null,
    val userImage : String? = null
)

data class SignInState(
    val isSignInSuccessful : Boolean = false,
    val signInError : String? = null
)