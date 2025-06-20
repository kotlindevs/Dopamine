package com.google.android.piyush.dopamine.activities

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.adapters.YoutubePlayerKeywordsAdapter
import com.google.android.piyush.dopamine.adapters.YoutubePlayerShortsAdapter
import com.google.android.piyush.dopamine.adapters.YoutubePlayerVideosAdapter
import com.google.android.piyush.dopamine.databinding.ActivityYoutubePlayerBinding
import com.google.android.piyush.dopamine.databinding.YoutubePlayerInfoBinding
import com.google.android.piyush.youtube.model.SearchResponse
import com.google.android.piyush.youtube.model.SearchResponse.Contents.TwoColumnSearchResultsRenderer.PrimaryContents.SectionListRenderer.Content.ItemSectionRenderer.Content.ReelShelfRenderer.Item.ShortsLockupViewModel
import com.google.android.piyush.youtube.model.SearchResponse.Contents.TwoColumnSearchResultsRenderer.PrimaryContents.SectionListRenderer.Content.ItemSectionRenderer.Content.VideoRenderer
import com.google.android.piyush.youtube.model.VideoInfo
import com.google.android.piyush.youtube.utilities.YoutubeResponse
import com.google.android.piyush.youtube.viewModels.YoutubeViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions

class YoutubePlayer : AppCompatActivity() {

    private lateinit var binding: ActivityYoutubePlayerBinding
    private val viewModel : YoutubeViewModel by viewModels<YoutubeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYoutubePlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val videoId = intent?.getStringExtra("videoId")
        val publishedTime = intent.getStringExtra("publishedTime")
        val viewCount = intent.getStringExtra("viewCount")
        val channelName = intent.getStringExtra("channelName")
        val channelImage = intent.getStringExtra("channelImage")
        val videoLength = intent.getStringExtra("videoLength")

        videoId?.let {
            viewModel.getPlayerInfo(it)
        }

        channelImage?.let {
            Glide.with(this).load(it).into(binding.channelImage)
        }

        channelName?.let {
            binding.channelName.text = it
        }

        viewModel.playerInfo.observe(this) { response ->
            when(response) {
                is YoutubeResponse.Loading -> {
                    binding.apply {
                        shimmerEffectTitle.visibility = View.VISIBLE
                        shimmerEffectVideoInfo.visibility = View.VISIBLE
                        shimmerEffectTitle.startShimmer()
                        shimmerEffectVideoInfo.startShimmer()
                        videoTitle.visibility = View.GONE
                        videoInfo.visibility = View.GONE
                    }
                }
                is YoutubeResponse.Success -> {
                    binding.apply {
                        shimmerEffectTitle.visibility = View.GONE
                        shimmerEffectVideoInfo.visibility = View.GONE
                        shimmerEffectTitle.stopShimmer()
                        shimmerEffectVideoInfo.stopShimmer()
                        videoTitle.visibility = View.VISIBLE
                        videoInfo.visibility = View.VISIBLE
                    }
                    val video = response.data.videoDetails
                    val videoInfo = "$viewCount • $publishedTime ...more"
                    binding.videoTitle.text = response.data.videoDetails?.title ?: "No Title"
                    binding.videoInfo.text = videoInfo

                    viewModel.submitSharedVideoInfo(
                        VideoInfo(
                            videoId = videoId,
                            title = video?.title.toString(),
                            publishedTime = publishedTime,
                            viewCount = viewCount,
                            channelName = channelName,
                            length = videoLength,
                            keywords = video?.keywords,
                            channelImage = channelImage,
                            description = video?.shortDescription.toString()
                        )
                    )

                    video?.keywords?.let {
                        viewModel.keys(it)
                    }
                }
                is YoutubeResponse.Error -> {
                    Log.e("YoutubePlayer", "Error : ${response.exception}")
                }
            }
        }

        viewModel.searchKeys?.observe(this) { keys ->
            val key = keys[0].toString()

            viewModel.searchData(query = key)
            Log.i("YoutubePlayer", "Keys : $key")
        }

