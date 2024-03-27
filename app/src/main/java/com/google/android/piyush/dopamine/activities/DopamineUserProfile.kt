package com.google.android.piyush.dopamine.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.dcastalia.localappupdate.DownloadApk
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.piyush.database.viewModel.DatabaseViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.adapters.RecentVideosAdapter
import com.google.android.piyush.dopamine.beta.ExperimentsMode
import com.google.android.piyush.dopamine.databinding.ActivityDopamineUserProfileBinding
import com.google.android.piyush.dopamine.utilities.CustomDialog
import com.google.android.piyush.dopamine.utilities.NetworkUtilities
import com.google.android.piyush.dopamine.utilities.Utilities
import com.google.android.piyush.youtube.utilities.DopamineVersionViewModel
import com.google.android.piyush.youtube.utilities.YoutubeResource
import com.google.firebase.auth.FirebaseAuth
import kotlin.system.exitProcess

class DopamineUserProfile : AppCompatActivity() {

    private lateinit var binding: ActivityDopamineUserProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dopamineVersionViewModel: DopamineVersionViewModel
    private lateinit var databaseViewModel: DatabaseViewModel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDopamineUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        databaseViewModel = DatabaseViewModel(applicationContext)
        sharedPreferences = getSharedPreferences("DopamineApp", MODE_PRIVATE)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.useExpSearch.isChecked = sharedPreferences.getBoolean("ExperimentalSearch", false)
        binding.useExpDynamicUser.isChecked = sharedPreferences.getBoolean("ExperimentalUserColor", false)
        binding.applyForPreReleaseUpdate.isChecked = sharedPreferences.getBoolean("PreReleaseUpdate", false)

        onBackPressedDispatcher.addCallback {
            startActivity(Intent(this@DopamineUserProfile, DopamineHome::class.java))
        }

        if(NetworkUtilities.isNetworkAvailable(context = this).equals(true)) {
            dopamineVersionViewModel = DopamineVersionViewModel()
            if (firebaseAuth.currentUser?.email.isNullOrEmpty()) {
                Glide.with(this).load(R.drawable.default_user).into(binding.userImage)
                binding.userName.text = getString(R.string.app_name)
                binding.userEmail.text = firebaseAuth.currentUser?.phoneNumber
            } else {
                Glide.with(this).load(firebaseAuth.currentUser?.photoUrl).into(binding.userImage)
                binding.userName.text = firebaseAuth.currentUser?.displayName
                binding.userEmail.text = firebaseAuth.currentUser?.email
            }
        }else{
            applicationContext.getSharedPreferences("currentUser", MODE_PRIVATE).apply {
                getString("uid","").also { binding.userName.text = if(it.isNullOrEmpty()) "No User Id" else it.substring(0,15) }
                getString("email","").also { binding.userEmail.text = if(it.isNullOrEmpty()) "Empty Email" else it }
                binding.userImage.apply {
                    setImageResource(R.drawable.default_user)
                }
            }
            Snackbar.make(
                binding.main,"You are not connected to the internet",Snackbar.LENGTH_LONG
            ).show()
        }

