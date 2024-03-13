package com.google.android.piyush.dopamine.beta.youtubedl

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.databinding.ActivityDownloadVideoBinding
import com.google.android.piyush.dopamine.utilities.Utilities
import com.yausername.youtubedl_android.YoutubeDL.getInstance
import com.yausername.youtubedl_android.YoutubeDLRequest
import com.yausername.youtubedl_android.YoutubeDLResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.File

class DownloadVideo : AppCompatActivity() {
    private lateinit var binding: ActivityDownloadVideoBinding
    private lateinit var compositeDisposable: CompositeDisposable
    private var downloading = false

    private val callback: Function3<Float, Long, String, Unit> =
        { progress: Float, _: Long?, _: String? ->
            runOnUiThread {
                binding.progressBar.progress = progress.toInt()
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownloadVideoBinding.inflate(layoutInflater)
        compositeDisposable = CompositeDisposable()
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.startVideo.setOnClickListener {
            startDownload()
        }

        binding.stopVideo.setOnClickListener{
            stopDownload()
        }
    }

    private fun stopDownload() {
        try {
            getInstance().destroyProcessById(Utilities.PROCESS_ID)
        } catch (e: Exception) {
            Log.e(
                TAG,
                e.toString()
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun startDownload() {
        if (downloading) {
            Toast.makeText(
                this@DownloadVideo,
                "cannot start download. a download is already in progress",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (!isStoragePermissionGranted()) {
            Toast.makeText(
                this@DownloadVideo,
                "grant storage permission and retry",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val url: String = binding.videoLink.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(url)) {
            binding.videoLink.error = "Enter a valid URL"
            return
        }

        val request = YoutubeDLRequest(url)
        val youtubeDLDir: File = getDownloadLocation()
        val config = File(youtubeDLDir, "config.txt")

        if (binding.useAdvancedMode.isChecked && config.exists()) {
            request.addOption("--config-location", config.absolutePath)
        } else {
            request.addOption("--no-mtime")
            request.addOption("--downloader", "libaria2c.so")
            request.addOption("--external-downloader-args", "aria2c:\"--summary-interval=1\"")
            request.addOption("-f", "best")
            request.addOption("-o", youtubeDLDir.absolutePath + "/%(title)s.%(ext)s")
        }

        binding.progressBar.progress = 0

        downloading = true
        val disposable = Observable.fromCallable<YoutubeDLResponse> {
            getInstance().execute(
                request,
                Utilities.PROCESS_ID,
                callback
            )
        }
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ _: YoutubeDLResponse ->
                binding.progressBar.progress = 100
                Toast.makeText(
                    this@DownloadVideo,
                    "Download Successfully",
                    Toast.LENGTH_LONG
                ).show()
                downloading = false
            }, { e: Throwable -> Log.e(TAG, "failed to download",e)
                Toast.makeText(
                    this@DownloadVideo,
                    "Download Failed",
                    Toast.LENGTH_LONG
                ).show()
                downloading = false
            })
        compositeDisposable.add(disposable)

    }

    private fun getDownloadLocation(): File {
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val dir = File(downloadsDir, Utilities.PROJECT_ID)
        if (!dir.exists()) dir.mkdir()
        return dir
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun isStoragePermissionGranted(): Boolean {
        if (checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO)
            == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_VIDEO),
                1
            )
            return false
        }
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}