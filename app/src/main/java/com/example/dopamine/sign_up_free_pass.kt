package com.example.dopamine

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.dopamine.databinding.ActivitySignUpFreePassBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class sign_up_free_pass : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpFreePassBinding
    private lateinit var pass : EditText
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSession: googleSession
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpFreePassBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        googleSession = googleSession(this)
        setContentView(binding.root)

        pass = findViewById(R.id.user_pass)

        binding.topAppBar.setOnClickListener {
            finish()
        }
        onBackPressedDispatcher.addCallback {}

        binding.btnNext.setOnClickListener {
            if (pass.length()==0){
                Toast.makeText(this,"Password Can't Be Empty",Toast.LENGTH_SHORT).show()
            } else if (pass.length() < 8 && pass.length() > 8){
                Toast.makeText(this,"Password Must Be 8 Characters",Toast.LENGTH_SHORT).show()
            } else {
                val email = intent.getStringExtra("SignupEmail")
                val passwd = pass.text.toString()
                if(email!=null){
                    firebaseAuth.createUserWithEmailAndPassword(email,passwd)
                        .addOnCompleteListener {
                            if(it.isComplete){
                                val userId = databaseReference.push().key!!
                                databaseReference
                                    .child(userId)
                                    .setValue(UserModel(email))
                                    .addOnCompleteListener {
                                        Toast.makeText(applicationContext,"Successfully Signup !",Toast.LENGTH_LONG).show()
                                    }
                                googleSession.emailPasswordSession(email,passwd)
                                startActivity(Intent(this,Dopamine_home::class.java))
                            }else{
                                Toast.makeText(applicationContext,it.exception.toString(),Toast.LENGTH_LONG).show()
                            }
                        }
                }
            }
        }
    }
}