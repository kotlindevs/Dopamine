package com.google.android.piyush.dopamine.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.piyush.database.DopamineDao
import com.google.android.piyush.dopamine.DopamineDbViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.YoutubeViewModel
import com.google.android.piyush.dopamine.adapters.SearchSuggestionAdapter
import com.google.android.piyush.dopamine.adapters.YoutubePlayerVideosAdapter
import com.google.android.piyush.dopamine.databinding.FragmentSearchBinding
import com.google.android.piyush.youtube.model.SearchResponse.Contents.TwoColumnSearchResultsRenderer.PrimaryContents.SectionListRenderer.Content.ItemSectionRenderer.Content.VideoRenderer
import com.google.android.piyush.youtube.utilities.Response
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class Explore : Fragment() {

    private var binding: FragmentSearchBinding? = null
    private val viewModel : YoutubeViewModel by viewModels<YoutubeViewModel>()
    private val database : DopamineDbViewModel by viewModels<DopamineDbViewModel>()

    @Inject
    lateinit var dao: DopamineDao

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
            searchBar.setOnClickListener {
                searchView.show()
            }
            searchView.editText.doAfterTextChanged { query ->
                viewModel.searchSuggestions(query = query.toString())
            }

            searchView.editText.doOnTextChanged {
                    text, start, before, count ->
                if(searchView.editText.text.toString().isEmpty()){
                    searchSuggestionText.visibility = View.GONE
                }else{
                    searchSuggestionText.visibility = View.VISIBLE
                }
            }
        }

        database.loadRecentSearch().run {
            database.recentSearch.observe(viewLifecycleOwner){ response ->
                when(response){
                    is Response.Loading -> {}
                    is Response.Success -> {
                        val results = response.data
                        Log.i("Recent Search => ", results.toString())
                    }
                    is Response.Error -> {
                        Log.e("Error => ", response.exception.message.toString())
                    }
                }
            }
        }

        viewModel.searchSuggestions.observe(viewLifecycleOwner) { response ->
            when(response){
                is Response.Loading -> {}
                is Response.Success -> {
                    val results = response.data.refinements
                    if (results != null) {
                        binding?.apply {
                            searchSuggestionText.apply {
                                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                                adapter = SearchSuggestionAdapter(
                                    results,
                                    onSuggestionClick = { search ->
                                        if(search.isNotEmpty()){
                                            database.addSearchKeyword(
                                                keyword = search,
                                                timestamp = System.currentTimeMillis()
                                            )
                                            viewModel.searchData(search)
                                            searchView.clearText()
                                            searchView.clearFocusAndHideKeyboard()
                                            searchView.hide()
                                        }
                                    }
                                )
                            }
                        }
                    }else {
                        Log.d("Search", "Results getting null !")
                    }
                    Log.d("Search", results.toString())
                }
                is Response.Error -> {
                    Log.d("Search", response.exception.message.toString())
                }
            }
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { response ->
            val videosList = mutableListOf<VideoRenderer>()

            when(response){
                is Response.Loading -> {}
                is Response.Success -> {
                    val results = response.data
                    results.contents?.twoColumnSearchResultsRenderer?.primaryContents?.sectionListRenderer?.contents?.forEach { contents ->
                        contents.itemSectionRenderer?.contents?.forEach { content -> val videos = content.videoRenderer
                            if (videos != null) {
                                videosList.add(videos)
                            }
                        }
                    }
                }
                is Response.Error -> {
                    Log.d("Search", response.exception.message.toString())
                }
            }

            binding?.searchResults?.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = YoutubePlayerVideosAdapter(videosList)
                addOnScrollListener(object : RecyclerView.OnScrollListener(){
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        if(dy > 0){
                            if(binding?.searchBar?.visibility == View.VISIBLE) {
                                binding?.searchBar?.animate()?.alpha(0.0f)?.setDuration(300)?.withEndAction {
                                    binding?.searchBar?.visibility = View.GONE
                                }?.start()

                            }
                        }else if(dy < 0){
                            if(binding?.searchBar?.visibility == View.GONE) {
                                binding?.searchBar?.alpha = 0.0f
                                binding?.searchBar?.visibility = View.VISIBLE
                                binding?.searchBar?.animate()?.alpha(1.0f)?.setDuration(300)?.start()
                            }
                        }
                    }
                })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}