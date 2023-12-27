package com.example.dopamine.Gaming

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dopamine.R
import com.example.dopamine.authentication.googleSession
import com.example.dopamine.databinding.ActivityDopamineGamingBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DopamineGaming : AppCompatActivity() {
    private lateinit var binding: ActivityDopamineGamingBinding
    private lateinit var adapter: GamingAdapter
    val baseUrl = "https://api.npoint.io/3c6b399952924125b043/"
    private lateinit var googleSession: googleSession
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDopamineGamingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        googleSession = googleSession(this)

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        binding.rvGaming.layoutManager = LinearLayoutManager(this)

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GamingApi::class.java)
            .getGaming()
            .enqueue(object : Callback<List<Gaming>> {
                override fun onResponse(call: Call<List<Gaming>>, response: Response<List<Gaming>>) {
                    adapter = GamingAdapter(
                        applicationContext,
                        response.body()!!,
                        googleSession,
                    )
                    binding.rvGaming.adapter = adapter
                }

                override fun onFailure(call: Call<List<Gaming>>, t: Throwable) {
                }

            })
    }
}