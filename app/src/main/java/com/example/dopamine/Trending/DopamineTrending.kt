package com.example.dopamine.Trending

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dopamine.R
import com.example.dopamine.databinding.ActivityDopamineTrendingBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DopamineTrending : AppCompatActivity() {
    private lateinit var binding: ActivityDopamineTrendingBinding
    private lateinit var adaptor: TrendsAdaptor
    val baseUrl = "https://api.npoint.io/23ad1a516e3776ed61b5/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDopamineTrendingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener{
            finish()
        }

        binding.rvTrending.layoutManager = LinearLayoutManager(this)

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrendsApi::class.java)
            .getTrends()
            .enqueue(object : Callback<List<Trend>> {
                override fun onResponse(call: Call<List<Trend>>, response: Response<List<Trend>>) {
                    adaptor = TrendsAdaptor(applicationContext,response.body()!!)
                    binding.rvTrending.adapter = adaptor
                    //
                }

                override fun onFailure(call: Call<List<Trend>>, t: Throwable) {
                    //
                }

            })
    }
}