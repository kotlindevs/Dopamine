package com.example.dopamine.DopamineMuiscPlayer

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.dopamine.Bollywood.Bollywood
import com.example.dopamine.Bollywood.BollywoodAdapter
import com.example.dopamine.Bollywood.BollywoodApi
import com.example.dopamine.Gaming.Gaming
import com.example.dopamine.Gaming.GamingAdapter
import com.example.dopamine.Gaming.GamingApi
import com.example.dopamine.Phonk.Phonk
import com.example.dopamine.Phonk.PhonkAdapter
import com.example.dopamine.Phonk.PhonkApi
import com.example.dopamine.R
import com.example.dopamine.Remix.Remix
import com.example.dopamine.Remix.RemixAdapter
import com.example.dopamine.Remix.RemixApi
import com.example.dopamine.TracksList.Adapter.TrackListAdapter
import com.example.dopamine.TracksList.TrackListApi.TracksApi
import com.example.dopamine.TracksList.TracksDataClass.Track
import com.example.dopamine.Travelling.Travelling
import com.example.dopamine.Travelling.TravellingAdapter
import com.example.dopamine.Travelling.TravellingApi
import com.example.dopamine.Trending.Trend
import com.example.dopamine.Trending.TrendsAdaptor
import com.example.dopamine.Trending.TrendsApi
import com.example.dopamine.authentication.googleSession
import com.example.dopamine.databinding.ActivityMasterMusicPlayerBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MasterMusicPlayer : AppCompatActivity(){

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var binding: ActivityMasterMusicPlayerBinding
    private var handler: Handler = Handler()
    private lateinit var runnable: Runnable
    private lateinit var googleSession: googleSession

    private lateinit var trackListAdapter: TrackListAdapter
    private lateinit var trendsAdaptor: TrendsAdaptor
    private lateinit var bollywoodAdapter: BollywoodAdapter
    private lateinit var travellingAdapter: TravellingAdapter
    private lateinit var phonkAdapter: PhonkAdapter
    private lateinit var remixAdapter: RemixAdapter
    private lateinit var gamingAdapter: GamingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMasterMusicPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mediaPlayer = MediaPlayer()
        googleSession = googleSession(this)

        //Dopamine Picks
        if(intent.getStringExtra("dopamine_pick")!=null){
            trackListAdapter = TrackListAdapter(applicationContext,ArrayList())
            Retrofit.Builder()
                .baseUrl("https://api.npoint.io/a2bbf40c66d86d855cda/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TracksApi::class.java)
                .getTracks()
                .enqueue(object : Callback<List<Track>> {
                    override fun onResponse(call: Call<List<Track>>, response: Response<List<Track>>) {
                        trackListAdapter = TrackListAdapter(
                            applicationContext,
                            response.body()!!
                        )
                        var currentSongPosition = intent.getIntExtra("position",0)
                        val tracksList  = trackListAdapter.getArrayList()
                        var currentSong = tracksList[currentSongPosition]

                        setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                        playSong(currentSong.preview_url.toUri())

                        Log.d("currentSong",currentSong.toString())

                        binding.nextSong.setOnClickListener {
                            if(mediaPlayer.isPlaying){
                                handler.removeCallbacks(runnable)
                                mediaPlayer.reset()
                                binding.playPause.setImageResource(R.drawable.baseline_play_circle_24)
                                binding.musicSeekBar.progress = 0
                                binding.trackStart.text = milliSecondToTime(mediaPlayer.currentPosition.toLong())
                                currentSongPosition  = (currentSongPosition + 1) % tracksList.size
                                currentSong = tracksList[currentSongPosition]
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                            }else{
                                currentSongPosition  = (currentSongPosition + 1) % tracksList.size
                                currentSong = tracksList[currentSongPosition]
                                mediaPlayer.reset()
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                                Log.d("currentSong",currentSong.toString())
                                Log.d("currentSongPosition",currentSongPosition.toString())
                            }
                        }
                        binding.prevSong.setOnClickListener {
                            if(mediaPlayer.isPlaying){
                                handler.removeCallbacks(runnable)
                                mediaPlayer.reset()
                                binding.playPause.setImageResource(R.drawable.baseline_play_circle_24)
                                binding.musicSeekBar.progress = 0
                                binding.trackStart.text = milliSecondToTime(mediaPlayer.currentPosition.toLong())
                                currentSongPosition  = (currentSongPosition - 1) % tracksList.size
                                currentSong = tracksList[currentSongPosition]
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                            }else{
                                currentSongPosition = (currentSongPosition - 1) % tracksList.size
                                currentSong = tracksList[currentSongPosition]
                                mediaPlayer.reset()
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(), currentSong.song_name)

                                Log.d("currentSong", currentSong.toString())
                                Log.d("currentSongPosition", currentSongPosition.toString())
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<Track>>, t: Throwable) {
                        Log.d("Tracks", t.message.toString())
                    }
                })

        //Trending
        }else if(intent.getStringExtra("trendings")!=null){
            trendsAdaptor = TrendsAdaptor(applicationContext,ArrayList())
            Retrofit.Builder()
                .baseUrl("https://api.npoint.io/23ad1a516e3776ed61b5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TrendsApi::class.java)
                .getTrends()
                .enqueue(object : Callback<List<Trend>> {
                    override fun onResponse(call: Call<List<Trend>>, response: Response<List<Trend>>) {
                        trendsAdaptor = TrendsAdaptor(
                            applicationContext,
                            response.body()!!
                        )
                        var currentSongPosition = intent.getIntExtra("position",0)
                        val trendsList  = trendsAdaptor.getArrayList()
                        var currentSong = trendsList[currentSongPosition]

                        setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                        playSong(currentSong.preview_url.toUri())

                        Log.d("currentSong",currentSong.toString())

                        binding.nextSong.setOnClickListener {
                            if(mediaPlayer.isPlaying){
                                handler.removeCallbacks(runnable)
                                mediaPlayer.reset()
                                binding.playPause.setImageResource(R.drawable.baseline_play_circle_24)
                                binding.musicSeekBar.progress = 0
                                binding.trackStart.text = milliSecondToTime(mediaPlayer.currentPosition.toLong())
                                currentSongPosition  = (currentSongPosition + 1) % trendsList.size
                                currentSong = trendsList[currentSongPosition]
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                            }else{
                                currentSongPosition  = (currentSongPosition + 1) % trendsList.size
                                currentSong = trendsList[currentSongPosition]
                                mediaPlayer.reset()
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                                Log.d("currentSong",currentSong.toString())
                                Log.d("currentSongPosition",currentSongPosition.toString())
                            }
                        }
                        binding.prevSong.setOnClickListener {
                            if(mediaPlayer.isPlaying){
                                handler.removeCallbacks(runnable)
                                mediaPlayer.reset()
                                binding.playPause.setImageResource(R.drawable.baseline_play_circle_24)
                                binding.musicSeekBar.progress = 0
                                binding.trackStart.text = milliSecondToTime(mediaPlayer.currentPosition.toLong())
                                currentSongPosition  = (currentSongPosition - 1) % trendsList.size
                                currentSong = trendsList[currentSongPosition]
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                            }else{
                                currentSongPosition = (currentSongPosition - 1) % trendsList.size
                                currentSong = trendsList[currentSongPosition]
                                mediaPlayer.reset()
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(), currentSong.song_name)

                                Log.d("currentSong", currentSong.toString())
                                Log.d("currentSongPosition", currentSongPosition.toString())
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<Trend>>, t: Throwable) {
                        Log.d("Tracks", t.message.toString())
                    }
                })

        //Bollywood
        }else if(intent.getStringExtra("Bollywood")!=null){
            bollywoodAdapter = BollywoodAdapter(applicationContext,ArrayList())
            Retrofit.Builder()
                .baseUrl("https://api.npoint.io/362bf03a7dd20cef3dce/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BollywoodApi::class.java)
                .getBollywood()
                .enqueue(object : Callback<List<Bollywood>> {
                    override fun onResponse(call: Call<List<Bollywood>>, response: Response<List<Bollywood>>) {
                        bollywoodAdapter = BollywoodAdapter(
                            applicationContext,
                            response.body()!!
                        )
                        var currentSongPosition = intent.getIntExtra("position",0)
                        val bollywoodList  = bollywoodAdapter.getArrayList()
                        var currentSong = bollywoodList[currentSongPosition]

                        setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                        playSong(currentSong.preview_url.toUri())

                        Log.d("currentSong",currentSong.toString())

                        binding.nextSong.setOnClickListener {
                            if(mediaPlayer.isPlaying){
                                handler.removeCallbacks(runnable)
                                mediaPlayer.reset()
                                binding.playPause.setImageResource(R.drawable.baseline_play_circle_24)
                                binding.musicSeekBar.progress = 0
                                binding.trackStart.text = milliSecondToTime(mediaPlayer.currentPosition.toLong())
                                currentSongPosition  = (currentSongPosition + 1) % bollywoodList.size
                                currentSong = bollywoodList[currentSongPosition]
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                            }else{
                                currentSongPosition  = (currentSongPosition + 1) % bollywoodList.size
                                currentSong = bollywoodList[currentSongPosition]
                                mediaPlayer.reset()
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                                Log.d("currentSong",currentSong.toString())
                                Log.d("currentSongPosition",currentSongPosition.toString())
                            }
                        }
                        binding.prevSong.setOnClickListener {
                            if(mediaPlayer.isPlaying){
                                handler.removeCallbacks(runnable)
                                mediaPlayer.reset()
                                binding.playPause.setImageResource(R.drawable.baseline_play_circle_24)
                                binding.musicSeekBar.progress = 0
                                binding.trackStart.text = milliSecondToTime(mediaPlayer.currentPosition.toLong())
                                currentSongPosition  = (currentSongPosition - 1) % bollywoodList.size
                                currentSong = bollywoodList[currentSongPosition]
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                            }else{
                                currentSongPosition = (currentSongPosition - 1) % bollywoodList.size
                                currentSong = bollywoodList[currentSongPosition]
                                mediaPlayer.reset()
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(), currentSong.song_name)

                                Log.d("currentSong", currentSong.toString())
                                Log.d("currentSongPosition", currentSongPosition.toString())
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<Bollywood>>, t: Throwable) {
                        Log.d("Tracks", t.message.toString())
                    }
                })

            //Travelling
        } else if(intent.getStringExtra("Travelling") != null){
            travellingAdapter = TravellingAdapter(applicationContext,ArrayList())
            Retrofit.Builder()
                .baseUrl("https://api.npoint.io/2a0dd0282835656afdcd/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TravellingApi::class.java)
                .getTravelling()
                .enqueue(object : Callback<List<Travelling>> {
                    override fun onResponse(call: Call<List<Travelling>>, response: Response<List<Travelling>>) {
                        travellingAdapter = TravellingAdapter(
                            applicationContext,
                            response.body()!!
                        )
                        var currentSongPosition = intent.getIntExtra("position",0)
                        val bollywoodList  = travellingAdapter.getArrayList()
                        var currentSong = bollywoodList[currentSongPosition]

                        setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                        playSong(currentSong.preview_url.toUri())

                        Log.d("currentSong",currentSong.toString())

                        binding.nextSong.setOnClickListener {
                            if(mediaPlayer.isPlaying){
                                handler.removeCallbacks(runnable)
                                mediaPlayer.reset()
                                binding.playPause.setImageResource(R.drawable.baseline_play_circle_24)
                                binding.musicSeekBar.progress = 0
                                binding.trackStart.text = milliSecondToTime(mediaPlayer.currentPosition.toLong())
                                currentSongPosition  = (currentSongPosition + 1) % bollywoodList.size
                                currentSong = bollywoodList[currentSongPosition]
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                            }else{
                                currentSongPosition  = (currentSongPosition + 1) % bollywoodList.size
                                currentSong = bollywoodList[currentSongPosition]
                                mediaPlayer.reset()
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                                Log.d("currentSong",currentSong.toString())
                                Log.d("currentSongPosition",currentSongPosition.toString())
                            }
                        }
                        binding.prevSong.setOnClickListener {
                            if(mediaPlayer.isPlaying){
                                handler.removeCallbacks(runnable)
                                mediaPlayer.reset()
                                binding.playPause.setImageResource(R.drawable.baseline_play_circle_24)
                                binding.musicSeekBar.progress = 0
                                binding.trackStart.text = milliSecondToTime(mediaPlayer.currentPosition.toLong())
                                currentSongPosition  = (currentSongPosition - 1) % bollywoodList.size
                                currentSong = bollywoodList[currentSongPosition]
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                            }else{
                                currentSongPosition = (currentSongPosition - 1) % bollywoodList.size
                                currentSong = bollywoodList[currentSongPosition]
                                mediaPlayer.reset()
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(), currentSong.song_name)

                                Log.d("currentSong", currentSong.toString())
                                Log.d("currentSongPosition", currentSongPosition.toString())
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<Travelling>>, t: Throwable) {
                        Log.d("Tracks", t.message.toString())
                    }
                })

            //Phonk
        } else if(intent.getStringExtra("Phonk") != null){
            phonkAdapter = PhonkAdapter(applicationContext,ArrayList())
            Retrofit.Builder()
                .baseUrl("https://api.npoint.io/cea292aae5d0b392abdc/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PhonkApi::class.java)
                .getPhonk()
                .enqueue(object : Callback<List<Phonk>> {
                    override fun onResponse(call: Call<List<Phonk>>, response: Response<List<Phonk>>) {
                        phonkAdapter = PhonkAdapter(
                            applicationContext,
                            response.body()!!
                        )
                        var currentSongPosition = intent.getIntExtra("position",0)
                        val bollywoodList  = phonkAdapter.getArrayList()
                        var currentSong = bollywoodList[currentSongPosition]

                        setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                        playSong(currentSong.preview_url.toUri())

                        Log.d("currentSong",currentSong.toString())

                        binding.nextSong.setOnClickListener {
                            if(mediaPlayer.isPlaying){
                                handler.removeCallbacks(runnable)
                                mediaPlayer.reset()
                                binding.playPause.setImageResource(R.drawable.baseline_play_circle_24)
                                binding.musicSeekBar.progress = 0
                                binding.trackStart.text = milliSecondToTime(mediaPlayer.currentPosition.toLong())
                                currentSongPosition  = (currentSongPosition + 1) % bollywoodList.size
                                currentSong = bollywoodList[currentSongPosition]
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                            }else{
                                currentSongPosition  = (currentSongPosition + 1) % bollywoodList.size
                                currentSong = bollywoodList[currentSongPosition]
                                mediaPlayer.reset()
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                                Log.d("currentSong",currentSong.toString())
                                Log.d("currentSongPosition",currentSongPosition.toString())
                            }
                        }
                        binding.prevSong.setOnClickListener {
                            if(mediaPlayer.isPlaying){
                                handler.removeCallbacks(runnable)
                                mediaPlayer.reset()
                                binding.playPause.setImageResource(R.drawable.baseline_play_circle_24)
                                binding.musicSeekBar.progress = 0
                                binding.trackStart.text = milliSecondToTime(mediaPlayer.currentPosition.toLong())
                                currentSongPosition  = (currentSongPosition - 1) % bollywoodList.size
                                currentSong = bollywoodList[currentSongPosition]
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                            }else{
                                currentSongPosition = (currentSongPosition - 1) % bollywoodList.size
                                currentSong = bollywoodList[currentSongPosition]
                                mediaPlayer.reset()
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(), currentSong.song_name)

                                Log.d("currentSong", currentSong.toString())
                                Log.d("currentSongPosition", currentSongPosition.toString())
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<Phonk>>, t: Throwable) {
                        Log.d("Tracks", t.message.toString())
                    }
                })

            //Remix
        } else if(intent.getStringExtra("Remix") != null){
            remixAdapter = RemixAdapter(applicationContext,ArrayList())
            Retrofit.Builder()
                .baseUrl("https://api.npoint.io/12abb65f1a120508b605/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RemixApi::class.java)
                .getRemix()
                .enqueue(object : Callback<List<Remix>> {
                    override fun onResponse(call: Call<List<Remix>>, response: Response<List<Remix>>) {
                        remixAdapter = RemixAdapter(
                            applicationContext,
                            response.body()!!
                        )
                        var currentSongPosition = intent.getIntExtra("position",0)
                        val bollywoodList  = remixAdapter.getArrayList()
                        var currentSong = bollywoodList[currentSongPosition]

                        setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                        playSong(currentSong.preview_url.toUri())

                        Log.d("currentSong",currentSong.toString())

                        binding.nextSong.setOnClickListener {
                            if(mediaPlayer.isPlaying){
                                handler.removeCallbacks(runnable)
                                mediaPlayer.reset()
                                binding.playPause.setImageResource(R.drawable.baseline_play_circle_24)
                                binding.musicSeekBar.progress = 0
                                binding.trackStart.text = milliSecondToTime(mediaPlayer.currentPosition.toLong())
                                currentSongPosition  = (currentSongPosition + 1) % bollywoodList.size
                                currentSong = bollywoodList[currentSongPosition]
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                            }else{
                                currentSongPosition  = (currentSongPosition + 1) % bollywoodList.size
                                currentSong = bollywoodList[currentSongPosition]
                                mediaPlayer.reset()
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                                Log.d("currentSong",currentSong.toString())
                                Log.d("currentSongPosition",currentSongPosition.toString())
                            }
                        }
                        binding.prevSong.setOnClickListener {
                            if(mediaPlayer.isPlaying){
                                handler.removeCallbacks(runnable)
                                mediaPlayer.reset()
                                binding.playPause.setImageResource(R.drawable.baseline_play_circle_24)
                                binding.musicSeekBar.progress = 0
                                binding.trackStart.text = milliSecondToTime(mediaPlayer.currentPosition.toLong())
                                currentSongPosition  = (currentSongPosition - 1) % bollywoodList.size
                                currentSong = bollywoodList[currentSongPosition]
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                            }else{
                                currentSongPosition = (currentSongPosition - 1) % bollywoodList.size
                                currentSong = bollywoodList[currentSongPosition]
                                mediaPlayer.reset()
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(), currentSong.song_name)

                                Log.d("currentSong", currentSong.toString())
                                Log.d("currentSongPosition", currentSongPosition.toString())
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<Remix>>, t: Throwable) {
                        Log.d("Tracks", t.message.toString())
                    }
                })

            //Gaming and chill
        } else if(intent.getStringExtra("Gaming") != null){
            gamingAdapter = GamingAdapter(applicationContext,ArrayList())
            Retrofit.Builder()
                .baseUrl("https://api.npoint.io/3c6b399952924125b043/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GamingApi::class.java)
                .getGaming()
                .enqueue(object : Callback<List<Gaming>> {
                    override fun onResponse(call: Call<List<Gaming>>, response: Response<List<Gaming>>) {
                        gamingAdapter = GamingAdapter(
                            applicationContext,
                            response.body()!!
                        )
                        var currentSongPosition = intent.getIntExtra("position",0)
                        val bollywoodList  = gamingAdapter.getArrayList()
                        var currentSong = bollywoodList[currentSongPosition]

                        setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                        playSong(currentSong.preview_url.toUri())

                        Log.d("currentSong",currentSong.toString())

                        binding.nextSong.setOnClickListener {
                            if(mediaPlayer.isPlaying){
                                handler.removeCallbacks(runnable)
                                mediaPlayer.reset()
                                binding.playPause.setImageResource(R.drawable.baseline_play_circle_24)
                                binding.musicSeekBar.progress = 0
                                binding.trackStart.text = milliSecondToTime(mediaPlayer.currentPosition.toLong())
                                currentSongPosition  = (currentSongPosition + 1) % bollywoodList.size
                                currentSong = bollywoodList[currentSongPosition]
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                            }else{
                                currentSongPosition  = (currentSongPosition + 1) % bollywoodList.size
                                currentSong = bollywoodList[currentSongPosition]
                                mediaPlayer.reset()
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                                Log.d("currentSong",currentSong.toString())
                                Log.d("currentSongPosition",currentSongPosition.toString())
                            }
                        }
                        binding.prevSong.setOnClickListener {
                            if(mediaPlayer.isPlaying){
                                handler.removeCallbacks(runnable)
                                mediaPlayer.reset()
                                binding.playPause.setImageResource(R.drawable.baseline_play_circle_24)
                                binding.musicSeekBar.progress = 0
                                binding.trackStart.text = milliSecondToTime(mediaPlayer.currentPosition.toLong())
                                currentSongPosition  = (currentSongPosition - 1) % bollywoodList.size
                                currentSong = bollywoodList[currentSongPosition]
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                            }else{
                                currentSongPosition = (currentSongPosition - 1) % bollywoodList.size
                                currentSong = bollywoodList[currentSongPosition]
                                mediaPlayer.reset()
                                playSong(currentSong.preview_url.toUri())
                                setDataForSong(currentSong.mp_url.toUri(), currentSong.song_name)

                                Log.d("currentSong", currentSong.toString())
                                Log.d("currentSongPosition", currentSongPosition.toString())
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<Gaming>>, t: Throwable) {
                        Log.d("Tracks", t.message.toString())
                    }
                })

            //Gym & workout
        } else if(intent.getStringExtra("Gym") != null){

        }

        //Player
        binding.playPause.setOnClickListener {
            if(mediaPlayer.isPlaying){
                handler.removeCallbacks(runnable)
                mediaPlayer.pause()
                binding.playPause.setImageResource(R.drawable.baseline_play_circle_24)
            }else{
                mediaPlayer.start()
                binding.playPause.setImageResource(R.drawable.baseline_pause_circle_24)
                updateSeekBar()
            }
        }

        runnable= Runnable {
            binding.musicSeekBar.setProgress(mediaPlayer.currentPosition,true)
            binding.trackStart.text = milliSecondToTime(mediaPlayer.currentPosition.toLong())
            updateSeekBar()
        }

        binding.musicSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser) {
                    mediaPlayer.seekTo(progress)
                    binding.trackStart.text =
                        milliSecondToTime(mediaPlayer.currentPosition.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        mediaPlayer.setOnCompletionListener {
            Log.d("UserVal","Called")
            binding.playPause.setImageResource(R.drawable.baseline_play_circle_24)
            binding.musicSeekBar.progress = 0
        }
    }

    private fun playSong(songUrl : Uri){
        try {
            mediaPlayer.setDataSource(applicationContext,songUrl)
            mediaPlayer.prepare()
            binding.trackEnd.text = milliSecondToTime(mediaPlayer.duration.toLong())
            binding.musicSeekBar.max = mediaPlayer.duration
        }catch (e : Exception){
            showToast(e.message.toString())
        }
    }

    private fun setDataForSong(songImage : Uri,songName : String){
        Glide.with(applicationContext)
            .load(songImage)
            .into(binding.TracksPhoto)

        binding.TracksName.text = songName
    }

    override fun onStart() {
        super.onStart()

        binding.likeSong.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                showToast("You liked ❤️")
            }
        }
    }

    private fun updateSeekBar(){
        if(mediaPlayer.isPlaying){
            binding.musicSeekBar.progress = mediaPlayer.currentPosition
            Log.d("Progress", mediaPlayer.currentPosition.toString())
            handler.postDelayed(runnable,100)
        }
    }

    private fun milliSecondToTime(milliSecond : Long) : String{
        var timer1 = ""
        var timer2 = ""
        val hours = milliSecond / (1000 * 60 * 60)
        val minutes = (milliSecond % (1000 * 60 * 60)) / (1000 * 60)
        val seconds = ((milliSecond % (1000 * 60 * 60)) % (1000 * 60) / 1000)

        if(hours > 0){
            timer1 = "${hours}:"
        }

        timer2 = if(seconds < 10){
            "0${seconds}"
        }else{
            "$seconds"
        }

        timer1 = "$timer1$minutes:$timer2"
        return timer1
    }

    private fun showToast(str : String) {
        Toast.makeText(applicationContext,str,Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
    }
}