package com.google.android.piyush.dopamine.activities

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.piyush.dopamine.R
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
        binding.useExpDynamicUser.isChecked = sharedPreferences.getBoolean("ExperimentalUserColor", false)
        val storageInfo = getStorageInfo()
        val ramInfo = getRAMInfo()
        binding.deviceStorageInfoTxt.text = storageInfo
        binding.deviceRamInfoTxt.text = ramInfo

        binding.deviceStorage.setOnClickListener{
            val intentStorage = Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS)
            startActivity(intentStorage)
        }

        binding.deviceRam.setOnClickListener{
            val intentStorage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                Intent(Settings.ACTION_ADVANCED_MEMORY_PROTECTION_SETTINGS)
            } else {
                TODO("VERSION.SDK_INT < UPSIDE_DOWN_CAKE")
            }
            startActivity(intentStorage)
        }

        onBackPressedDispatcher.addCallback {
            startActivity(Intent(this@DopamineUserProfile, DopamineHome::class.java))
        }

        if(NetworkUtilities.isNetworkAvailable(context = this)) {
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
        if (preReleaseUpdates) {
            dopamineVersionViewModel.preReleaseUpdate()
            dopamineVersionViewModel.preRelease.observe(this@DopamineUserProfile) {
                if (it is YoutubeResource.Success) {
                    sharedPreferences.edit().apply {
                        putString("PreReleaseVersion", it.data.versionName)
                        putString("PreReleaseUrl", it.data.url)
                        apply()
                    }
                    if (it.data.versionName != Utilities.PRE_RELEASE_VERSION) {
                        Toast.makeText(this,"Under Development",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }else {
            if (
                NetworkUtilities.isNetworkAvailable(applicationContext)) {
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
                                Toast.makeText(this,"Under Development",Toast.LENGTH_SHORT).show()
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

        binding.useExpDynamicUser.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                sharedPreferences.edit { putBoolean("ExperimentalUserColor", true) }
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
                sharedPreferences.edit { putBoolean("ExperimentalUserColor", false) }
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
                            sharedPreferences.edit { putString("Theme", Utilities.LIGHT_MODE) }
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                            dialog.dismiss()
                        }
                        1 -> {
                            sharedPreferences.edit { putString("Theme", Utilities.DARK_MODE) }
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            dialog.dismiss()
                        }
                        2 -> {
                            sharedPreferences.edit { putString("Theme", Utilities.SYSTEM_MODE) }
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

    private fun getStorageInfo(): String {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val path = Environment.getExternalStorageDirectory().absolutePath
            val statFs = StatFs(path)

            val blockSize = statFs.blockSizeLong
            val totalBlocks = statFs.blockCountLong
            val availableBlocks = statFs.availableBlocksLong

            val totalStorage = formatSize(blockSize * totalBlocks)
            val availableStorage = formatSize(blockSize * availableBlocks)
            val usedStorage = formatSize(blockSize * (totalBlocks - availableBlocks))

            return "\n Total Storage: $totalStorage\n\n Used Storage: $usedStorage\n\n Available Storage: $availableStorage \n"
        } else {
            return "External storage is not available."
        }

    }

    private fun getRAMInfo(): String {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val totalMemory = memoryInfo.totalMem
        val availableMemory = memoryInfo.availMem
        val usedMemory = totalMemory - availableMemory

        return "\nTotal RAM: ${formatSize(totalMemory)}\n\n" +
                "Used RAM: ${formatSize(usedMemory)}\n\n" +
                "Available RAM: ${formatSize(availableMemory)} \n"
    }

    @SuppressLint("DefaultLocale")
    private fun formatSize(size: Long): String {
        val kb = 1024L
        val mb = kb * 1024
        val gb = mb * 1024

        return when {
            size >= gb -> String.format("%.2f GB", size.toDouble() / gb)
            size >= mb -> String.format("%.2f MB", size.toDouble() / mb)
            size >= kb -> String.format("%.2f KB", size.toDouble() / kb)
            else -> String.format("%d bytes", size)
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