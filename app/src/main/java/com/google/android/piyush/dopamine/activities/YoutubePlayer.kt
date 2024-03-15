package com.google.android.piyush.dopamine.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues.TAG
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.piyush.database.entities.EntityFavouritePlaylist
import com.google.android.piyush.database.entities.EntityRecentVideos
import com.google.android.piyush.database.viewModel.DatabaseViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.adapters.CustomPlaylistsAdapter
import com.google.android.piyush.dopamine.adapters.YoutubeChannelPlaylistsAdapter
import com.google.android.piyush.dopamine.databinding.ActivityYoutubePlayerBinding
import com.google.android.piyush.dopamine.utilities.CustomDialog
import com.google.android.piyush.dopamine.viewModels.YoutubePlayerViewModel
import com.google.android.piyush.dopamine.viewModels.YoutubePlayerViewModelFactory
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import com.google.android.piyush.youtube.utilities.YoutubeResource
import com.google.firebase.auth.FirebaseAuth
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import io.reactivex.disposables.CompositeDisposable
import java.text.DecimalFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@Suppress("DEPRECATION")
class YoutubePlayer : AppCompatActivity() {

    private lateinit var binding: ActivityYoutubePlayerBinding
    private lateinit var youtubeRepositoryImpl: YoutubeRepositoryImpl
    private lateinit var youtubePlayerViewModel: YoutubePlayerViewModel
    private lateinit var youtubePlayerViewModelFactory: YoutubePlayerViewModelFactory
    private lateinit var databaseViewModel: DatabaseViewModel
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var notificationManager : NotificationManagerCompat
    private lateinit var notificationBuilder : NotificationCompat.Builder
    private var downloading = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityYoutubePlayerBinding.inflate(layoutInflater)
        youtubeRepositoryImpl = YoutubeRepositoryImpl()
        youtubePlayerViewModelFactory = YoutubePlayerViewModelFactory(youtubeRepositoryImpl)
        databaseViewModel = DatabaseViewModel(applicationContext)
        compositeDisposable = CompositeDisposable()
        notificationManager = NotificationManagerCompat.from(this)
        notificationBuilder = NotificationCompat.Builder(this, "download_channel")
        youtubePlayerViewModel = ViewModelProvider(
            this, youtubePlayerViewModelFactory
        )[YoutubePlayerViewModel::class.java]

        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val callback: Function3<Float, Long, String, Unit> =
            { progress: Float, _: Long?, _: String? ->
                runOnUiThread {
                    notificationBuilder.setContentText("${progress.toInt()}%")
                }
            }

