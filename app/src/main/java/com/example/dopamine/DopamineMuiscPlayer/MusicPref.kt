package com.example.dopamine.DopamineMuiscPlayer

import android.content.Context
import android.content.SharedPreferences

class MusicPref(var context: Context) {
    lateinit var sharedPreferences : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    var  mode : Int = 0

    init{
        this.sharedPreferences = context.getSharedPreferences("TracksPref",mode)
        this.editor = sharedPreferences.edit()
    }

    fun playTrack(id : String){
        editor.putBoolean("play",true)
            .putString("id",id)
            .commit()
    }

    fun isTrackPlayable() : Boolean{
        return sharedPreferences.getBoolean("play",false)
    }
}