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
import com.google.android.piyush.dopamine.databinding.ActivityYoutubePlayerBinding
import com.google.android.piyush.dopamine.databinding.YoutubePlayerInfoBinding
import com.google.android.piyush.youtube.model.VideoInfo
import com.google.android.piyush.youtube.utilities.YoutubeResponse
import com.google.android.piyush.youtube.viewModels.YoutubeViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions

class YoutubePlayer : AppCompatActivity() {

    private lateinit var binding: ActivityYoutubePlayerBinding
    private val viewmodel : YoutubeViewModel by viewModels<YoutubeViewModel>()

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

        Log.e("YoutubePlayer", "VideoId : $videoId || VideoLength : $videoLength")
        videoId?.let {
            viewmodel.getPlayerInfo(it)
        }

        channelImage?.let {
            Glide.with(this).load(it).into(binding.channelImage)
        }

        channelName?.let {
            binding.channelName.text = it
        }

        viewmodel.playerInfo.observe(this) { response ->
            when(response) {
                is YoutubeResponse.Loading -> {}
                is YoutubeResponse.Success -> {
                    val video = response.data.videoDetails
                    val videoInfo = "$viewCount • $publishedTime ...more"
                    binding.videoTitle.text = response.data.videoDetails?.title ?: "No Title"
                    binding.videoInfo.text = videoInfo

                    viewmodel.submitSharedVideoInfo(
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