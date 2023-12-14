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
        data_left.add(SearchModel(R.drawable.tth,"Podcasts"))
        data_left.add(SearchModel(R.drawable.mix,"Made For \n You"))
        data_left.add(SearchModel(R.drawable.sia,"2023 in \n Music"))
        val adapter_left = SearchAdapter(applicationContext,data_left)
        recyclerViewLeft.adapter = adapter_left

        val data_right = ArrayList<SearchModel>()
        data_right.add(SearchModel(R.drawable.blade_runner,"Music"))
        data_right.add(SearchModel(R.drawable.imagine,"Live Events"))
        data_right.add(SearchModel(R.drawable.lofi,"New \n Releases"))
        data_right.add(SearchModel(R.drawable.eminem,"2023 in \n Podcasts"))
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