        databaseViewModel.isFavouriteVideo(
            intent?.getStringExtra("videoId").toString()
        )
        databaseViewModel.isFavourite.observe(this) {
            binding.addToPlayList.isChecked = it == intent.getStringExtra("videoId").toString()
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
                    Log.d(
                        TAG,
                        " -> Activity : YoutubePlayer || videoId : $intent.getStringExtra(\"videoId\") "
                    )
                }
            }
        }, true, iFramePlayerOptions)

        binding.YtPlayer.addFullscreenListener(object : FullscreenListener {
            override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

                listOf(
                    binding.YtPlayer,
                    binding.addToPlayList,
                    binding.addToCustomPlayList
                ).forEach { it.visibility = View.GONE }

                if (fullscreenView.parent == null) {
                    binding.frameLayout.addView(fullscreenView)
                }
            }

            override fun onExitFullscreen() {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                binding.frameLayout.removeAllViews()
                listOf(
                    binding.YtPlayer,
                    binding.addToPlayList,
                    binding.addToCustomPlayList
                ).forEach { it.visibility = View.VISIBLE }
            }
        })

        binding.enterInPip.setOnClickListener {
            val supportsPIP =
                packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
            if (supportsPIP) enterPictureInPictureMode()
        }

        binding.addToCustomPlayList.setOnClickListener {
            val bottomSheetFragment = MyBottomSheetFragment()
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }

        youtubePlayerViewModel.getVideoDetails(
            intent?.getStringExtra("videoId").toString()
        )

        youtubePlayerViewModel.videoDetails.observe(this) { videoDetails ->
            when (videoDetails) {
                is YoutubeResource.Loading -> {
                    //Log.d(TAG, "YoutubePlayer || VideoDetails : True")
                }

                is YoutubeResource.Success -> {
                    val video = videoDetails.data
                    val videoLinkData =
                        "https://YouTube.com/watch?v=${intent.getStringExtra("videoId")}"
                    val textLikedData =
                        counter(
                            if(video.items?.get(0)?.statistics?.likeCount == null) 0 else video.items?.get(0)?.statistics?.likeCount!!
                        )
                    val textViewData = counter(
                        if(video.items?.get(0)?.statistics?.viewCount == null) 0 else video.items?.get(0)?.statistics?.viewCount!!
                    )
                    binding.apply {
                        textTitle.text = video.items?.get(0)?.snippet?.title
                        textKind.text = video.items?.get(0)?.kind
                        textTags.text = video.items?.get(0)?.snippet?.tags.toString()
                        videoLink.text = videoLinkData
                        textLiked.text = textLikedData
                        textView.text = textViewData
                        textDescription.text = video.items?.get(0)?.snippet?.description
                        copyVideoLink.setOnClickListener {
                            val clipboardManager =
                                getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                            val clipData = ClipData.newPlainText("Text Copied", videoLink.text)
                            clipboardManager.setPrimaryClip(clipData)
                            Toast.makeText(applicationContext, "Copied", Toast.LENGTH_SHORT).show()
                        }
                        addToPlayList.addOnCheckedStateChangedListener { _, isFavourite ->
                            if (isFavourite == 1) {
                                databaseViewModel.insertFavouriteVideos(
                                    EntityFavouritePlaylist(
                                        videoId = intent.getStringExtra("videoId").toString(),
                                        thumbnail = video.items!![0].snippet?.thumbnails?.high?.url,
                                        title = video.items!![0].snippet?.title,
                                        customName = video.items!![0].snippet?.customUrl,
                                        channelId = video.items!![0].snippet?.channelId!!,
                                        channelTitle = video.items!![0].snippet?.channelTitle
                                    )
                                )
                            } else {
                                databaseViewModel.deleteFavouriteVideo(
                                    intent.getStringExtra("videoId").toString()
                                )
                            }
                        }
                    }
                    databaseViewModel.isRecentVideo(videoId = video.items?.get(0)?.id.toString())

                    databaseViewModel.isRecent.observe(this) {
                        if (it == intent.getStringExtra("videoId").toString()) {
                            databaseViewModel.updateRecentVideo(
                                videoId = intent.getStringExtra("videoId").toString(),
                                time = LocalTime.now()
                                    .format(DateTimeFormatter.ofPattern("hh:mm a")).toString()
                            )
                        } else {
                            databaseViewModel.insertRecentVideos(
                                EntityRecentVideos(
                                    id = Random.nextInt(1, 100000),
                                    videoId = video.items?.get(0)?.id,
                                    thumbnail = video.items?.get(0)?.snippet?.thumbnails?.high?.url,
                                    title = video.items?.get(0)?.snippet?.title,
                                    customName = video.items?.get(0)?.snippet?.customUrl,
                                    timing = LocalTime.now()
                                        .format(DateTimeFormatter.ofPattern("hh:mm a"))
                                        .toString(),
                                    channelId = video.items?.get(0)?.snippet?.channelId
                                )
                            )
                        }
                    }

                    getSharedPreferences("customPlaylist", MODE_PRIVATE).edit {
                        putString("videoId", video.items?.get(0)?.id.toString())
                        putString("thumbnail", video.items?.get(0)?.snippet?.thumbnails?.high?.url)
                        putString("title", video.items?.get(0)?.snippet?.title)
                        putString("customName", video.items?.get(0)?.snippet?.customUrl)
                        putString("channelId", video.items?.get(0)?.snippet?.channelId)
                        putString("channelTitle", video.items?.get(0)?.snippet?.channelTitle)
                        putInt("viewCount", video.items?.get(0)?.statistics?.viewCount!!)
                        putString("publishedAt",  video.items?.get(0)?.snippet?.publishedAt)
                        putString("duration", video.items?.get(0)?.contentDetails?.duration)
                    }
                    Log.d(
                        TAG,
                        " -> Activity : YoutubePlayer || Video Details : ${videoDetails.data}"
                    )
                }

                is YoutubeResource.Error -> {
                    Log.d("YoutubePlayer", "YoutubePlayer: ${videoDetails.exception.message.toString()}")
                }
            }
        }

        youtubePlayerViewModel.getChannelDetails(
            intent?.getStringExtra("channelId").toString()
        )

        youtubePlayerViewModel.channelDetails.observe(this) { channelDetails ->
            when (channelDetails) {
                is YoutubeResource.Loading -> {
                    //Log.d(TAG, "YoutubePlayer || ChannelDetails : True")
                }

                is YoutubeResource.Success -> {
                    val channel = channelDetails.data
                    val subscriberData = "${
                        channel.items?.get(0)?.statistics?.subscriberCount?.let { subscriber ->
                            counter(subscriber)
                        }
                    } subscribers"
                    Glide.with(this)
                        .load(channel.items?.get(0)?.snippet?.thumbnails?.high?.url)
                        .into(binding.imageView)
                    binding.apply {
                        this.text1.text = channel.items?.get(0)?.snippet?.title
                        this.text2.text = channel.items?.get(0)?.snippet?.customUrl
                        this.text3.text = subscriberData
                        this.text4.text = channel.items?.get(0)?.snippet?.description
                    }
                    Log.d(TAG, " -> Activity : YoutubePlayer || Channel Details : $channel")
                }

                is YoutubeResource.Error -> {
                    //Log.d(TAG, "YoutubePlayer: ${channelDetails.exception.message.toString()}")
                }
            }
        }

        youtubePlayerViewModel.getChannelsPlaylist(
            intent?.getStringExtra("channelId").toString()
        )

        youtubePlayerViewModel.channelsPlaylist.observe(this) { channelsPlaylist ->
            when (channelsPlaylist) {
                is YoutubeResource.Loading -> {
                    Log.d(TAG, "YoutubePlayer || ChannelsPlaylist : True")
                }

                is YoutubeResource.Success -> {
                    binding.channelsPlaylist.apply {
                        layoutManager = LinearLayoutManager(this@YoutubePlayer)
//                        adapter = YoutubeChannelPlaylistsAdapter(context, channelsPlaylist.data)
                    }
                }

                is YoutubeResource.Error -> {
                    Log.d(TAG, "YoutubePlayer: ${channelsPlaylist.exception.message.toString()}")
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode)
        if (isInPictureInPictureMode) {
            binding.YtPlayer.wrapContent()
            binding.addToPlayList.visibility = View.GONE
            binding.addToCustomPlayList.visibility = View.GONE
        } else {
            binding.addToPlayList.visibility = View.VISIBLE
            binding.addToCustomPlayList.visibility = View.VISIBLE
        }
    }

    private fun counter(count : Int) : String{
        var num : Double = count.toDouble()
        val data: String
        if(num > 1000000.00){
            num /= 1000000.00
            num = DecimalFormat("#.##").format(num).toDouble()
            data = "${num}M"
        }else {
            num /= 1000
            num = DecimalFormat("#.##").format(num).toDouble()
            data = "${num}K"
        }
        return data
    }
}

