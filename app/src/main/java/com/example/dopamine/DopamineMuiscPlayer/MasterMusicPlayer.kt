package com.example.dopamine.DopamineMuiscPlayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dopamine.R
import com.example.dopamine.databinding.ActivityDopamineTracksBinding
import com.example.dopamine.databinding.ActivityMasterMusicPlayerBinding

class MasterMusicPlayer : AppCompatActivity() {
    private lateinit var binding: ActivityMasterMusicPlayerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMasterMusicPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.blankTitle.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus){
                binding.blankTitle.isSelected = true
            }
        }
    }
}