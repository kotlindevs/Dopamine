package com.example.dopamine

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

class googleSession(var context: Context) {
    lateinit var sharedPreferences : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    var privateMode = 0

    init {
        this.sharedPreferences = context.getSharedPreferences("googleAuth",privateMode)
        this.editor = sharedPreferences.edit()
    }

    fun googleLogin(email: String,photo : String,name : String){
        editor.putBoolean("login",true)
        editor.putString("photo",photo)
        editor.putString("name",name)
        editor.putString("email",email)
        editor.commit()
    }

    fun isUserLogin() : Boolean{
        return sharedPreferences.getBoolean("login",false)
    }

    fun emailPasswordSession(Email : String , Password : String){
        editor.putBoolean("login",true)
        editor.putString("email",Email)
        editor.putString("password",Password)
        editor.commit()
    }

    fun userLogOut(){
        editor.clear()
        editor.commit()
        val i : Intent = Intent(context,MainActivity::class.java)
            .setAction(Intent.ACTION_VIEW)
            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(i)
    }
}