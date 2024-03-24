package com.google.android.piyush.dopamine.activities

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.dcastalia.localappupdate.DownloadApk
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.authentication.utilities.SignInUtils
import com.google.android.piyush.dopamine.databinding.ActivityDopamineUserProfileBinding
import com.google.android.piyush.dopamine.utilities.CustomDialog
import com.google.android.piyush.dopamine.utilities.NetworkUtilities
import com.google.android.piyush.dopamine.utilities.Utilities
import com.google.android.piyush.youtube.utilities.DopamineVersionViewModel
import com.google.android.piyush.youtube.utilities.YoutubeResource
import com.google.firebase.auth.FirebaseAuth

class DopamineUserProfile : AppCompatActivity() {

    private lateinit var binding: ActivityDopamineUserProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dopamineVersionViewModel: DopamineVersionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDopamineUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
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

        if(NetworkUtilities.isNetworkAvailable(context = this).equals(true)) {
            dopamineVersionViewModel = DopamineVersionViewModel()
            if (firebaseAuth.currentUser?.email.isNullOrEmpty()) {
                Glide.with(this).load(SignInUtils.DEFAULT_IMAGE).into(binding.userImage)
                binding.userName.text = getString(R.string.app_name)
                binding.userEmail.text = firebaseAuth.currentUser?.phoneNumber
            } else {
                Glide.with(this).load(firebaseAuth.currentUser?.photoUrl).into(binding.userImage)
                binding.userName.text = firebaseAuth.currentUser?.displayName
                binding.userEmail.text = firebaseAuth.currentUser?.email
            }
        }else{
            Utilities.turnOnNetworkDialog(context = this,"full user profile")
            applicationContext.getSharedPreferences("currentUser", MODE_PRIVATE).apply {
                getString("name","").also { binding.userName.text = it }
                getString("email","").also { binding.userEmail.text = it }
                binding.userImage.apply {
                    setImageResource(R.drawable.default_user)
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

        false.also {
            binding.cardView2.isEnabled = it
            binding.applyForPreReleaseUpdate.isEnabled = it
            binding.applyForPreRelease.isEnabled = it
        }

        /*
        binding.applyForPreReleaseUpdate.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked.equals(true)){
                sharedPreferences.edit().putBoolean("PreReleaseUpdate", true).apply()

                if(sharedPreferences.getBoolean("PreReleaseUpdate", false).equals(true)) {
                    dopamineVersionViewModel.preReleaseUpdate()
                    dopamineVersionViewModel.preRelease.observe(this) {
                        if (it is YoutubeResource.Success) {
                            if (it.data.versionName == Utilities.PRE_RELEASE_VERSION) {
                                if (sharedPreferences.getBoolean("ExperimentalPreUpdate", false).equals(true)) {
                                    MaterialAlertDialogBuilder(this).apply {
                                        this.setTitle(it.data.versionName)
                                        this.setMessage(it.data.changelog)
                                        this.setIcon(R.drawable.ic_info)
                                        this.setCancelable(true)
                                        this.setPositiveButton("Okay") { dialog, _ ->
                                            dialog?.dismiss()
                                        }
                                    }.create().show()
                                } else {
                                    MaterialAlertDialogBuilder(this).apply {
                                        this.setTitle("Thanks for your interest !")
                                        this.setMessage("You successfully registered for pre-release update. once you upgrade the app, you will be able to use pre-release feature.")
                                        this.setIcon(R.drawable.ic_info)
                                        this.setCancelable(true)
                                        this.setPositiveButton("Don't show again") { _, _ ->
                                            context.getSharedPreferences(
                                                "DopamineApp",
                                                MODE_PRIVATE
                                            )
                                                .edit().putBoolean("ExperimentalPreUpdate", true).apply()
                                        }
                                    }.create().show()
                                }
                            } else {
                                val downloadApk = DownloadApk(this@DopamineUserProfile)
                                downloadApk.startDownloadingApk(it.data.url.toString())
                            }
                        }

                    }
                }
            }else{
                sharedPreferences.edit().putBoolean("PreReleaseUpdate", false).apply()
                Snackbar.make(
                    binding.main,"Application rollback feature is currently unavailable",Snackbar.LENGTH_LONG
                ).show()
            }
        } */

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
                dopamineVersionViewModel.update.observe(this) { update ->
                    when (update) {
                        is YoutubeResource.Loading -> {}
                        is YoutubeResource.Success -> {
                            if (update.data.versionName == Utilities.PROJECT_VERSION) {
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
                                val downloadApk = DownloadApk(this@DopamineUserProfile)
                                downloadApk.startDownloadingApk(update.data.url.toString())
                            }
                            Log.d(ContentValues.TAG, update.data.toString())
                        }

                        is YoutubeResource.Error -> {
                            Log.d(ContentValues.TAG, update.exception.message.toString())
                        }
                    }
                }
            }else{
                Snackbar.make(
                    binding.main,"Please check your internet connection",Snackbar.LENGTH_LONG
                ).show()
            }
        }

        binding.customPlayList.setOnClickListener {
            val bottomSheetFragment = MyBottomSheetFragment()
            bottomSheetFragment.show(supportFragmentManager,bottomSheetFragment.tag)
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

    class MyBottomSheetFragment : BottomSheetDialogFragment(){
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {

            val view = inflater.inflate(R.layout.bottom_sheet_add_playlist,container,false)

            val createPlaylist = view.findViewById<MaterialButton>(R.id.btCreatePlaylist)
            createPlaylist.setOnClickListener {
                val customDialog = CustomDialog(requireContext())
                customDialog.show()
            }
            return view
        }

    }
}