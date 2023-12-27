package com.example.dopamine.Remix

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dopamine.authentication.googleSession
import com.example.dopamine.databinding.ActivityDopamineRemixBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DopamineRemix : AppCompatActivity() {
    private lateinit var binding: ActivityDopamineRemixBinding
    private lateinit var adapter: RemixAdapter
    val baseUrl = "https://api.npoint.io/12abb65f1a120508b605/"
    private lateinit var googleSession: googleSession
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDopamineRemixBinding.inflate(layoutInflater)
        setContentView(binding.root)
        googleSession = googleSession(this)

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        binding.rvRemix.layoutManager = LinearLayoutManager(this)

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RemixApi::class.java)
            .getRemix()
            .enqueue(object : Callback<List<Remix>> {
                override fun onResponse(call: Call<List<Remix>>, response: Response<List<Remix>>) {
                    adapter = RemixAdapter(
                        applicationContext,
                        response.body()!!,
                        googleSession,
                    )
                    binding.rvRemix.adapter = adapter
                }

                override fun onFailure(call: Call<List<Remix>>, t: Throwable) {
                }

            })
    }
}