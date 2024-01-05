package com.example.dopamine.DopamineHome

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.dopamine.DopamineHome.Fragments.Home
import com.example.dopamine.DopamineHome.Fragments.Library
import com.example.dopamine.DopamineHome.Fragments.Search
import com.example.dopamine.R
import com.example.dopamine.databinding.ActivityDopamineHomeBinding


class Dopamine_home : AppCompatActivity() {
    private lateinit var binding: ActivityDopamineHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDopamineHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        defaultScreen(Home())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){

                R.id.home -> {
                    defaultScreen(Home())
                    true
                }

                R.id.search -> {
                    defaultScreen(Search())
                    true
                }

                R.id.library -> {
                    defaultScreen(Library())
                    true
                }

                else -> {
                    false
                }
            }
        }
    }
    private fun defaultScreen(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
    }
}