        viewModel.searchResults.observe(this) { response ->
            when(response) {
                is YoutubeResponse.Loading -> {}
                is YoutubeResponse.Success -> {
                    val shortsList = mutableListOf<ShortsLockupViewModel>()
                    val videosList = mutableListOf<VideoRenderer>()
                    response.data.contents?.twoColumnSearchResultsRenderer?.primaryContents?.sectionListRenderer?.contents?.forEach { contents ->
                        contents.itemSectionRenderer?.contents?.forEach { content ->
                            content.reelShelfRenderer?.items?.forEach { shorts ->
                                val shortsViewModel = shorts.shortsLockupViewModel

                                shortsViewModel?.let {
                                    shortsList.add(it)
                                }
                            }
                            content.channelRenderer.let { channelInfo ->
                                if (channelInfo != null) {
                                    binding.apply {
                                        Glide.with(this@YoutubePlayer).load(
                                            "https:${channelInfo.thumbnail?.thumbnails?.firstOrNull()?.url.toString()}"
                                        ).into(channelOwnerImage)
                                        channelInfo.shortBylineText?.runs?.firstOrNull()?.text.toString()
                                            .let { title ->
                                                if (true) {
                                                    channelOwnerTitle.text = title
                                                } else {
                                                    channelOwnerTitle.text = ""
                                                }
                                            }
                                        channelInfo.subscriberCountText?.simpleText.toString()
                                            .let { simpleName ->
                                                if (true) {
                                                    channelOwnerSubscriberCount.text = simpleName
                                                } else {
                                                    channelOwnerSubscriberCount.text = ""
                                                }
                                            }
                                        channelInfo.videoCountText?.simpleText.toString()
                                            .let { views ->
                                                if (true) {
                                                    channelOwnerViewCount.text = views
                                                } else {
                                                    channelOwnerViewCount.text = ""
                                                }
                                            }
                                    }
                                }
                            }
                            content.videoRenderer.let { video ->
                                if (video != null) {
                                    videosList.add(video)
                                }
                            }
                        }
                    }
                    binding.shorts.apply {
                        layoutManager = LinearLayoutManager(
                            this@YoutubePlayer,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        adapter = YoutubePlayerShortsAdapter(
                            shortsList
                        )
                    }

                    binding.relativeVideos.apply {
                        layoutManager = LinearLayoutManager(
                            this@YoutubePlayer,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                        adapter = YoutubePlayerVideosAdapter(
                            videosList
                        )
                    }

                }
                is YoutubeResponse.Error -> {
                    Log.e("YoutubePlayer", "Error : ${response.exception}")
                }
            }
        }

        binding.videoInfo.setOnClickListener{
            val youtubePlayerInfo = YoutubePlayerInfo()
            youtubePlayerInfo.show(supportFragmentManager, youtubePlayerInfo.tag)
        }

        binding.YtPlayer.enableBackgroundPlayback(true)
        binding.YtPlayer.enableAutomaticInitialization = false
        val iFramePlayerOptions = IFramePlayerOptions.Builder()
            .rel(1)
            .controls(1)
            .fullscreen(1)
            .build()

        binding.YtPlayer.initialize(youTubePlayerListener = object :
            AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
                youTubePlayer.apply {
                    loadVideo(
                        intent?.getStringExtra("videoId")!!,
                        0F
                    )
                }
            }
        }, true, iFramePlayerOptions)
    }
}

class YoutubePlayerInfo : BottomSheetDialogFragment(){

    private var binding : YoutubePlayerInfoBinding? = null
    private val viewModel : YoutubeViewModel by activityViewModels<YoutubeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.youtube_player_info,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = YoutubePlayerInfoBinding.bind(view)

        viewModel.sharedVideoInfo.observe(viewLifecycleOwner) { response ->
            when(response) {
                is YoutubeResponse.Loading -> {}
                is YoutubeResponse.Success -> {
                    val video = response.data
                    binding.apply {
                        this?.videoTitle?.text = video.title
                        this?.videoViews?.text = video.viewCount
                        this?.videoPublished?.text = video.publishedTime
                        this?.videoDuration?.text = video.length
                        this?.keywords?.apply {
                            layoutManager = LinearLayoutManager(
                                requireContext(),
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                            adapter = YoutubePlayerKeywordsAdapter(video.keywords)
                        }
                        this?.videoDescription?.text = video.description
                    }
                }
                is YoutubeResponse.Error -> {
                    Log.e("YoutubePlayerInfo", "Error : ${response.exception}")
                }
            }
        }

        binding?.closeInfo?.setOnClickListener{
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}