package com.google.android.piyush.dopamine.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.adapters.TrendingAdapter
import com.google.android.piyush.dopamine.databinding.FragmentTrendingBinding
import com.google.android.piyush.youtube.model.BrowseResponse.Contents.TwoColumnBrowseResultsRenderer.Tab.TabRenderer.Content.SectionListRenderer.Contents.ItemSectionRenderer.Contents.ShelfRenderer.Content.ExpandedShelfContentsRenderer.Item.VideoRenderer
import com.google.android.piyush.youtube.utilities.YoutubeResponse
import com.google.android.piyush.youtube.viewModels.YoutubeViewModel

class Trending : Fragment() {

    private var binding : FragmentTrendingBinding? = null
    private val viewModel : YoutubeViewModel by activityViewModels<YoutubeViewModel>()
    private val trendingTabs = mutableListOf<String>()
    private lateinit var trendingAdapter: TrendingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_trending, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentTrendingBinding.bind(view)

        viewModel.trendingVideos.observe(viewLifecycleOwner) { response ->
            when(response) {
                is YoutubeResponse.Loading -> {
                    binding?.progressBar?.visibility = View.VISIBLE
                    binding?.text1?.visibility = View.GONE
                    binding?.recyclerView1?.visibility = View.GONE
                }
                is YoutubeResponse.Success -> {
                    val videos = mutableListOf<VideoRenderer>()
                    val tabs = response.data.contents?.twoColumnBrowseResultsRenderer?.tabs
                    tabs?.forEach {
                        val title = it.tabRenderer?.title.toString()
                        trendingTabs.add(title)
                        it.tabRenderer?.content?.sectionListRenderer?.contents?.forEach {
                            it.itemSectionRenderer?.contents?.forEach {
                                val items = it.shelfRenderer?.content?.expandedShelfContentsRenderer?.items
                                items?.forEach {
                                    val data = it.videoRenderer
                                    videos.add(
                                        VideoRenderer(
                                            videoId = data?.videoId,
                                            thumbnail = data?.thumbnail,
                                            title = data?.title,
                                            longBylineText = data?.longBylineText,
                                            shortViewCountText = data?.shortViewCountText,
                                            publishedTimeText = data?.publishedTimeText,
                                            avatar = data?.avatar
                                        )
                                    )
                                }
                            }
                        }
                    }

                    binding.apply {
                        this?.progressBar?.visibility = View.GONE
                        this?.text1?.visibility = View.VISIBLE
                        this?.recyclerView1?.visibility = View.VISIBLE
                        this?.text1?.text = trendingTabs[0]
                    }
                    trendingAdapter = TrendingAdapter(requireContext(), videos = videos.distinct())
                    binding?.recyclerView1.apply {
                        this?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        this?.adapter = trendingAdapter
                    }
                }
                is YoutubeResponse.Error -> {
                    Log.e("Library", "Error : ${response.exception}")
                }
            }
        }

        viewModel.musicVideos.observe(viewLifecycleOwner) { response ->
            when(response) {
                is YoutubeResponse.Loading -> {
                    binding?.progressBar?.visibility = View.VISIBLE
                    binding?.text2?.visibility = View.GONE
                    binding?.recyclerView2?.visibility = View.GONE
                }
                is YoutubeResponse.Success -> {
                    val videos = mutableListOf<VideoRenderer>()
                    val tabs = response.data.contents?.twoColumnBrowseResultsRenderer?.tabs
                    tabs?.forEach {
                        it.tabRenderer?.content?.sectionListRenderer?.contents?.forEach {
                            it.itemSectionRenderer?.contents?.forEach {
                                val items = it.shelfRenderer?.content?.expandedShelfContentsRenderer?.items
                                items?.forEach {
                                    val data = it.videoRenderer
                                    videos.add(
                                        VideoRenderer(
                                            videoId = data?.videoId,
                                            thumbnail = data?.thumbnail,
                                            title = data?.title,
                                            longBylineText = data?.longBylineText,
                                            shortViewCountText = data?.shortViewCountText,
                                            publishedTimeText = data?.publishedTimeText,
                                            avatar = data?.avatar
                                        )
                                    )
                                }
                            }
                        }
                    }

                    binding.apply {
                        this?.progressBar?.visibility = View.GONE
                        this?.text2?.visibility = View.VISIBLE
                        this?.recyclerView2?.visibility = View.VISIBLE
                        this?.text2?.text = trendingTabs[1]
                    }

                    trendingAdapter = TrendingAdapter(requireContext(), videos = videos.distinct())
                    binding?.recyclerView2.apply {
                        this?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        this?.adapter = trendingAdapter
                    }
                }
                is YoutubeResponse.Error -> {
                    Log.e("Library", "Error : ${response.exception}")
                }
            }
        }

