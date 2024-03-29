package com.google.android.piyush.dopamine.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
import com.google.android.piyush.dopamine.authentication.viewModel.UserAuthViewModel
import com.google.android.piyush.dopamine.beta.ExperimentsMode
import com.google.android.piyush.dopamine.databinding.FragmentUserBinding
import com.google.android.piyush.dopamine.utilities.NetworkUtilities
import com.google.android.piyush.dopamine.utilities.ToastUtilities
import com.google.android.piyush.dopamine.utilities.Utilities
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