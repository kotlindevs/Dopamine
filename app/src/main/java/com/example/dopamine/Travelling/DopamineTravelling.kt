package com.example.dopamine.Travelling

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dopamine.Bollywood.Bollywood
import com.example.dopamine.Bollywood.BollywoodAdapter
import com.example.dopamine.Bollywood.BollywoodApi
import com.example.dopamine.authentication.googleSession
import com.example.dopamine.databinding.ActivityDopamineTravellingBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DopamineTravelling : AppCompatActivity() {
    private lateinit var binding : ActivityDopamineTravellingBinding
    private lateinit var adapter: BollywoodAdapter
    val baseUrl = "https://api.npoint.io/2a0dd0282835656afdcd/"
    private lateinit var googleSession: googleSession
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDopamineTravellingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        googleSession = googleSession(this)

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        binding.rvTravelling.layoutManager = LinearLayoutManager(this)

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BollywoodApi::class.java)
            .getBollywood()
            .enqueue(object : Callback<List<Bollywood>> {
                override fun onResponse(call: Call<List<Bollywood>>, response: Response<List<Bollywood>>) {
                    adapter = BollywoodAdapter(
                        applicationContext,
                        response.body()!!,
                        googleSession,
                    )
                    binding.rvTravelling.adapter = adapter
                }

                override fun onFailure(call: Call<List<Bollywood>>, t: Throwable) {
                }

            })
    }
}