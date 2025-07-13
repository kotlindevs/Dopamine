package com.google.android.piyush.dopamine.authentication.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.piyush.dopamine.authentication.SignInResult
import com.google.android.piyush.dopamine.authentication.SignInState
import com.google.android.piyush.dopamine.authentication.repository.UserAuthRepositoryImpl
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UserAuthViewModel(
    private val userAuthRepositoryImpl: UserAuthRepositoryImpl
) : ViewModel() {

    private val _state : MutableStateFlow<SignInState> = MutableStateFlow(
        SignInState()
    )
    val state = _state.asStateFlow()

    fun currentUser() : FirebaseUser? {
        return userAuthRepositoryImpl.currentUser()
    }

    fun onSignInResult(result : SignInResult) {
        _state.update {
            it.copy(
                isSignInSuccessful = result.userData != null,
                signInError = result.errorMessage
            )
        }
    }

    fun resetSignInState() {
        _state.update { SignInState() }
    }
}

class UserAuthViewModelFactory(
    private val userAuthRepositoryImpl: UserAuthRepositoryImpl
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(UserAuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserAuthViewModel(userAuthRepositoryImpl) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}