        viewModel.gamingVideos.observe(viewLifecycleOwner) { response ->
            when(response) {
                is YoutubeResponse.Loading -> {
                    binding?.progressBar?.visibility = View.VISIBLE
                    binding?.text3?.visibility = View.GONE
                    binding?.recyclerView3?.visibility = View.GONE
                }
                is YoutubeResponse.Success -> {
                    val videos = mutableListOf<VideoRenderer>()
                    val tabs = response.data.contents?.twoColumnBrowseResultsRenderer?.tabs
                    tabs?.forEach {
                        it.tabRenderer?.content?.sectionListRenderer?.contents?.forEach {
                            it.itemSectionRenderer?.contents?.forEach {
                                val items = it.shelfRenderer?.content?.expandedShelfContentsRenderer?.items
                                items?.forEach {
                                    val data = it.videoRenderer
                                    videos.add(
                                        VideoRenderer(
                                            videoId = data?.videoId,
                                            thumbnail = data?.thumbnail,
                                            title = data?.title,
                                            longBylineText = data?.longBylineText,
                                            shortViewCountText = data?.shortViewCountText,
                                            publishedTimeText = data?.publishedTimeText,
                                            avatar = data?.avatar
                                        )
                                    )
                                }
                            }
                        }
                    }

                    binding.apply {
                        this?.progressBar?.visibility = View.GONE
                        this?.text3?.visibility = View.VISIBLE
                        this?.recyclerView3?.visibility = View.VISIBLE
                        this?.text3?.text = trendingTabs[2]
                    }

                    trendingAdapter = TrendingAdapter(requireContext(), videos = videos.distinct())
                    binding?.recyclerView3.apply {
                        this?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        this?.adapter = trendingAdapter
                    }
                }
                is YoutubeResponse.Error -> {
                    Log.e("Library", "Error : ${response.exception}")
                }
            }
        }

        viewModel.moviesVideos.observe(viewLifecycleOwner) { response ->
            when(response) {
                is YoutubeResponse.Loading -> {
                    binding?.progressBar?.visibility = View.VISIBLE
                    binding?.text4?.visibility = View.GONE
                    binding?.recyclerView4?.visibility = View.GONE
                }

                is YoutubeResponse.Success -> {
                    val videos = mutableListOf<VideoRenderer>()
                    val tabs = response.data.contents?.twoColumnBrowseResultsRenderer?.tabs
                    tabs?.forEach {
                        val title = it.tabRenderer?.title.toString()
                        trendingTabs.add(title)
                        it.tabRenderer?.content?.sectionListRenderer?.contents?.forEach {
                            it.itemSectionRenderer?.contents?.forEach {
                                val items = it.shelfRenderer?.content?.expandedShelfContentsRenderer?.items
                                items?.forEach {
                                    val data = it.videoRenderer
                                    videos.add(
                                        VideoRenderer(
                                            videoId = data?.videoId,
                                            thumbnail = data?.thumbnail,
                                            title = data?.title,
                                            longBylineText = data?.longBylineText,
                                            shortViewCountText = data?.shortViewCountText,
                                            publishedTimeText = data?.publishedTimeText,
                                            avatar = data?.avatar
                                        )
                                    )
                                }
                            }
                        }
                    }

                    binding.apply {
                        this?.progressBar?.visibility = View.GONE
                        this?.text4?.visibility = View.VISIBLE
                        this?.recyclerView4?.visibility = View.VISIBLE
                        this?.text4?.text = trendingTabs[3]
                    }

                    trendingAdapter = TrendingAdapter(requireContext(), videos = videos.distinct())
                    binding?.recyclerView4.apply {
                        this?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        this?.adapter = trendingAdapter
                    }
                }

                is YoutubeResponse.Error -> {
                    Log.e("Library", "Error : ${response.exception}")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}