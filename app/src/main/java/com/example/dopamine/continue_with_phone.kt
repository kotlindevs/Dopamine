package com.example.dopamine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import com.example.dopamine.databinding.ActivityContinueWithPhoneBinding
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class continue_with_phone : AppCompatActivity() {

    private lateinit var binding: ActivityContinueWithPhoneBinding
    private lateinit var materialSpinner: MaterialAutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityContinueWithPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}