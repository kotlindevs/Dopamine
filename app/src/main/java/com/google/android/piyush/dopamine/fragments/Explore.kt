package com.google.android.piyush.dopamine.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.piyush.database.DopamineDao
import com.google.android.piyush.dopamine.DopamineDbViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.YoutubeViewModel
import com.google.android.piyush.dopamine.activities.YoutubePlayer
import com.google.android.piyush.dopamine.adapters.ExploreRecentSearchAdapter
import com.google.android.piyush.dopamine.adapters.SearchSuggestionAdapter
import com.google.android.piyush.dopamine.adapters.YoutubePlayerVideosAdapter
import com.google.android.piyush.dopamine.databinding.FragmentSearchBinding
import com.google.android.piyush.youtube.model.VideoRenderer
import com.google.android.piyush.youtube.utilities.Response
import dagger.hilt.android.AndroidEntryPoint
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

        database.loadRecentSearch().run {
            database.recentSearch.observe(viewLifecycleOwner){ response ->
                when(response){
                    is Response.Loading -> {
                        binding?.apply {
                            progressBar.visibility = View.VISIBLE
                            recentSearchTitle.visibility = View.GONE
                            recentSearch.visibility = View.GONE
                            searchResults.visibility = View.GONE
                        }
                    }
                    is Response.Success -> {
                        binding?.apply {
                            progressBar.visibility = View.GONE
                            recentSearchTitle.visibility = View.VISIBLE
                            recentSearch.visibility = View.VISIBLE
                            searchResults.visibility = View.GONE
                        }
                        val results = response.data
                        if(results.isNotEmpty()) {
                            val adapter = ExploreRecentSearchAdapter(
                                results,
                                selectedSearch = { i ->
                                    binding?.apply {
                                        recentSearchTitle.visibility = View.GONE
                                        recentSearch.visibility = View.GONE
                                        searchResults.visibility = View.VISIBLE
                                    }
                                    viewModel.searchData(query = i.searchText)
                                },
                                deleteSearch = { i ->
                                    database.deleteRecentSearch(keyword = i.searchText)
                                }
                            )
                            results.let {
                                binding?.apply {
                                    recentSearchTitle.visibility = View.VISIBLE
                                    recentSearch.apply {
                                        visibility = View.VISIBLE
                                        layoutManager = LinearLayoutManager(
                                            requireContext(),
                                            LinearLayoutManager.VERTICAL,
                                            false
                                        )
                                        this.adapter = adapter
                                    }
                                }
                            }
                        }else{
                            binding?.apply {
                                emptySearch.visibility = View.VISIBLE
                                recentSearchTitle.visibility = View.GONE
                                recentSearch.visibility = View.GONE
                                searchResults.visibility = View.VISIBLE
                            }
                        }
                    }
                    is Response.Error -> {
                        Log.e("Error => ", response.exception.message.toString())
                    }
                }
            }
        }

        binding?.apply {
            searchBar.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    binding?.emptySearch?.visibility = View.GONE
                    searchView.show()
                }
            }
            searchView.editText.apply {
                doAfterTextChanged { query ->
                    viewModel.searchSuggestions(query = query.toString())
                }

                doOnTextChanged { text, start, before, count ->
                    if (searchView.editText.text.toString().isEmpty()) {
                        searchSuggestionText.visibility = View.GONE
                        searchViewProgressBar.visibility = View.GONE
                    } else {
                        searchSuggestionText.visibility = View.VISIBLE
                    }
                }

                setOnEditorActionListener { v, actionId, event ->
                    val search = v?.text.toString()
                    if(search.isNotEmpty()){
                        viewModel.searchData(query = search)
                        database.addSearchKeyword(
                            keyword = search,
                            timestamp = System.currentTimeMillis()
                        )
                        searchSuggestionText.visibility = View.GONE
                        searchView.visibility = View.GONE
                        searchResults.visibility = View.VISIBLE
                    }else{
                        searchSuggestionText.visibility = View.GONE
                        searchViewProgressBar.visibility = View.GONE
                    }
                    true
                }
            }
        }

        viewModel.searchSuggestions.observe(viewLifecycleOwner) { response ->
            when(response){
                is Response.Loading -> {
                    binding?.apply {
                        searchViewProgressBar.visibility = View.VISIBLE
                        searchSuggestionText.visibility = View.GONE
                    }
                }
                is Response.Success -> {
                    binding?.apply {
                        searchViewProgressBar.visibility = View.GONE
                        searchSuggestionText.visibility = View.VISIBLE
                    }
                    val results = response.data.refinements
                    if (results != null) {
                        binding?.apply {
                            searchSuggestionText.apply {
                                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                                adapter = SearchSuggestionAdapter(
                                    results,
                                    onSuggestionClick = { search ->
                                        if(search.isNotEmpty()){
                                            binding?.apply {
                                                recentSearchTitle.visibility = View.GONE
                                                recentSearch.visibility = View.GONE
                                                searchResults.visibility = View.VISIBLE
                                            }
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
                is Response.Loading -> {
                    binding?.progressBar?.visibility = View.VISIBLE
                    binding?.searchResults?.visibility = View.GONE
                    binding?.searchBar?.visibility = View.GONE
                }
                is Response.Success -> {
                    binding?.progressBar?.visibility = View.GONE
                    binding?.searchResults?.visibility = View.VISIBLE
                    binding?.searchBar?.visibility = View.VISIBLE
                    val results = response.data
                    results.contents?.twoColumnSearchResultsRenderer?.primaryContents?.sectionListRenderer?.contents?.forEach { contents ->
                        contents.itemSectionRenderer?.contents?.forEach { content -> val videos = content.videoRenderer
                            if (videos != null) {
                                videosList.add(videos)
                            }
                        }
                    }

                    results.contents?.twoColumnSearchResultsRenderer?.primaryContents?.sectionListRenderer?.contents?.get(1).let {
                        it?.continuationItemRenderer?.continuationEndpoint?.continuationCommand.let { d ->
                            Log.i("Search => ", "${d?.request.toString()} :: ${d?.token.toString()}" )
                        }
                    }
                }
                is Response.Error -> {
                    Log.d("Search", response.exception.message.toString())
                }
            }

            binding?.searchResults?.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = YoutubePlayerVideosAdapter(
                    videosList,
                    onVideoClick = { video ->
                        context.startActivity(
                            Intent(context, YoutubePlayer::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .putExtra("videoId", video.videoId.toString())
                                .putExtra("channelName", video.longBylineText?.runs?.get(0)?.text.toString())
                                .putExtra("publishedTime", video.publishedTimeText?.simpleText.toString())
                                .putExtra("viewCount", video.shortViewCountText?.simpleText.toString())
                                .putExtra("videoLength", video.lengthText?.simpleText.toString())
                                .putExtra("channelImage", video.channelThumbnailSupportedRenderers?.channelThumbnailWithLinkRenderer?.thumbnail?.thumbnails?.firstOrNull()?.url.toString())

                        )
                    },
                    onChannelClick = {}
                )
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