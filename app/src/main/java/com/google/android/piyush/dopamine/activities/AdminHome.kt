package com.google.android.piyush.dopamine.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.databinding.ActivityAdminHomeBinding
import com.google.android.piyush.youtube.utilities.AdminViewModel
import com.google.android.piyush.youtube.utilities.YoutubeResource

class AdminHome : AppCompatActivity() {

    private lateinit var binding: ActivityAdminHomeBinding
    private val viewModel by viewModels<AdminViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val adminId= intent.getStringExtra("adminId")

        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }

        binding.navigationView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.notification -> {
                    it.isChecked = true
                    binding.drawerLayout.close()
                    startActivity(
                        Intent(
                            this,
                            SendNotifications::class.java
                        )
                    )
                    true
                }
                else -> false
            }
        }

        if(!adminId.isNullOrEmpty()){
            viewModel.admin.observe(this) { admin ->
                when (admin) {
                    is YoutubeResource.Loading -> {}
                    is YoutubeResource.Success -> {
                        val adminData = admin.data
                        Glide.with(this)
                            .load(adminData.adminImage).into(binding.drawerLayout.findViewById(R.id.adminImage))
                        binding.drawerLayout.findViewById<MaterialTextView>(R.id.adminName).apply {
                            text = adminData.adminName
                        }
                        binding.drawerLayout.findViewById<MaterialTextView>(R.id.adminEmail).apply {
                            text = adminData.adminEmail
                        }
                    }

                    is YoutubeResource.Error -> {
                        Snackbar.make(binding.root, admin.exception.message.toString(), Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}