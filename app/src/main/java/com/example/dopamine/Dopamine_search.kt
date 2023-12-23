package com.example.dopamine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dopamine.authentication.googleSession
import com.example.dopamine.databinding.ActivityDopamineSearchBinding
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Dopamine_search : AppCompatActivity() {
    private lateinit var binding: ActivityDopamineSearchBinding
    private lateinit var googleSession: googleSession
    private lateinit var adapter: SearchAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDopamineSearchBinding.inflate(layoutInflater)
        googleSession = googleSession(this)
        firebaseAuth = FirebaseAuth.getInstance()
        setContentView(binding.root)

        binding.rvLeftBrowse.layoutManager = LinearLayoutManager(this)
        binding.rvRightBrowse.layoutManager = LinearLayoutManager(this)
        binding.bottomNavigationView.setSelectedItemId(R.id.search)

        //Photo Fetching
        if (googleSession.sharedPreferences.getString("Mon","")!!.startsWith("+91")){
            val userPhoto = "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/default-profile-picture-grey-male-icon.png"
            Glide.with(this).load(userPhoto).into(binding.UserImage)
        } else {
            val userPhoto = googleSession.sharedPreferences.getString("photo","")
            Glide.with(this).load(userPhoto).into(binding.UserImage)
        }

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

        binding.UserImage.setOnClickListener {
            Toast.makeText(applicationContext,googleSession.sharedPreferences.getString("name",""),
                Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,DopamineUserProfile::class.java))
        }

        Retrofit.Builder()
            .baseUrl("https://api.npoint.io/2d750257967d1836ebc1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SearchApi::class.java)
            .browseAll()
            .enqueue(object : Callback<List<Data>> {
                override fun onResponse(call: Call<List<Data>>, response: Response<List<Data>>) {
                    adapter = SearchAdapter(applicationContext,response.body()!!)
                    binding.rvLeftBrowse.adapter = adapter
                }

                override fun onFailure(call: Call<List<Data>>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })

        Retrofit.Builder()
            .baseUrl("https://api.npoint.io/bea4ee26ace677a5390d/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SearchApi::class.java)
            .browseAll()
            .enqueue(object : Callback<List<Data>> {
                override fun onResponse(call: Call<List<Data>>, response: Response<List<Data>>) {
                    adapter = SearchAdapter(applicationContext,response.body()!!)
                    binding.rvRightBrowse.adapter = adapter
                }

                override fun onFailure(call: Call<List<Data>>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
    }
}