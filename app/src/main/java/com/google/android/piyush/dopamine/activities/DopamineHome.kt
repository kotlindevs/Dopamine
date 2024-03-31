package com.google.android.piyush.dopamine.activities

import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
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
import com.google.android.piyush.dopamine.viewModels.DopamineHomeViewModel
import com.google.android.piyush.dopamine.viewModels.SharedViewModel
import com.google.android.piyush.youtube.utilities.NotificationViewModel
import com.google.android.piyush.youtube.utilities.YoutubeResource
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
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
        }

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
                            binding.bottomNavigationView.getOrCreateBadge(R.id.home).apply {
                                number = newNotifications.size
                                isVisible = true
                                badgeGravity = BadgeDrawable.TOP_END
                            }
                        }
                    }
                }
                is YoutubeResource.Error -> {
                    ToastUtilities.showToast(this, notifications.exception.message.toString())
                }

            }
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    defaultScreen(Home())
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