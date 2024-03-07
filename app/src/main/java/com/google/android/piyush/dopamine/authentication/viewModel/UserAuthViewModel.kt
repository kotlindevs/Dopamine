package com.google.android.piyush.dopamine.authentication.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.piyush.dopamine.authentication.utilities.GoogleAuth
import com.google.android.piyush.dopamine.authentication.utilities.PhoneNumberAuth
import com.google.android.piyush.dopamine.authentication.User
import com.google.android.piyush.dopamine.authentication.repository.UserAuthRepositoryImpl
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class UserAuthViewModel(
    private val userAuthRepositoryImpl: UserAuthRepositoryImpl
) : ViewModel() {

    private val _signInResult : LiveData<GoogleAuth<User>> = userAuthRepositoryImpl.signInResult
    val signInResult : LiveData<GoogleAuth<User>> = _signInResult

    private val _sendVerificationCodeResult : MutableLiveData<PhoneNumberAuth<Unit>> = MutableLiveData()
    val sendVerificationCodeResult  : LiveData<PhoneNumberAuth<Unit>> = _sendVerificationCodeResult

    private val _verifyOtpResult : MutableLiveData<PhoneNumberAuth<Unit>> = MutableLiveData()
    val verifyOtpResult  : LiveData<PhoneNumberAuth<Unit>> = _verifyOtpResult

    fun currentUser() : FirebaseUser? {
        return userAuthRepositoryImpl.currentUser()
    }

    fun signInWithGoogle() {
        userAuthRepositoryImpl.signInWithGoogle()
    }

    fun sendVerificationCode(phoneNumber: String) {
        viewModelScope.launch {
            val result = userAuthRepositoryImpl.sendVerificationCode(phoneNumber)
            _sendVerificationCodeResult.value = result
        }
    }

    fun verifyOtp(otp: String) {
        viewModelScope.launch {
            _verifyOtpResult.value = PhoneNumberAuth.loading(null)
            userAuthRepositoryImpl.verifyCode(otp)
        }
    }
}

class UserAuthViewModelFactory(
    private val userAuthRepositoryImpl: UserAuthRepositoryImpl
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(UserAuthViewModel::class.java)) {
            return UserAuthViewModel(userAuthRepositoryImpl) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}