        val preReleaseUpdates = sharedPreferences.getBoolean("PreReleaseUpdate", false)
        if (preReleaseUpdates.equals(true)) {
            dopamineVersionViewModel.preReleaseUpdate()
            dopamineVersionViewModel.preRelease.observe(this@DopamineUserProfile) {
                if (it is YoutubeResource.Success) {
                    sharedPreferences.edit().apply {
                        putString("PreReleaseVersion", it.data.versionName)
                        putString("PreReleaseUrl", it.data.url)
                        apply()
                    }
                    if (it.data.versionName != Utilities.PRE_RELEASE_VERSION) {
                        createDefaultNotification(
                            applicationContext,
                            it.data.versionName.toString()
                        )
                    }
                }
            }
        }else {
            if (NetworkUtilities.isNetworkAvailable(applicationContext).equals(true)) {
                dopamineVersionViewModel.update.observe(this) { update ->
                    when (update) {
                        is YoutubeResource.Loading -> {}
                        is YoutubeResource.Success -> {
                            sharedPreferences.edit().apply {
                                putString("Version", update.data.versionName)
                                putString("Url", update.data.url)
                                apply()
                            }
                            if (update.data.versionName != Utilities.PROJECT_VERSION) {
                                createDefaultNotification(
                                    applicationContext,
                                    update.data.versionName.toString()
                                )
                            }
                        }

                        is YoutubeResource.Error -> {
                            Snackbar.make(
                                binding.main,
                                "Oh no! Something went wrong",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }

        if(NetworkUtilities.isNetworkAvailable(applicationContext).equals(true)) {
            databaseViewModel.getRecentVideos()

            databaseViewModel.recentVideos.observe(this) { recentVideos ->
                binding.recentWatchHistory.apply {
                    setHasFixedSize(true)
                    layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, binding.recentWatchHistory.horizontalFadingEdgeLength, false)
                    adapter = RecentVideosAdapter(context, recentVideos)
                }
                if (recentVideos.isNullOrEmpty()) {
                    binding.recentWatchHistory.visibility = View.GONE
                }
            }
        }


        binding.topAppBar.setNavigationOnClickListener {
            startActivity(Intent(this, DopamineHome::class.java))
            finish()
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.logout ->{

                    MaterialAlertDialogBuilder(this)
                        .setTitle("Sign out from your account ?")
                        .setIcon(R.drawable.ic_dopamine)
                        .setMessage("Logging out will remove your account from the app and you will not be able to access it's features. To access it, please sign in again 😊")
                        .setCancelable(true)
                        .setPositiveButton("Yes"){
                                dialog, _ ->
                            if(NetworkUtilities.isNetworkAvailable(context = this)) {
                                firebaseAuth.signOut()
                                Toast.makeText(
                                    applicationContext,
                                    "See you soon 🫡",
                                    Toast.LENGTH_LONG
                                ).show()
                                startActivity(
                                    Intent(this, MainActivity::class.java)
                                )
                                dialog.dismiss()
                            }else{
                                Snackbar.make(
                                    binding.main,"Please check your internet connection",Snackbar.LENGTH_LONG
                                ).show()
                            }
                        }
                        .setNegativeButton("No"){
                                dialog, _ ->
                            dialog.dismiss()
                        }
                  .create().show()
                    true
                }
                else -> false
            }
        }

        binding.useExpSearch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked.equals(true)){
                sharedPreferences.edit().putBoolean("ExperimentalSearch", true).apply()
            }else{
                sharedPreferences.edit().putBoolean("ExperimentalSearch", false).apply()
            }
        }

        binding.applyForPreReleaseUpdate.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                MaterialAlertDialogBuilder(this).apply {
                    this.setTitle("Thanks for your interest !")
                    this.setMessage("You are now successfully registered for pre-release update. once you upgrade the app, you will be able to use pre-release feature but you wants to restart the app after you can upgrade it !")
                    this.setIcon(R.drawable.ic_info)
                    this.setCancelable(true)
                    this.setPositiveButton("Restart 🐬") { _, _ ->
                        exitProcess(0)
                    }
                }.create().show()
                sharedPreferences.edit().putBoolean("PreReleaseUpdate", true).apply()
            }else{
                sharedPreferences.edit().putBoolean("PreReleaseUpdate", false).apply()
                Snackbar.make(
                    binding.main,"Application rollback feature is currently unavailable",Snackbar.LENGTH_LONG
                ).show()
            }
        }

        binding.expFeaturesCard.setOnClickListener {
            startActivity(
                Intent(
                    this,ExperimentsMode::class.java
                )
            )
        }

        binding.useExpDynamicUser.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked.equals(true)){
                sharedPreferences.edit().putBoolean("ExperimentalUserColor", true).apply()
                MaterialAlertDialogBuilder(this).apply {
                    this.setTitle("NOTICE")
                    this.setMessage("This feature is currently available in android 12 or above users but you need to restart the app to apply this feature ")
                    this.setIcon(R.drawable.ic_alert)
                    this.setCancelable(true)
                    this.setPositiveButton("Okay") { dialog, _ ->
                        dialog?.dismiss()
                    }
                }.create().show()
            }else{
                sharedPreferences.edit().putBoolean("ExperimentalUserColor", false).apply()
                MaterialAlertDialogBuilder(this).apply {
                    this.setTitle("NOTICE")
                    this.setMessage("This feature is currently available in android 12 or above users but you need to restart the app to apply this feature ")
                    this.setIcon(R.drawable.ic_alert)
                    this.setCancelable(true)
                    this.setPositiveButton("Okay") { dialog, _ ->
                        dialog?.dismiss()
                    }
                }.create().show()
            }
        }

        binding.checkForUpdate.setOnClickListener {
            if(NetworkUtilities.isNetworkAvailable(context = this).equals(true)) {
                if(sharedPreferences.getBoolean("PreReleaseUpdate", false).equals(true)) {
                    if (sharedPreferences.getString("PreReleaseVersion" , "") == Utilities.PRE_RELEASE_VERSION) {
                        MaterialAlertDialogBuilder(this).apply {
                            this.setTitle("Congratulations !")
                            this.setMessage("You are already using the latest pre-release version of Dopamine. Thank you for your interest❤️")
                            this.setIcon(R.drawable.ic_alert)
                            this.setCancelable(true)
                            this.setPositiveButton("Got it !") { dialog, _ ->
                                dialog?.dismiss()
                            }
                        }.create().show()
                    } else {
                       DownloadApk(this@DopamineUserProfile).apply {
                            startDownloadingApk(sharedPreferences.getString("PreReleaseUrl", "")!!)
                       }
                    }
                }else{
                    if (sharedPreferences.getString("Version", "") == Utilities.PROJECT_VERSION) {
                        MaterialAlertDialogBuilder(this).apply {
                            this.setTitle("Wow ! 🫡")
                            this.setMessage("You are already using the latest version of Dopamine . Happy Coding :) ")
                            this.setIcon(R.drawable.ic_alert)
                            this.setCancelable(true)
                            this.setPositiveButton("Okay") { dialog, _ ->
                                dialog?.dismiss()
                            }
                        }.create().show()
                    } else {
                        DownloadApk(this@DopamineUserProfile).apply {
                            startDownloadingApk(sharedPreferences.getString("Url", "")!!)
                        }
                    }
                }
            }else{
                Snackbar.make(
                    binding.main,"Please check your internet connection",Snackbar.LENGTH_LONG
                ).show()
            }
        }

        binding.cardView3.setOnClickListener {
            MaterialAlertDialogBuilder(this).apply {
                this.setTitle("Choose dopamine theme")
                this.setIcon(R.drawable.ic_info)
                this.setSingleChoiceItems(Utilities.THEME,if(sharedPreferences.getString("Theme", Utilities.SYSTEM_MODE) == Utilities.LIGHT_MODE) 0 else if(sharedPreferences.getString("Theme", Utilities.SYSTEM_MODE) == Utilities.DARK_MODE) 1 else 2
                ) { dialog, which ->
                    when(which){
                        0 -> {
                            sharedPreferences.edit().putString("Theme", Utilities.LIGHT_MODE).apply()
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                            dialog.dismiss()
                        }
                        1 -> {
                            sharedPreferences.edit().putString("Theme", Utilities.DARK_MODE).apply()
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            dialog.dismiss()
                        }
                        2 -> {
                            sharedPreferences.edit().putString("Theme", Utilities.SYSTEM_MODE).apply()
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                            dialog.dismiss()
                        }
                    }
                }
                this.setCancelable(true)
            }.create().show()
        }

        binding.cardView4.setOnClickListener{
            AboutUs(context = this).create().show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun createDefaultNotification(
        context: Context, content: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "dopamineUpdateChannel",
                "Update Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val updateApp: PendingIntent =
            PendingIntent.getActivity(this, 0, Intent(
                this,
                DopamineUserProfile::class.java
            ), PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(context, "dopamineUpdateChannel")
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_update)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOnlyAlertOnce(true)
            .addAction(
                R.drawable.ic_update,
                "Update",
                updateApp
            )


        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
            ){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),0)
        }else {
            notificationManager.notify(0, notificationBuilder.build())
        }
    }
}