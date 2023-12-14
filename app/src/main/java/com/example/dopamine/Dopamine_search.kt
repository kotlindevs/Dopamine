package com.example.dopamine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dopamine.databinding.ActivityDopamineSearchBinding

class Dopamine_search : AppCompatActivity() {
    private lateinit var binding: ActivityDopamineSearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDopamineSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Recycler View Work
        val recyclerViewLeft = findViewById<RecyclerView>(R.id.rv_left_browse)
        val recyclerViewRight = findViewById<RecyclerView>(R.id.rv_right_browse)

        recyclerViewLeft.layoutManager = LinearLayoutManager(this)
        recyclerViewRight.layoutManager = LinearLayoutManager(this)

        val data_left = ArrayList<SearchModel>()
        data_left.add(SearchModel(R.drawable.likedsongs,"Wrapped"))
        data_left.add(SearchModel(R.drawable.likedsongs,"Podcasts"))
        data_left.add(SearchModel(R.drawable.likedsongs,"Made For You"))
        data_left.add(SearchModel(R.drawable.likedsongs,"2023 in Music"))
        data_left.add(SearchModel(R.drawable.likedsongs,"Hindi"))
        data_left.add(SearchModel(R.drawable.likedsongs,"Tamil"))
        data_left.add(SearchModel(R.drawable.likedsongs,"Podcast Charts"))
        data_left.add(SearchModel(R.drawable.likedsongs,"Video Podcasts"))
        data_left.add(SearchModel(R.drawable.likedsongs,"Charts"))
        data_left.add(SearchModel(R.drawable.likedsongs,"Pop"))
        data_left.add(SearchModel(R.drawable.likedsongs,"Trending"))
        data_left.add(SearchModel(R.drawable.likedsongs,"Discover"))
        data_left.add(SearchModel(R.drawable.likedsongs,"Mood"))
        data_left.add(SearchModel(R.drawable.likedsongs,"Devotional"))
        data_left.add(SearchModel(R.drawable.likedsongs,"Hip Hop"))
        data_left.add(SearchModel(R.drawable.likedsongs,"Student"))
        data_left.add(SearchModel(R.drawable.likedsongs,"Gaming"))
        data_left.add(SearchModel(R.drawable.likedsongs,"Workout"))
        data_left.add(SearchModel(R.drawable.likedsongs,"EQUAL"))
        data_left.add(SearchModel(R.drawable.likedsongs,"Rock"))

        val adapter_left = SearchAdapter(applicationContext,data_left)
        recyclerViewLeft.adapter = adapter_left

        val data_right = ArrayList<SearchModel>()
        data_right.add(SearchModel(R.drawable.likedsongs,"Music"))
        data_right.add(SearchModel(R.drawable.likedsongs,"Live Events"))
        data_right.add(SearchModel(R.drawable.likedsongs,"New Releases"))
        data_right.add(SearchModel(R.drawable.likedsongs,"2023 in Podcasts"))
        data_right.add(SearchModel(R.drawable.likedsongs,"Hindi"))
        data_right.add(SearchModel(R.drawable.likedsongs,"Tamil"))
        data_right.add(SearchModel(R.drawable.likedsongs,"Podcast Charts"))
        data_right.add(SearchModel(R.drawable.likedsongs,"Video Podcasts"))
        data_right.add(SearchModel(R.drawable.likedsongs,"Charts"))
        data_right.add(SearchModel(R.drawable.likedsongs,"Pop"))
        data_right.add(SearchModel(R.drawable.likedsongs,"Trending"))
        data_right.add(SearchModel(R.drawable.likedsongs,"Discover"))
        data_right.add(SearchModel(R.drawable.likedsongs,"Mood"))
        data_right.add(SearchModel(R.drawable.likedsongs,"Devotional"))
        data_right.add(SearchModel(R.drawable.likedsongs,"Hip Hop"))
        data_right.add(SearchModel(R.drawable.likedsongs,"Student"))
        data_right.add(SearchModel(R.drawable.likedsongs,"Gaming"))
        data_right.add(SearchModel(R.drawable.likedsongs,"Workout"))
        data_right.add(SearchModel(R.drawable.likedsongs,"EQUAL"))
        data_right.add(SearchModel(R.drawable.likedsongs,"Rock"))
        val adapter_right = SearchAdapter(applicationContext,data_right)
        recyclerViewRight.adapter = adapter_right

        binding.bottomNavigationView.setSelectedItemId(R.id.search)

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
    }
}