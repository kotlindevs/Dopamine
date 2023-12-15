package com.example.dopamine.DopamineMuiscPlayer

import android.content.Context
import android.content.SharedPreferences
import com.example.dopamine.TracksList.TracksDataClass.Track
import java.text.FieldPosition

class musicSession(var context: Context) {
    lateinit var sharedPreferences : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    var privateMode = 0

    init {
        this.sharedPreferences = context.getSharedPreferences("musicSession",privateMode)
        this.editor = sharedPreferences.edit()
    }

    fun setMusicPlayer(id : String,artistName : String,songName : String,imgUrl : String,mp3Preview : String,type : String,songDate : String,isPlayable : Boolean){
        editor.putBoolean("is_playable",isPlayable)
            .putString("id",id)
            .putString("artist_name",artistName)
            .putString("song_name",songName)
            .putString("image_url",imgUrl)
            .putString("mp3_preview",mp3Preview)
            .putString("type",type)
            .putString("date",songDate)
            .commit()
    }

    fun currentPos(position: Int){
        editor.putBoolean("is_running",true).putInt("position",position).commit()
    }

    fun isSongRunning() : Boolean{
        return sharedPreferences.getBoolean("is_running",false)
    }
    fun isPlaying() : Boolean{
        return sharedPreferences.getBoolean("is_playable",false)
    }
}