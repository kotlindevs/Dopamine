package com.example.dopamine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import com.example.dopamine.databinding.ActivitySignUpFreeBinding
import com.example.dopamine.databinding.ActivitySignUpFreePassBinding

class sign_up_free_pass : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpFreePassBinding
    private lateinit var pass : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpFreePassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pass = findViewById(R.id.user_pass)

        binding.topAppBar.setOnClickListener {
            finish()
        }
        onBackPressedDispatcher.addCallback {}

        binding.btnNext.setOnClickListener {
            if (pass.length()==0){
                Toast.makeText(this,"Password Can't Be Empty",Toast.LENGTH_SHORT).show()
            } else if (pass.length() < 8){
                Toast.makeText(this,"Password Must Be 8 Characters",Toast.LENGTH_SHORT).show()
            } else {
                //startActivity(Intent())
            }
        }
    }
}