package com.google.android.piyush.dopamine.activities

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.google.android.material.badge.BadgeDrawable
import com.google.android.piyush.database.viewModel.DatabaseViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.databinding.ActivityDopamineHomeBinding
import com.google.android.piyush.dopamine.fragments.ExperimentalSearch
import com.google.android.piyush.dopamine.fragments.Home
import com.google.android.piyush.dopamine.fragments.Library
import com.google.android.piyush.dopamine.fragments.Search
import com.google.android.piyush.dopamine.fragments.Shorts
import com.google.android.piyush.dopamine.fragments.User
import com.google.android.piyush.dopamine.utilities.NetworkUtilities
import com.google.android.piyush.dopamine.utilities.ToastUtilities
import com.google.android.piyush.dopamine.utilities.Utilities
import com.google.android.piyush.dopamine.utilities.dopamineSharedPreferences
import com.google.android.piyush.dopamine.viewModels.DopamineHomeViewModel
import com.google.android.piyush.dopamine.viewModels.SharedViewModel
import com.google.android.piyush.youtube.utilities.NotificationViewModel
import com.google.android.piyush.youtube.utilities.YoutubeResource
import me.leolin.shortcutbadger.ShortcutBadger
import kotlin.system.exitProcess

@Suppress("DEPRECATION")
class DopamineHome : AppCompatActivity() {

    private val viewModel : DopamineHomeViewModel by viewModels<DopamineHomeViewModel>()
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var binding: ActivityDopamineHomeBinding
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDopamineHomeBinding.inflate(layoutInflater)
        sharedViewModel = SharedViewModel()
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback {
            overridePendingTransition(
                android.R.anim.fade_in, android.R.anim.fade_out
            )
            finishAffinity()
            finish()
            exitProcess(0)
        }

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }

        val theme = dopamineSharedPreferences(applicationContext).getString("Theme", "")
        if(theme.isNullOrEmpty()){
            dopamineSharedPreferences(context = applicationContext).edit().putString("Theme", Utilities.SYSTEM_MODE).apply()
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        if(!NetworkUtilities.isNetworkAvailable(this)){
            Utilities.turnOnNetworkDialog(this,"No Internet Connection")
        }

        if (savedInstanceState == null) {
            defaultScreen(Home())
        }

        if(intent.getBooleanExtra("fromSettings",false).equals(true)){
            defaultScreen(User())
            binding.bottomNavigationView.selectedItemId = R.id.user
        }else if(intent.getBooleanExtra("userSignedIn",false).equals(true)){
            defaultScreen(User())
            binding.bottomNavigationView.selectedItemId = R.id.user
        }else if(intent.getBooleanExtra("userSignedOut",false).equals(true)){
            defaultScreen(User())
            binding.bottomNavigationView.selectedItemId = R.id.user
        }else if(intent.getBooleanExtra("userWantToSignIn",false).equals(true)){
            defaultScreen(User())
            binding.bottomNavigationView.selectedItemId = R.id.user
        }else if(intent.getBooleanExtra("fromPlaylistManager",false).equals(true)){
            defaultScreen(User())
            binding.bottomNavigationView.selectedItemId = R.id.user
        }else if(intent.getBooleanExtra("userDeleteAccount",false).equals(true)){
            defaultScreen(User())
            binding.bottomNavigationView.selectedItemId = R.id.user
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    defaultScreen(Home())
                    val notificationViewModel = NotificationViewModel()
                    val databaseViewModel = DatabaseViewModel(this)
                    notificationViewModel.notifications.observe(this){ notifications ->
                        when(notifications){
                            is YoutubeResource.Loading -> {}
                            is YoutubeResource.Success -> {
                                databaseViewModel.initializeNotifications()
                                val oldNotifications = databaseViewModel.getListOfNotifications()
                                if(oldNotifications.isNotEmpty()){
                                    val newNotifications = notifications.data.subtract(
                                        oldNotifications.toSet()
                                    )
                                    if(newNotifications.isNotEmpty()) {
                                        binding.bottomNavigationView.getOrCreateBadge(R.id.home)
                                            .apply {
                                                number = newNotifications.size
                                                isVisible = true
                                                badgeGravity = BadgeDrawable.TOP_END
                                            }
                                        allNotifications(
                                            applicationContext,
                                            newNotifications.toTypedArray()[0].title!!,
                                            newNotifications.toTypedArray()[0].description!!,
                                        )
                                        ShortcutBadger.applyCount(
                                            this,
                                            newNotifications.size
                                        )
                                    }

                                    if(newNotifications.isEmpty()){
                                        binding.bottomNavigationView.getOrCreateBadge(R.id.home)
                                            .apply {
                                                number = 0
                                                isVisible = false
                                            }
                                        ShortcutBadger.removeCount(
                                            this
                                        )
                                    }
                                }
                            }
                            is YoutubeResource.Error -> {
                                ToastUtilities.showToast(this, notifications.exception.message.toString())
                            }

                        }
                    }
                    true
                }
                R.id.search -> {
                    if(getSharedPreferences("DopamineApp", MODE_PRIVATE).getBoolean("ExperimentalSearch", false)){
                        defaultScreen(ExperimentalSearch())
                    }else{
                        defaultScreen(Search())
                    }
                    true
                }
                R.id.library -> {
                    defaultScreen(Library())
                    true
                }
                R.id.shorts -> {
                    defaultScreen(Shorts())
                    true
                }
                R.id.user -> {
                    defaultScreen(User())
                    true
                }

                else -> false
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun allNotifications(
        context: Context, title: String, content: String ,channelId : String = "allNotifications") {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "All Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notifyIntent = Intent(this, AppNotificationView::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val notifyPendingIntent = PendingIntent.getActivity(
            this, 0, notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOnlyAlertOnce(false)
            .addAction(
                R.drawable.ic_notification,
                "View All",
                notifyPendingIntent
            )
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(ActivityCompat.checkSelfPermission(context,android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(context as Activity, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),0)
        }else {
            notificationManager.notify(0, notificationBuilder)
        }
    }

    private fun defaultScreen(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.selectedFragment.value?.let {
            outState.putInt("selectedFragment", it)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        viewModel.setSelectedFragment(
            savedInstanceState.getInt("selectedFragment")
        )
    }
}