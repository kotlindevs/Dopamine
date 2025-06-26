package com.google.android.piyush.dopamine.activities

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.databinding.ActivityDopamineHomeBinding
import com.google.android.piyush.dopamine.fragments.Home
import com.google.android.piyush.dopamine.fragments.Explore
import com.google.android.piyush.dopamine.fragments.Trending
import com.google.android.piyush.dopamine.fragments.UserAccount
import com.google.android.piyush.dopamine.viewModels.DopamineHomeViewModel
import kotlin.system.exitProcess

class DopamineHome : AppCompatActivity() {

    private val viewModel : DopamineHomeViewModel by viewModels<DopamineHomeViewModel>()
    private lateinit var binding: ActivityDopamineHomeBinding
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDopamineHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(
            owner = this,
            enabled = true,
            onBackPressed = {
               finish()
                exitProcess(0)
            }
        )

        if (savedInstanceState == null) {
            replaceFragment(Home())
        }

        binding.bottomNavigationView.setOnItemSelectedListener(
            object : NavigationBarView.OnItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when(item.itemId){
                    R.id.home -> {
                        replaceFragment(Home())
                        return true
                    }
                    R.id.explore -> {
                        replaceFragment(Explore())
                        return true
                    }
                    R.id.trending -> {
                        replaceFragment(Trending())
                        return true
                    }
                    R.id.userAccount -> {
                        replaceFragment(UserAccount())
                        return true
                    }
                    else -> {
                        return false
                    }
                }
            }
        })

        NavigationBarView.OnItemReselectedListener { item ->
            when(item.itemId){
                R.id.home -> {
                    replaceFragment(Home())
                }

                R.id.explore -> {
                    replaceFragment(Explore())
                }

                R.id.trending -> {
                    replaceFragment(Trending())
                }

                R.id.userAccount -> {
                    replaceFragment(UserAccount())
                }
            }
        }
    }
    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
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