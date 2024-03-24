package com.google.android.piyush.dopamine.beta.youtubedl

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
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

        if(this.getSharedPreferences("DopamineApp", MODE_PRIVATE)
                .getBoolean("ExperimentalDownload", false).equals(true)) {
        }else{
            MaterialAlertDialogBuilder(this).apply {
                this.setTitle("NOTICES")
                this.setMessage("This functionality is experimental and currently it is not available for all users , android 13 and above users are recommended to use this feature.")
                this.setIcon(R.drawable.ic_info)
                this.setCancelable(true)
                this.setPositiveButton("Okay , I Understood ❤️") { _, _ ->
                    context.getSharedPreferences(
                        "DopamineApp",
                        MODE_PRIVATE
                    )
                        .edit().putBoolean("ExperimentalDownload", true).apply()
                }
            }.create().show()
        }


        binding.startVideo.setOnClickListener {
            startDownload()
        }

        binding.stopVideo.setOnClickListener{
            stopDownload()
        }

        binding.useAdvancedMode.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked.equals(true)){
                MaterialAlertDialogBuilder(this).apply {
                    this.setTitle("Alert")
                    this.setMessage("You are about to use advanced mode. before start you set the config file in your application download directory 🫡")
                    this.setIcon(R.drawable.ic_alert)
                    this.setCancelable(false)
                    this.setPositiveButton("Yes, I know") { dialog, _ ->
                        dialog?.dismiss()
                    }
                }.create().show()
            }
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
        if (!isStoragePermissionGranted()) {
            Snackbar.make(
                binding.main,
                "Grant Storage Permission",
                Snackbar.LENGTH_LONG
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

        if (binding.useAdvancedMode.isChecked) {
            if(config.exists()) {
                request.addOption("--config-location", config.absolutePath)
            }else{
                MaterialAlertDialogBuilder(this).apply {
                    this.setTitle("Error")
                    this.setMessage("The config file is not found in your application download directory, please set the config file in your application download directory and try again.")
                    this.setIcon(R.drawable.ic_dialog_error)
                    this.setCancelable(false)
                    this.setPositiveButton("Okay, I Understood") { dialog, _ ->
                        dialog?.dismiss()
                    }
                }.create().show()
            }
        } else {
            request.addOption("--no-mtime")
            request.addOption("--downloader", "libaria2c.so")
            request.addOption("--external-downloader-args", "aria2c:\"--summary-interval=1\"")
            request.addOption("-f", "best")
            request.addOption("-o", youtubeDLDir.absolutePath + "/%(title)s.%(ext)s")
        }

        binding.progressBar.progress = 0

        downloading = true
        val disposable = Observable.fromCallable {
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
                Snackbar.make(
                    binding.main,
                    "Download Successfully",
                    Snackbar.LENGTH_LONG
                ).show()
                downloading = false
            }, { e: Throwable -> Log.e(TAG, "failed to download",e)
                Snackbar.make(
                    binding.main,
                    "Download Failed",
                    Snackbar.LENGTH_LONG
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
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_VIDEO),
                    Utilities.PERMISSION_REQUEST_CODE
                )
            }
            return false
        }
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}