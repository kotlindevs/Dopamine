package com.google.android.piyush.dopamine.activities

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.databinding.ActivityDopamineHomeBinding
import com.google.android.piyush.dopamine.fragments.Home
import com.google.android.piyush.dopamine.fragments.Search
import com.google.android.piyush.dopamine.fragments.Trending
import com.google.android.piyush.dopamine.fragments.UserAccount
import com.google.android.piyush.dopamine.utilities.NetworkUtilities
import com.google.android.piyush.dopamine.utilities.Utilities
import com.google.android.piyush.dopamine.viewModels.DopamineHomeViewModel
import com.google.android.piyush.dopamine.viewModels.SharedViewModel
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

        onBackPressedDispatcher.addCallback(
            owner = this,
            enabled = true,
            onBackPressed = {
                overridePendingTransition(
                    android.R.anim.fade_in, android.R.anim.fade_out
                )
                finishAffinity()
                finish()
                exitProcess(0)
            }
        )

        if(!NetworkUtilities.isNetworkAvailable(this)){
            Utilities.turnOnNetworkDialog(this,"No Internet Connection")
        }

        if (savedInstanceState == null) {
            replaceFragment(Home(), true)
        }

        binding.bottomNavigationView.addOnButtonCheckedListener(
            object : MaterialButtonToggleGroup.OnButtonCheckedListener{
            override fun onButtonChecked(
                group: MaterialButtonToggleGroup?,
                checkedId: Int,
                isChecked: Boolean
            ) {
                if(isChecked){
                    when(checkedId){
                        R.id.fragmentHome -> {
                            replaceFragment(Home(), true)
                        }
                        R.id.fragmentSearch -> {
                            replaceFragment(Search(), false)
                            }
                        R.id.fragmentTrending -> {
                            replaceFragment(Trending(), false)
                        }
                        R.id.fragmentUserAccount -> {
                            replaceFragment(UserAccount(), false)
                        }
                    }
                }
            }
        })
    }

    private fun replaceFragment(fragment: Fragment, showTooBar : Boolean = true){
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()

        if(showTooBar){
            binding.floatingToolBar.visibility = View.VISIBLE
        }else{
            binding.floatingToolBar.visibility = View.GONE
        }
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