package com.example.dopamine.DopamineHome.Phonk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dopamine.authentication.googleSession
import com.example.dopamine.databinding.ActivityDopaminePhonkBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DopaminePhonk : AppCompatActivity() {
    private lateinit var binding: ActivityDopaminePhonkBinding
    private lateinit var adapter: PhonkAdapter
    val baseUrl = "https://api.npoint.io/cea292aae5d0b392abdc/"
    private lateinit var googleSession: googleSession
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDopaminePhonkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        googleSession = googleSession(this)

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        binding.rvPhonk.layoutManager = LinearLayoutManager(this)

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PhonkApi::class.java)
            .getPhonk()
            .enqueue(object : Callback<List<Phonk>> {
                override fun onResponse(call: Call<List<Phonk>>, response: Response<List<Phonk>>) {
                    adapter = PhonkAdapter(
                        applicationContext,
                        response.body()!!,
                        googleSession,
                    )
                    binding.rvPhonk.adapter = adapter
                }

                override fun onFailure(call: Call<List<Phonk>>, t: Throwable) {
                }

            })
    }
}