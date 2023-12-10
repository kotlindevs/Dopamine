package com.example.dopamine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dopamine.databinding.ActivityDopamineSearchBinding

class Dopamine_search : AppCompatActivity() {
    private lateinit var binding: ActivityDopamineSearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDopamineSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.setSelectedItemId(R.id.search)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){

                R.id.home -> {
                    startActivity(Intent(applicationContext,Dopamine_home::class.java))
                    finish()
                    true
                }

                R.id.search -> {
                    true
                }

                R.id.library -> {
                    startActivity(Intent(applicationContext,Dopamine_library::class.java))
                    finish()
                    true
                }

                else -> {

                }
            }
            false
        }
    }
}