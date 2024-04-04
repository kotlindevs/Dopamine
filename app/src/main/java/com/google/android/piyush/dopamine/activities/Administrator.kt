package com.google.android.piyush.dopamine.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.databinding.ActivityAdministratorBinding
import com.google.android.piyush.youtube.utilities.AdminViewModel
import com.google.android.piyush.youtube.utilities.YoutubeResource

class Administrator : AppCompatActivity() {

    private lateinit var binding: ActivityAdministratorBinding
    private val viewModel by viewModels<AdminViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdministratorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.continueWithAdminId.setOnClickListener {
            viewModel.admin.observe(this) { admin ->
                when (admin) {
                    is YoutubeResource.Loading -> {}
                    is YoutubeResource.Success -> {
                        val adminId = binding.adminId.text.toString()
                        if (adminId.isEmpty()) {
                            binding.textInputLayout.error = "adminId is required"
                            binding.textInputLayout.isErrorEnabled = true
                        } else {
                            binding.textInputLayout.isErrorEnabled = false
                            if (admin.data.adminId == adminId) {
                                Snackbar.make(
                                    binding.root,
                                    "Welcome ${admin.data.adminName} !",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                startActivity(
                                    Intent(
                                        this,AdminProfiler::class.java
                                    ).putExtra("adminId",adminId).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                                binding.adminId.clearFocus()
                                binding.adminId.text?.clear()
                            } else {
                                binding.textInputLayout.error = "adminId is incorrect"
                                binding.textInputLayout.isErrorEnabled = true
                            }
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