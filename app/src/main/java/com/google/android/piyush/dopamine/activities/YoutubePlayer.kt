package com.google.android.piyush.dopamine.activities

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.piyush.database.viewModel.DatabaseViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.adapters.CustomPlaylistsAdapter
import com.google.android.piyush.dopamine.databinding.ActivityYoutubePlayerBinding
import com.google.android.piyush.dopamine.utilities.CustomDialog
import com.google.android.piyush.youtube.model.BrowseResponse
import com.google.android.piyush.youtube.model.BrowseResponse.Contents.TwoColumnBrowseResultsRenderer.Tab.TabRenderer.Content.SectionListRenderer.Contents.ItemSectionRenderer.Contents.ShelfRenderer.Content.ExpandedShelfContentsRenderer.Item.VideoRenderer
import com.google.android.piyush.youtube.utilities.YoutubeResponse
import com.google.android.piyush.youtube.viewModels.YoutubeViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions

@Suppress("DEPRECATION")
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
        val channelName = intent.getStringExtra("channelName")
        val publishedTime = intent.getStringExtra("publishedTime")
        val viewCount = intent.getStringExtra("viewCount")
        val channelImage = intent.getStringExtra("channelImage")

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
                    val videoInfo = "$viewCount • $publishedTime ...more"
                    binding.videoTitle.text = response.data.videoDetails?.title
                    binding.videoInfo.text = videoInfo
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_add_to_a_playlist,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.post {
            val dialog = dialog as? BottomSheetDialog
            dialog?.behavior?.let { behavior ->
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }
}