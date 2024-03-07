package com.google.android.piyush.dopamine.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.databinding.ActivityDopamineHomeBinding
import com.google.android.piyush.dopamine.fragments.Home
import com.google.android.piyush.dopamine.fragments.Library
import com.google.android.piyush.dopamine.fragments.Search
import com.google.android.piyush.dopamine.fragments.Shorts
import com.google.android.piyush.dopamine.viewModels.DopamineHomeViewModel

class DopamineHome : AppCompatActivity() {

    private val viewModel : DopamineHomeViewModel by viewModels<DopamineHomeViewModel>()
    private lateinit var binding: ActivityDopamineHomeBinding
    private var backPressed = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDopamineHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        onBackPressedDispatcher.addCallback {
            if(backPressed!=true){
                Toast.makeText(applicationContext,"Press back again to exit", Toast.LENGTH_SHORT).show()
                backPressed = true
            }else{
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finishAffinity()
                finish()
                System.exit(0)
            }
        }

        if (savedInstanceState == null) {
            defaultScreen(Home())
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    defaultScreen(Home())
                    true
                }
                R.id.search -> {
                    defaultScreen(Search())
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