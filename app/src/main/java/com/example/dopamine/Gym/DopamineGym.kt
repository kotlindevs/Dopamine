package com.example.dopamine.Gym

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dopamine.R
import com.example.dopamine.authentication.googleSession
import com.example.dopamine.databinding.ActivityDopamineGymBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DopamineGym : AppCompatActivity() {
    private lateinit var binding: ActivityDopamineGymBinding
    private lateinit var adapter: GymAdapter
    val baseUrl = "https://api.npoint.io/2ffcaa6e10a2152f101b/"
    private lateinit var googleSession: googleSession
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDopamineGymBinding.inflate(layoutInflater)
        setContentView(binding.root)

        googleSession = googleSession(this)

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        binding.rvGym.layoutManager = LinearLayoutManager(this)

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GymApi::class.java)
            .getGym()
            .enqueue(object : Callback<List<Gym>> {
                override fun onResponse(call: Call<List<Gym>>, response: Response<List<Gym>>) {
                    adapter = GymAdapter(
                        applicationContext,
                        response.body()!!,
                        googleSession,
                    )
                    binding.rvGym.adapter = adapter
                }

                override fun onFailure(call: Call<List<Gym>>, t: Throwable) {
                }

            })
    }
}