class MyBottomSheetFragment : BottomSheetDialogFragment(){
    private lateinit var databaseViewModel: DatabaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_add_to_a_playlist,container,false)
        val createNewPlaylist: MaterialButton = view.findViewById(R.id.createNewPlayList)
        val customPlaylists : RecyclerView = view.findViewById(R.id.recyclerViewLocalPlaylist)
        databaseViewModel = DatabaseViewModel(requireContext())
        databaseViewModel.defaultMasterDev

        createNewPlaylist.setOnClickListener {
            val customDialog = CustomDialog(requireContext())
            customDialog.show()
        }

        if(FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()){
            if(databaseViewModel.isPlaylistExist(databaseViewModel.isUserFromPhoneAuth).equals(false)){
                databaseViewModel.userFromPhoneAuth()
            }else{
                Log.d(TAG, "${databaseViewModel.isUserFromPhoneAuth} : Exists")
            }
        }else {
            if (databaseViewModel.isPlaylistExist(databaseViewModel.newPlaylistName).equals(false)) {
                databaseViewModel.defaultUserPlaylist()
            } else {
                Log.d(TAG, "${databaseViewModel.newPlaylistName} : Exists")
            }
        }

        customPlaylists.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = CustomPlaylistsAdapter(
                requireContext(),
                databaseViewModel.getPlaylist(),
            )
            Log.d(TAG, " -> Fragment : BottomSheetFragment || Custom Playlists : ${databaseViewModel.getPlaylist()}")
        }

        return view
    }
}