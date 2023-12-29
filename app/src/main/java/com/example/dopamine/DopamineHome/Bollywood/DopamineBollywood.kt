package com.example.dopamine.DopamineHome.Bollywood

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dopamine.databinding.ActivityDopamineBollywoodBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DopamineBollywood : AppCompatActivity() {
    private lateinit var binding : ActivityDopamineBollywoodBinding
    private lateinit var adapter: BollywoodAdapter
    val baseUrl = "https://api.npoint.io/362bf03a7dd20cef3dce/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDopamineBollywoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        binding.rvBollywood.layoutManager = LinearLayoutManager(this)

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BollywoodApi::class.java)
            .getBollywood()
            .enqueue(object : Callback<List<Bollywood>>{
                override fun onResponse(call: Call<List<Bollywood>>, response: Response<List<Bollywood>>) {
                    adapter = BollywoodAdapter(
                        applicationContext,
                        response.body()!!
                    )
                    binding.rvBollywood.adapter = adapter
                }

                override fun onFailure(call: Call<List<Bollywood>>, t: Throwable) {
                }

            })

    }
}