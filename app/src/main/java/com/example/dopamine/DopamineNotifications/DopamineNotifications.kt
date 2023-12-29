package com.example.dopamine.DopamineNotifications

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dopamine.databinding.ActivityDopamineNotificationsBinding

class DopamineNotifications : AppCompatActivity() {
    private lateinit var binding: ActivityDopamineNotificationsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDopamineNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

    }
}