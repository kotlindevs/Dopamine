package com.example.dopamine.DopamineHome.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.dopamine.DopamineHome.DopamineUserProfile
import com.example.dopamine.DopamineSearch.Data
import com.example.dopamine.DopamineSearch.SearchAdapter
import com.example.dopamine.DopamineSearch.SearchApi
import com.example.dopamine.authentication.googleSession
import com.example.dopamine.databinding.FragmentSearchBinding
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Search : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var googleSession: googleSession
    private lateinit var adapter: SearchAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater,container,false)

        googleSession = context?.let { googleSession(it) }!!
        firebaseAuth = FirebaseAuth.getInstance()

        if (googleSession.sharedPreferences.getString("Mon","")!!.startsWith("+91")){
            val userPhoto = "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/default-profile-picture-grey-male-icon.png"
            Glide.with(this).load(userPhoto).into(binding.UserImage)
        } else {
            val userPhoto = googleSession.sharedPreferences.getString("photo","")
            Glide.with(this).load(userPhoto).into(binding.UserImage)
        }

        binding.UserImage.setOnClickListener {
            Toast.makeText(
                context, googleSession.sharedPreferences.getString("name", ""),
                Toast.LENGTH_SHORT
            ).show()
            startActivity(Intent(context, DopamineUserProfile::class.java))
        }

        binding.rvRightBrowse.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.npoint.io/2d750257967d1836ebc1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SearchApi::class.java)
                .browseAll()
            retrofit.enqueue(object : Callback<List<Data>>{
                override fun onResponse(call: Call<List<Data>>, response: Response<List<Data>>) {
                    binding.rvRightBrowse.adapter = SearchAdapter(context,response.body()!!)
                }

                override fun onFailure(call: Call<List<Data>>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
        }

        binding.rvLeftBrowse.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.npoint.io/bea4ee26ace677a5390d/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SearchApi::class.java)
                .browseAll()
                .enqueue(object : Callback<List<Data>> {
                    override fun onResponse(
                        call: Call<List<Data>>,
                        response: Response<List<Data>>
                    ) {
                        binding.rvLeftBrowse.adapter = SearchAdapter(context, response.body()!!)
                    }

                    override fun onFailure(call: Call<List<Data>>, t: Throwable) {
                        TODO("Not yet implemented")
                    }
                })
        }
        return binding.root
    }
}