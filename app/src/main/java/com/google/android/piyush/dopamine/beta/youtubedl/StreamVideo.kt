package com.google.android.piyush.dopamine.beta.youtubedl

import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.databinding.ActivityStreamVideoBinding
import com.yausername.youtubedl_android.YoutubeDL.getInstance
import com.yausername.youtubedl_android.YoutubeDLRequest
import com.yausername.youtubedl_android.mapper.VideoInfo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class StreamVideo : AppCompatActivity() {
    private lateinit var binding: ActivityStreamVideoBinding
    private lateinit var compositeDisposable: CompositeDisposable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStreamVideoBinding.inflate(layoutInflater)
        compositeDisposable = CompositeDisposable()
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.videoView.setOnPreparedListener{
            binding.videoView.start()
        }

        binding.playVideo.setOnClickListener {
            startStream()
        }
    }

    private fun startStream(){
        val url: String = binding.videoLink.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(url)) {
            binding.videoLink.error = "Enter a valid URL"
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        val disposable = Observable.fromCallable {
            val request = YoutubeDLRequest(url)
            request.addOption("-f", "best")
            getInstance().getInfo(request)
        }
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { streamInfo: VideoInfo -> binding.progressBar.visibility = View.GONE

                val videoUrl = streamInfo.url
                if (TextUtils.isEmpty(videoUrl)) {
                    Toast.makeText(
                        this@StreamVideo,
                        "failed to get stream url",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    if (videoUrl != null) {
                        setupVideoView(videoUrl)
                    }
                }
            },
                { e: Throwable? ->
                Log.e(
                   TAG,
                    "failed to get stream info", e
                )
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    this@StreamVideo,
                    "streaming failed. failed to get stream info",
                    Toast.LENGTH_LONG
                ).show()
            })
        compositeDisposable.add(disposable)

    }
    private fun setupVideoView(videoUrl: String) {
        binding.videoView.setMedia(
            Uri.parse(videoUrl)
        )
    }


    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}