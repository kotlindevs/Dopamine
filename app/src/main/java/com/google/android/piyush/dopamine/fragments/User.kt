package com.google.android.piyush.dopamine.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dcastalia.localappupdate.DownloadApk
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.piyush.database.viewModel.DatabaseViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.activities.AboutUs
import com.google.android.piyush.dopamine.activities.DopamineHome
import com.google.android.piyush.dopamine.activities.MainActivity
import com.google.android.piyush.dopamine.adapters.RecentVideosAdapter
import com.google.android.piyush.dopamine.authentication.repository.UserAuthRepositoryImpl
import com.google.android.piyush.dopamine.beta.youtubedl.DownloadVideo
import com.google.android.piyush.dopamine.beta.youtubedl.StreamVideo
import com.google.android.piyush.dopamine.databinding.FragmentUserBinding
import com.google.android.piyush.dopamine.utilities.NetworkUtilities
import com.google.android.piyush.dopamine.utilities.ToastUtilities
import com.google.android.piyush.dopamine.utilities.Utilities
import com.google.android.piyush.dopamine.utilities.dopamineSharedPreferences
import com.google.android.piyush.youtube.utilities.DopamineVersionViewModel
import com.google.android.piyush.youtube.utilities.YoutubeResource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class User : Fragment() {

    private var userFragment : FragmentUserBinding? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dopamineVersionViewModel: DopamineVersionViewModel
    private lateinit var databaseViewModel: DatabaseViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentUserBinding.bind(view)
        userFragment = binding
        firebaseAuth = FirebaseAuth.getInstance()
        databaseViewModel = DatabaseViewModel(requireContext())
        sharedPreferences = requireContext().getSharedPreferences("DopamineApp", AppCompatActivity.MODE_PRIVATE)

        binding.useLiveSearch.isChecked = sharedPreferences.getBoolean("ExperimentalSearch", false)
        binding.useSystemColor.isChecked = sharedPreferences.getBoolean("ExperimentalUserColor", false)
        binding.installPreReleaseUpdate.isChecked = sharedPreferences.getBoolean("PreReleaseUpdate", false)

        if(NetworkUtilities.isNetworkAvailable(context = requireContext()).equals(true)) {
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
            requireContext().getSharedPreferences("currentUser", AppCompatActivity.MODE_PRIVATE).apply {
                getString("uid","").also { binding.userName.text = if(it.isNullOrEmpty()) "No User Id" else it.substring(0,15) }
                getString("email","").also { binding.userEmail.text = if(it.isNullOrEmpty()) "Empty Email" else it }
                binding.userImage.apply {
                    setImageResource(R.drawable.default_user)
                }
            }
            ToastUtilities.showToast(
                requireContext(),"You are not connected to the internet"
            )
        }

        val preReleaseUpdates = sharedPreferences.getBoolean("PreReleaseUpdate", false)
        if (preReleaseUpdates.equals(true)) {
            dopamineVersionViewModel.preReleaseUpdate()
            dopamineVersionViewModel.preRelease.observe(viewLifecycleOwner) {
                if (it is YoutubeResource.Success) {
                    sharedPreferences.edit().apply {
                        putString("PreReleaseVersion", it.data.versionName)
                        putString("PreReleaseUrl", it.data.url)
                        apply()
                    }
                    if (it.data.versionName != Utilities.PRE_RELEASE_VERSION) {
                        createDefaultNotification(
                            requireContext(),
                            it.data.versionName.toString()
                        )
                    }
                }
            }
        }else {
            if (NetworkUtilities.isNetworkAvailable(requireContext()).equals(true)) {
                dopamineVersionViewModel.update.observe(viewLifecycleOwner) { update ->
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
                                    requireContext(),
                                    update.data.versionName.toString()
                                )
                            }
                        }

                        is YoutubeResource.Error -> {
                            ToastUtilities.showToast(
                                requireContext(),
                                "Oh no! Something went wrong"
                            )
                        }
                    }
                }
            }
        }

        if(NetworkUtilities.isNetworkAvailable(requireContext()).equals(true)) {
            databaseViewModel.getRecentVideos()

            databaseViewModel.recentVideos.observe(viewLifecycleOwner) { recentVideos ->
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

        binding.tryVideoStreaming.setOnClickListener {
            context?.startActivity(
                Intent(
                    requireContext(),StreamVideo::class.java
                )
            )
        }

        binding.tryVideoDownloading.setOnClickListener {
            context?.startActivity(
                Intent(
                    requireContext(),DownloadVideo::class.java
                )
            )
        }

        binding.useLiveSearch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked.equals(true)){
                sharedPreferences.edit().putBoolean("ExperimentalSearch", true).apply()
            }else{
                sharedPreferences.edit().putBoolean("ExperimentalSearch", false).apply()
            }
        }

        binding.installPreReleaseUpdate.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                MaterialAlertDialogBuilder(requireContext()).apply {
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
               ToastUtilities.showToast(
                   requireContext(),"Application rollback feature is currently unavailable"
               )
            }
        }

        binding.useSystemColor.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked.equals(true)){
                sharedPreferences.edit().putBoolean("ExperimentalUserColor", true).apply()
                MaterialAlertDialogBuilder(requireContext()).apply {
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
                MaterialAlertDialogBuilder(requireContext()).apply {
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
            if(NetworkUtilities.isNetworkAvailable(context = requireContext()).equals(true)) {
                if(sharedPreferences.getBoolean("PreReleaseUpdate", false).equals(true)) {
                    if (sharedPreferences.getString("PreReleaseVersion" , "") == Utilities.PRE_RELEASE_VERSION) {
                        MaterialAlertDialogBuilder(requireContext()).apply {
                            this.setTitle("Congratulations !")
                            this.setMessage("You are already using the latest pre-release version of Dopamine. Thank you for your interest❤️")
                            this.setIcon(R.drawable.ic_alert)
                            this.setCancelable(true)
                            this.setPositiveButton("Got it !") { dialog, _ ->
                                dialog?.dismiss()
                            }
                        }.create().show()
                    } else {
                        DownloadApk(requireContext()).apply {
                            startDownloadingApk(sharedPreferences.getString("PreReleaseUrl", "")!!)
                        }
                    }
                }else{
                    if (sharedPreferences.getString("Version", "") == Utilities.PROJECT_VERSION) {
                        MaterialAlertDialogBuilder(requireContext()).apply {
                            this.setTitle("Wow ! 🫡")
                            this.setMessage("You are already using the latest version of Dopamine . Happy Coding :) ")
                            this.setIcon(R.drawable.ic_alert)
                            this.setCancelable(true)
                            this.setPositiveButton("Okay") { dialog, _ ->
                                dialog?.dismiss()
                            }
                        }.create().show()
                    } else {
                        DownloadApk(requireContext()).apply {
                            startDownloadingApk(sharedPreferences.getString("Url", "")!!)
                        }
                    }
                }
            }else{
                ToastUtilities.showToast(
                    requireContext(),"Please check your internet connection"
                )
            }
        }

        val theme = sharedPreferences.getString("Theme", Utilities.SYSTEM_MODE)
        binding.useSystemThemeText.text = theme

        binding.useSystemTheme.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext()).apply {
                this.setTitle("Choose dopamine theme")
                this.setIcon(R.drawable.ic_info)
                this.setSingleChoiceItems(Utilities.THEME,if(sharedPreferences.getString("Theme", Utilities.SYSTEM_MODE) == Utilities.LIGHT_MODE) 0 else if(sharedPreferences.getString("Theme", Utilities.SYSTEM_MODE) == Utilities.DARK_MODE) 1 else 2
                ) { dialog, which ->
                    when(which){
                        0 -> {
                            sharedPreferences.edit().putString("Theme", Utilities.LIGHT_MODE).apply()
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                            dialog.dismiss()
                            binding.useSystemThemeText.text = Utilities.LIGHT_MODE
                        }
                        1 -> {
                            sharedPreferences.edit().putString("Theme", Utilities.DARK_MODE).apply()
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            dialog.dismiss()
                            binding.useSystemThemeText.text = Utilities.DARK_MODE
                        }
                        2 -> {
                            sharedPreferences.edit().putString("Theme", Utilities.SYSTEM_MODE).apply()
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                            dialog.dismiss()
                            binding.useSystemThemeText.text = Utilities.SYSTEM_MODE
                        }
                    }
                }
                this.setCancelable(true)
            }.create().show()
        }

        binding.viewAboutUs.setOnClickListener{
            AboutUs(context = requireContext()).create().show()
        }
        val regionPref = dopamineSharedPreferences(requireContext())
        val region = regionPref.getString("region", "").toString()
        if(region.isNotEmpty()){
            binding.currentRegionText.text = region
        }else{
            binding.currentRegionText.text = "No Region Selected"
        }

        binding.currentRegion.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext()).apply {
                this.setTitle("Select your home region")
                this.setIcon(R.drawable.home_region)
                this.setSingleChoiceItems(
                    Utilities.REGIONS,
                    if (regionPref.getString("region", "") == Utilities.DEFAULT_REGION[0]) 0
                    else if (regionPref.getString("region", "") == Utilities.ARGENTINA[0]) 1
                    else if (regionPref.getString("region", "") == Utilities.AUSTRALIA[0]) 2
                    else if (regionPref.getString("region", "") == Utilities.BANGLADESH[0]) 3
                    else if (regionPref.getString("region", "") == Utilities.BRAZIL[0]) 4
                    else if (regionPref.getString("region", "") == Utilities.BHUTAN[0]) 5
                    else if (regionPref.getString("region", "") == Utilities.CANADA[0]) 6
                    else if (regionPref.getString("region", "") == Utilities.CHINA[0]) 7
                    else if (regionPref.getString("region", "") == Utilities.COLOMBIA[0]) 8
                    else if (regionPref.getString("region", "") == Utilities.DENMARK[0]) 9
                    else if (regionPref.getString("region", "") == Utilities.EGYPT[0]) 10
                    else if (regionPref.getString("region", "") == Utilities.FRANCE[0]) 11
                    else if (regionPref.getString("region", "") == Utilities.GERMANY[0]) 12
                    else if (regionPref.getString("region", "") == Utilities.HONG_KONG[0]) 13
                    else if (regionPref.getString("region", "") == Utilities.ISRAEL[0]) 14
                    else if (regionPref.getString("region", "") == Utilities.ITALY[0]) 15
                    else if (regionPref.getString("region", "") == Utilities.IRAN[0]) 16
                    else if (regionPref.getString("region", "") == Utilities.JAPAN[0]) 17
                    else if (regionPref.getString("region", "") == Utilities.JERSEY[0]) 18
                    else if (regionPref.getString("region", "") == Utilities.KENYA[0]) 19
                    else if (regionPref.getString("region", "") == Utilities.KOREA[0]) 20
                    else if (regionPref.getString("region", "") == Utilities.LEBANON[0]) 21
                    else if (regionPref.getString("region", "") == Utilities.MALAYSIA[0]) 22
                    else if (regionPref.getString("region", "") == Utilities.MALDIVES[0]) 23
                    else if (regionPref.getString("region", "") == Utilities.MEXICO[0]) 24
                    else if (regionPref.getString("region", "") == Utilities.MONGOLIA[0]) 25
                    else if (regionPref.getString("region", "") == Utilities.MYANMAR[0]) 26
                    else if (regionPref.getString("region", "") == Utilities.NETHERLANDS[0]) 27
                    else if (regionPref.getString("region", "") == Utilities.NEPAL[0]) 28
                    else if (regionPref.getString("region", "") == Utilities.NEW_ZEALAND[0]) 29
                    else if (regionPref.getString("region", "") == Utilities.NIGERIA[0]) 30
                    else if (regionPref.getString("region", "") == Utilities.NORWAY[0]) 31
                    else if (regionPref.getString("region", "") == Utilities.PAKISTAN[0]) 32
                    else if (regionPref.getString("region", "") == Utilities.PANAMA[0]) 33
                    else if (regionPref.getString("region", "") == Utilities.PARAGUAY[0]) 34
                    else if (regionPref.getString("region", "") == Utilities.PERU[0]) 35
                    else if (regionPref.getString("region", "") == Utilities.PHILIPPINES[0]) 36
                    else if (regionPref.getString("region", "") == Utilities.POLAND[0]) 37
                    else if (regionPref.getString("region", "") == Utilities.PORTUGAL[0]) 38
                    else if (regionPref.getString("region", "") == Utilities.RUSSIA[0]) 39
                    else if (regionPref.getString("region", "") == Utilities.ROMANIA[0]) 40
                    else if (regionPref.getString("region", "") == Utilities.SAUDI_ARABIA[0]) 41
                    else if (regionPref.getString("region", "") == Utilities.SINGAPORE[0]) 42
                    else if (regionPref.getString("region", "") == Utilities.SPAIN[0]) 43
                    else if (regionPref.getString("region", "") == Utilities.SRI_LANKA[0]) 44
                    else if (regionPref.getString("region", "") == Utilities.SWEDEN[0]) 45
                    else if (regionPref.getString("region", "") == Utilities.SWITZERLAND[0]) 46
                    else if (regionPref.getString("region", "") == Utilities.TAIWAN[0]) 47
                    else if (regionPref.getString("region", "") == Utilities.UKRAINE[0]) 48
                    else if (regionPref.getString("region", "") == Utilities.UNITED_KINGDOM[0]) 49
                    else if (regionPref.getString("region", "") == Utilities.UNITED_STATES[0]) 50
                    else if (regionPref.getString("region", "") == Utilities.ZIMBABWE[0]) 51
                    else 0
                ) { _, which ->
                    when (which) {
                        0 -> regionPref.edit().putString("region", Utilities.DEFAULT_REGION[0]).apply()
                        1 -> {
                            regionPref.edit().putString("region", Utilities.ARGENTINA[0]).apply()
                            ToastUtilities.showToast(
                                requireContext(),
                                "Service not available yet for this region"
                            )
                        }
                        2 -> regionPref.edit().putString("region", Utilities.AUSTRALIA[0]).apply()
                        3 -> regionPref.edit().putString("region", Utilities.BANGLADESH[0]).apply()
                        4 -> regionPref.edit().putString("region", Utilities.BRAZIL[0]).apply()
                        5 -> {
                            regionPref.edit().putString("region", Utilities.BHUTAN[0]).apply()
                            ToastUtilities.showToast(
                                requireContext(),
                                "Service not available yet for this region"
                            )
                        }
                        6 -> regionPref.edit().putString("region", Utilities.CANADA[0]).apply()
                        7 -> {
                            regionPref.edit().putString("region", Utilities.CHINA[0]).apply()
                            ToastUtilities.showToast(
                                requireContext(),
                                "Service not available yet for this region"
                            )
                        }
                        8 -> regionPref.edit().putString("region", Utilities.COLOMBIA[0]).apply()
                        9 -> regionPref.edit().putString("region", Utilities.DENMARK[0]).apply()
                        10 -> regionPref.edit().putString("region", Utilities.EGYPT[0]).apply()
                        11 -> regionPref.edit().putString("region", Utilities.FRANCE[0]).apply()
                        12 -> regionPref.edit().putString("region", Utilities.GERMANY[0]).apply()
                        13 -> regionPref.edit().putString("region", Utilities.HONG_KONG[0]).apply()
                        14 -> regionPref.edit().putString("region", Utilities.ISRAEL[0]).apply()
                        15 -> regionPref.edit().putString("region", Utilities.ITALY[0]).apply()
                        16 -> {
                            regionPref.edit().putString("region", Utilities.IRAN[0]).apply()
                            ToastUtilities.showToast(
                                requireContext(),
                                "Service not available yet for this region"
                            )
                        }
                        17 -> regionPref.edit().putString("region", Utilities.JAPAN[0]).apply()
                        18 -> {
                            regionPref.edit().putString("region", Utilities.JERSEY[0]).apply()
                            ToastUtilities.showToast(
                                requireContext(),
                                "Service not available yet for this region"
                            )
                        }
                        19 -> regionPref.edit().putString("region", Utilities.KENYA[0]).apply()
                        20 -> regionPref.edit().putString("region", Utilities.KOREA[0]).apply()
                        21 -> regionPref.edit().putString("region", Utilities.LEBANON[0]).apply()
                        22 -> regionPref.edit().putString("region", Utilities.MALAYSIA[0]).apply()
                        23 -> {
                            regionPref.edit().putString("region", Utilities.MALDIVES[0]).apply()
                            ToastUtilities.showToast(
                                requireContext(),
                                "Service not available yet for this region"
                            )
                        }
                        24 -> regionPref.edit().putString("region", Utilities.MEXICO[0]).apply()
                        25 -> regionPref.edit().putString("region", Utilities.MONGOLIA[0]).apply()
                        26 -> {
                            regionPref.edit().putString("region", Utilities.MYANMAR[0]).apply()
                            ToastUtilities.showToast(
                                requireContext(),
                                "Service not available yet for this region"
                            )
                        }
                        27 -> regionPref.edit().putString("region", Utilities.NETHERLANDS[0]).apply()
                        28 -> regionPref.edit().putString("region", Utilities.NEPAL[0]).apply()
                        29 -> regionPref.edit().putString("region", Utilities.NEW_ZEALAND[0]).apply()
                        30 -> regionPref.edit().putString("region", Utilities.NIGERIA[0]).apply()
                        31 -> regionPref.edit().putString("region", Utilities.NORWAY[0]).apply()
                        32 -> regionPref.edit().putString("region", Utilities.PAKISTAN[0]).apply()
                        33 -> regionPref.edit().putString("region", Utilities.PANAMA[0]).apply()
                        34 -> regionPref.edit().putString("region", Utilities.PARAGUAY[0]).apply()
                        35 -> regionPref.edit().putString("region", Utilities.PERU[0]).apply()
                        36 -> regionPref.edit().putString("region", Utilities.PHILIPPINES[0]).apply()
                        37 -> regionPref.edit().putString("region", Utilities.POLAND[0]).apply()
                        38 -> regionPref.edit().putString("region", Utilities.PORTUGAL[0]).apply()
                        39 -> regionPref.edit().putString("region", Utilities.RUSSIA[0]).apply()
                        40 -> regionPref.edit().putString("region", Utilities.ROMANIA[0]).apply()
                        41 -> regionPref.edit().putString("region", Utilities.SAUDI_ARABIA[0]).apply()
                        42 -> regionPref.edit().putString("region", Utilities.SINGAPORE[0]).apply()
                        43 -> regionPref.edit().putString("region", Utilities.SPAIN[0]).apply()
                        44 -> regionPref.edit().putString("region", Utilities.SRI_LANKA[0]).apply()
                        45 -> regionPref.edit().putString("region", Utilities.SWEDEN[0]).apply()
                        46 -> regionPref.edit().putString("region", Utilities.SWITZERLAND[0]).apply()
                        47 -> regionPref.edit().putString("region", Utilities.TAIWAN[0]).apply()
                        48 -> regionPref.edit().putString("region", Utilities.UKRAINE[0]).apply()
                        49 -> regionPref.edit().putString("region", Utilities.UNITED_KINGDOM[0]).apply()
                        50 -> regionPref.edit().putString("region", Utilities.UNITED_STATES[0]).apply()
                        51 -> regionPref.edit().putString("region", Utilities.ZIMBABWE[0]).apply()
                    }
                }
                this.setCancelable(true)
                this.setPositiveButton("Save") { dialog, _ ->
                    regionPref.edit().putBoolean("saveRegion", true).apply()
                    binding.currentRegionText.text = regionPref.getString("region", "")
                    dialog.dismiss()
                }
                this.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
            }.create().show()
        }

        binding.googleSignOut.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sign out from your account ?")
                .setIcon(R.drawable.ic_dopamine)
                .setMessage("Logging out will remove your account from the app and you will not be able to access it's features. To access it, please sign in again 😊")
                .setCancelable(true)
                .setPositiveButton("Yes"){
                        dialog, _ ->
                    if(NetworkUtilities.isNetworkAvailable(context = requireContext())) {
                        firebaseAuth.signOut()
                        lifecycleScope.launch {
                           UserAuthRepositoryImpl(
                               requireContext()
                           ).signOut()
                        }
                        ToastUtilities.showToast(
                            requireContext(),"You have successfully signed out from your account"
                        )
                        startActivity(
                            Intent(requireContext(), MainActivity::class.java)
                        )
                        dialog.dismiss()
                    }else{
                        ToastUtilities.showToast(requireContext(),"Please check your internet connection")
                    }
                }
                .setNegativeButton("No"){
                        dialog, _ ->
                    dialog.dismiss()
                }
                .create().show()
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
            PendingIntent.getActivity(requireContext(), 0, Intent(
                requireContext(),
                DopamineHome::class.java
            ), PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(context, "dopamineUpdateChannel")
            .setContentTitle("Update Available")
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
        if(ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),0)
        }else {
            notificationManager.notify(0, notificationBuilder.build())
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        userFragment = null
    }
}