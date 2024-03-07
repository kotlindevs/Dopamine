package com.google.android.piyush.dopamine.authentication.repository

import com.google.android.piyush.dopamine.authentication.utilities.PhoneNumberAuth

interface UserAuthRepository {
    suspend fun sendVerificationCode(phoneNumber: String) : PhoneNumberAuth<Unit>
    suspend fun verifyCode(code: String) : PhoneNumberAuth<Unit>
}