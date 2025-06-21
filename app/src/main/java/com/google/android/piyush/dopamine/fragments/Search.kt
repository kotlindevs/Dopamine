package com.google.android.piyush.dopamine.fragments

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.adapters.SearchSuggestionAdapter
import com.google.android.piyush.dopamine.adapters.YoutubePlayerVideosAdapter
import com.google.android.piyush.dopamine.databinding.FragmentSearchBinding
import com.google.android.piyush.youtube.model.SearchResponse
import com.google.android.piyush.youtube.model.SearchResponse.Contents.TwoColumnSearchResultsRenderer.PrimaryContents.SectionListRenderer.Content.ItemSectionRenderer.Content.VideoRenderer
import com.google.android.piyush.youtube.utilities.YoutubeResponse
import com.google.android.piyush.youtube.viewModels.YoutubeViewModel

class Search : Fragment() {

    private var binding: FragmentSearchBinding? = null
    private val viewModel : YoutubeViewModel by activityViewModels<YoutubeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSearchBinding.bind(view)
        binding?.apply {
            searchView.editText.doAfterTextChanged { query ->
                viewModel.searchSuggestions(query = query.toString())
            }

            searchView.editText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                override fun onEditorAction(
                    v: TextView?,
                    actionId: Int,
                    event: KeyEvent?
                ): Boolean {
                    return true
                }
            })
        }

        viewModel.searchSuggestions.observe(viewLifecycleOwner) { response ->
            when(response){
                is YoutubeResponse.Loading -> {}
                is YoutubeResponse.Success -> {
                    val results = response.data.refinements
                    if (results != null) {
                        binding?.apply {
                            searchSuggestionText.apply {
                                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                                adapter = SearchSuggestionAdapter(
                                    results,
                                    onSuggestionClick = { search ->
                                        binding?.searchBar?.setText(search)
                                        viewModel.searchData(search)
                                    }
                                )
                            }
                        }
                    }
                }
                is YoutubeResponse.Error -> {
                    Log.d("Search", response.exception.message.toString())
                }
            }
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { response ->
            val videosList = mutableListOf<VideoRenderer>()

            when(response){
                is YoutubeResponse.Loading -> {}
                is YoutubeResponse.Success -> {
                    val results = response.data
                    results.contents?.twoColumnSearchResultsRenderer?.primaryContents?.sectionListRenderer?.contents?.forEach { contents ->
                        contents.itemSectionRenderer?.contents?.forEach { content -> val videos = content.videoRenderer
                            if (videos != null) {
                                videosList.add(videos)
                            }
                        }
                    }
                }
                is YoutubeResponse.Error -> {
                    Log.d("Search", response.exception.message.toString())
                }
            }

            binding.apply {
                this?.searchResults.apply {
                    this?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    this?.adapter = YoutubePlayerVideosAdapter(videosList)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}