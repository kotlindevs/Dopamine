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
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.databinding.ActivityAdminProfilerBinding
import com.google.android.piyush.youtube.utilities.AdminViewModel
import com.google.android.piyush.youtube.utilities.YoutubeResource

class AdminProfiler : AppCompatActivity() {

    private lateinit var binding: ActivityAdminProfilerBinding
    private val viewModel by viewModels<AdminViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminProfilerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val adminId= intent.getStringExtra("adminId")
        if(!adminId.isNullOrEmpty()){
            viewModel.admin.observe(this) { admin ->
                when (admin) {
                    is YoutubeResource.Loading -> {
                        binding.findUserImage.visibility = View.VISIBLE
                        binding.findUserText.visibility = View.VISIBLE
                        binding.findUserDescription.visibility = View.VISIBLE
                    }
                    is YoutubeResource.Success -> {
                        val adminData = admin.data
                        if (adminId.isEmpty()) {
                            binding.textInputLayoutAdminId.error = "adminId is required"
                            binding.textInputLayoutAdminId.isErrorEnabled = true
                        } else {
                            binding.textInputLayoutAdminId.isErrorEnabled = false
                            if (admin.data.adminId == adminId) {
                                binding.apply {
                                    findUserImage.visibility = View.GONE
                                    findUserText.visibility = View.GONE
                                    findUserDescription.visibility = View.GONE
                                    adminImageIc.visibility = View.VISIBLE
                                    adminProfile.visibility = View.VISIBLE
                                    adminLoginDetails.visibility = View.VISIBLE
                                    this.adminId.setText(adminData.adminId)
                                    this.adminId.isEnabled = false
                                    this.adminName.text = adminData.adminName
                                    this.adminEmail.text = adminData.adminEmail
                                    Glide.with(applicationContext).load(adminData.adminImage).into(this.adminImage)
                                    this.adminDescription.text = adminData.adminDescription?.random()?.bio
                                }
                                binding.adminLogin.apply {
                                    visibility = View.VISIBLE
                                    setOnClickListener {
                                        val password = binding.adminPasswd.text.toString()
                                        if(password.isEmpty()){
                                            binding.textInputLayoutPasswd.isErrorEnabled = true
                                            binding.textInputLayoutPasswd.error = "Password is required"
                                        }else{
                                            binding.textInputLayoutPasswd.isErrorEnabled = false
                                            if(adminData.adminPasswd == password){
                                                Toast.makeText(this@AdminProfiler, "Login Successful", Toast.LENGTH_SHORT).show()
                                                startActivity(
                                                    Intent(
                                                        this@AdminProfiler, AdminHome::class.java
                                                    ).putExtra("adminId", adminId).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                )
                                                binding.adminPasswd.text?.clear()
                                            }else{
                                                Snackbar.make(binding.root, "Password is incorrect", Snackbar.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            } else {
                                binding.textInputLayoutAdminId.error = "adminId is incorrect"
                                binding.textInputLayoutAdminId.isErrorEnabled = true
                            }
                        }
                    }

                    is YoutubeResource.Error -> {
                        Snackbar.make(binding.root, admin.exception.message.toString(), Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }else{
            binding.findUserImage.visibility = View.VISIBLE
            binding.findUserText.visibility = View.VISIBLE
            binding.findUserText.text = getText(R.string.something_went_wrong)
            binding.findUserDescription.visibility = View.VISIBLE
            binding.findUserDescription.text = getText(R.string.try_again_later)
        }
    }
}