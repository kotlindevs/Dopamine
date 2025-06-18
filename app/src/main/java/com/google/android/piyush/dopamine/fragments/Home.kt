package com.google.android.piyush.dopamine.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.adapters.HomeAdapter
import com.google.android.piyush.dopamine.databinding.FragmentHomeBinding
import com.google.android.piyush.youtube.utilities.YoutubeResponse
import com.google.android.piyush.youtube.viewModels.YoutubeViewModel

class Home : Fragment() {

    private var binding : FragmentHomeBinding? = null
    private val viewModel : YoutubeViewModel by viewModels<YoutubeViewModel>()
    private lateinit var adapter : HomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)

        viewModel.trendingVideos.observe(viewLifecycleOwner) { response ->
            when(response) {
                is YoutubeResponse.Loading -> {
                    Log.d("Home", "Loading")
                }
                is YoutubeResponse.Success -> {
                    Log.i("Home", "Success : ${response.data}")

                    val tabs = response.data.contents?.twoColumnBrowseResultsRenderer?.tabs?.get(0)?.tabRenderer?.content?.sectionListRenderer?.contents?.get(0)?.itemSectionRenderer?.contents?.get(0)?.shelfRenderer?.content?.expandedShelfContentsRenderer?.items
                    val videos = tabs?.map { it.videoRenderer }
                    Log.i("Home","Videos : $videos")
                }
                is YoutubeResponse.Error -> {
                    Log.e("Home","Error : ${response.exception}")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}