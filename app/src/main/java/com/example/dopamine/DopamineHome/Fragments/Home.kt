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
import com.example.dopamine.DopamineHome.Artist.Adapter.ArtistAdapter
import com.example.dopamine.DopamineHome.Artist.ArtistData.Artist
import com.example.dopamine.DopamineHome.Artist.ArtistList.ArtistInterface
import com.example.dopamine.DopamineHome.DopamineUserProfile
import com.example.dopamine.DopamineHome.IndiaBest.IndiaBest
import com.example.dopamine.DopamineHome.IndiaBest.IndiaBestAdapter
import com.example.dopamine.DopamineHome.IndiaBest.IndiaBestApi
import com.example.dopamine.DopamineHome.ItemsViewModel
import com.example.dopamine.DopamineHome.MusicAdapter
import com.example.dopamine.DopamineHome.OldButGold.Chart
import com.example.dopamine.DopamineHome.OldButGold.ChartsApi
import com.example.dopamine.DopamineHome.OldButGold.MusicChartAdapter
import com.example.dopamine.DopamineHome.TopWeek.TopWeek
import com.example.dopamine.DopamineHome.TopWeek.TopWeekAdapter
import com.example.dopamine.DopamineHome.TopWeek.TopWeekApi
import com.example.dopamine.DopamineNotifications.DopamineNotifications
import com.example.dopamine.DopamineSettings.DopamineSettings
import com.example.dopamine.R
import com.example.dopamine.authentication.googleSession
import com.example.dopamine.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar


class Home : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var googleSession: googleSession
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        googleSession = context?.let { googleSession(it) }!!
        firebaseAuth = FirebaseAuth.getInstance()

        binding.leftBar.apply {
            this.layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }

        binding.rightBar.apply {
            this.layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }

        binding.musicArtist.apply {
            this.layoutManager = LinearLayoutManager(context,binding.musicArtist.horizontalFadingEdgeLength,false)
            setHasFixedSize(true)
            val retrofit =  Retrofit.Builder()
                .baseUrl("https://api.npoint.io/a3ea088449c3a010fb5d/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ArtistInterface::class.java)
                .getArtist()
            retrofit.enqueue(object : Callback<List<Artist>> {
                override fun onResponse(
                    call: Call<List<Artist>>,
                    response: Response<List<Artist>>
                ) {
                    binding.musicArtist.adapter =  ArtistAdapter(context,response.body())
                }

                override fun onFailure(call: Call<List<Artist>>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
        }

        binding.musicChart.apply {
            this.layoutManager = LinearLayoutManager(context,binding.musicArtist.horizontalFadingEdgeLength,false)
            setHasFixedSize(true)

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.npoint.io/504ec4f9cb720cbeb8df/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ChartsApi::class.java)
                .getCharts()
            retrofit.enqueue(object: Callback<List<Chart>>{
                override fun onResponse(call: Call<List<Chart>>, response: Response<List<Chart>>) {
                   binding.musicChart.adapter = MusicChartAdapter(context,response.body())
                }

                override fun onFailure(call: Call<List<Chart>>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
        }

        binding.topHitsThisWeek.apply {
            this.layoutManager = LinearLayoutManager(context,binding.topHitsThisWeek.horizontalFadingEdgeLength,false)
            setHasFixedSize(true)

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.npoint.io/bd952edd473e435304b3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TopWeekApi::class.java)
                .getTopWeek()
            retrofit.enqueue(object: Callback<List<TopWeek>>{
                override fun onResponse(call: Call<List<TopWeek>>, response: Response<List<TopWeek>>) {
                    binding.topHitsThisWeek.adapter = TopWeekAdapter(context,response.body())
                }

                override fun onFailure(call: Call<List<TopWeek>>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
        }

        binding.indiaBest.apply {
            this.layoutManager = LinearLayoutManager(context,binding.indiaBest.horizontalFadingEdgeLength,false)
            setHasFixedSize(true)

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.npoint.io/680948fdb94d0f461888/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(IndiaBestApi::class.java)
                .getIndiaBest()
            retrofit.enqueue(object: Callback<List<IndiaBest>>{
                override fun onResponse(call: Call<List<IndiaBest>>, response: Response<List<IndiaBest>>) {
                    binding.indiaBest.adapter = IndiaBestAdapter(context,response.body())
                }

                override fun onFailure(call: Call<List<IndiaBest>>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
        }

        val data_left = ArrayList<ItemsViewModel>()
        data_left.add(ItemsViewModel(R.drawable.dopamine,"Dopamine's Picks"))
        data_left.add(ItemsViewModel(R.drawable.bollywood,"Bollywood"))
        data_left.add(ItemsViewModel(R.drawable.phonk,"Phonk"))
        data_left.add(ItemsViewModel(R.drawable.gaming,"Gaming and chill"))
        val adapter_left = MusicAdapter(requireContext(),data_left)
        binding.leftBar.adapter = adapter_left

        val data_right = ArrayList<ItemsViewModel>()
        data_right.add(ItemsViewModel(R.drawable.trending,"Trending"))
        data_right.add(ItemsViewModel(R.drawable.travel,"Travelling"))
        data_right.add(ItemsViewModel(R.drawable.remix,"Remix"))
        data_right.add(ItemsViewModel(R.drawable.gym,"Gym & Workout"))
        val adapter_right = MusicAdapter(requireContext(),data_right)
        binding.rightBar.adapter = adapter_right

        val fullTime  = Calendar.getInstance().time
        val time = fullTime.hours
        if(time in 6..11){
            binding.greeting.text = "Good Morning"
        }else if(time in 12..15){
            binding.greeting.text = "Good Afternoon"
        }else if (time in 16..19){
            binding.greeting.text = "Good Evening"
        }else{
            binding.greeting.text = "Good Night"
        }


        if(googleSession.sharedPreferences.getString("Mon","")!!.startsWith("+91")){
            val userPhoto = "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/default-profile-picture-grey-male-icon.png"
            Glide.with(this).load(userPhoto).into(binding.UserImage)
        }else{
            val userPhoto = googleSession.sharedPreferences.getString("photo","")
            Glide.with(this).load(userPhoto).into(binding.UserImage)
        }

        binding.settings.setOnClickListener {
            startActivity(Intent(context, DopamineSettings::class.java))
        }

        binding.notifications.setOnClickListener {
            startActivity(Intent(context, DopamineNotifications::class.java))
        }

        binding.UserImage.setOnClickListener {
            Toast.makeText(context, googleSession.sharedPreferences.getString("name",""),Toast.LENGTH_SHORT).show()
            startActivity(Intent(context, DopamineUserProfile::class.java))
        }

        return binding.root
    }
}