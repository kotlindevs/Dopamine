package com.example.dopamine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dopamine.databinding.ActivityDopamineLibraryBinding


class Dopamine_library : AppCompatActivity() {
    private lateinit var binding: ActivityDopamineLibraryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDopamineLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.setSelectedItemId(R.id.library)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){

                R.id.home -> {
                    startActivity(Intent(applicationContext,Dopamine_home::class.java))
                    finish()
                    true
                }

                R.id.search -> {
                    startActivity(Intent(applicationContext,Dopamine_search::class.java))
                    finish()
                    true
                }

                R.id.library -> {

                    true
                }

                else -> {

                }
            }
            false
        }
    }
}