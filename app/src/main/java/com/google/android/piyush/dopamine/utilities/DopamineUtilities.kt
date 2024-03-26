package com.google.android.piyush.dopamine.utilities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.piyush.database.model.CustomPlaylistView
import com.google.android.piyush.database.viewModel.DatabaseViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.databinding.ItemCustomDialogBinding

object NetworkUtilities {
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null
    }
    fun showNetworkError(context: Context?) {
        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context!!)
            .setIcon(R.mipmap.ic_launcher)
            .setTitle("Error!")
            .setCancelable(false)
            .setMessage("No Internet Connection.")
            .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
        materialAlertDialogBuilder.create().show()
    }
}

object ToastUtilities {
    fun showToast(context: Context?, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

object Utilities {
const val PERMISSION_REQUEST_CODE = 100
    const val PROCESS_ID = "MyDlProcess"
    const val PROJECT_ID = "com.google.android.piyush.dopamine"
    const val RELEASE_DATE = "26/03/2024"
    const val PROJECT_VERSION = "dopamine_20242603_01.phone.stable.dynamic"
    const val STABLE = "stable"
    const val DEFAULT_LOGO = "https://cdn-images-1.medium.com/v2/resize:fit:1200/1*3tLD4Ve66pbBpuawm9Fu9Q.png"
    const val DEFAULT_BANNER = "https://developer.android.com/static/images/social/android-developers.png"
    const val LIGHT_MODE = "light"
    const val DARK_MODE = "dark"
    const val SYSTEM_MODE = "system"
    val THEME = arrayOf(
        LIGHT_MODE,
        DARK_MODE,
        SYSTEM_MODE
    )
    const val GITHUB = "https://github.com/kotlindevs/dopamine"
    const val EMAIL = "kotlindevslife@gmail.com"
    const val EMAIL1 = "piyushmakwana5617@gmail.com"


    fun turnOnNetworkDialog(context: Context, message: String) = MaterialAlertDialogBuilder(context).also {
        it.setTitle("Network not detected")
        it.setMessage("Please turn on network to view the $message.")
        it.setIcon(R.drawable.wifi_off)
        it.setCancelable(true)
        it.setNegativeButton("Cancel") {
            dialog, _ ->
            dialog.dismiss()
        }
        it.setPositiveButton("Turn on") { dialog, _ ->
            dialog.dismiss()
            Intent(Settings.ACTION_WIRELESS_SETTINGS).also { intent ->
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        }
    }.create().show()
}

class CustomDialog(context: Context) : MaterialAlertDialogBuilder(context) {
    private var binding: ItemCustomDialogBinding
    private var databaseViewModel: DatabaseViewModel
    init {
        setCancelable(true)
        databaseViewModel = DatabaseViewModel(context)
        binding = ItemCustomDialogBinding.inflate(LayoutInflater.from(context)).also {
            setView(it.root)
        }
        val playlistName = binding.text1.text
        val playlistDescription = binding.text2.text

        binding.button.setOnClickListener {
            if(databaseViewModel.isPlaylistExist(playlistName.toString()).equals(true)){
                //insert in playlist
            }else{
                if(playlistName.toString().isEmpty()){
                    ToastUtilities.showToast(context, "Please Fill All Fields")
                }else {
                    databaseViewModel.createCustomPlaylist(
                        CustomPlaylistView(
                            playlistName.toString(),
                            playlistDescription.toString().ifEmpty { "Empty Description" },
                        )
                    )
                    playlistName?.clear()
                    playlistDescription?.clear()
                    ToastUtilities.showToast(context, "$playlistName Created ✅")
                }
            }
        }
    }
}

object Developers{
    const val PIYUSH = "devPiyush"
    const val RAJAT = "devRajat"
    const val MEET = "devMeet"
    const val AJITH = "devAjith"
    const val AKSHAR = "devAkshar"
    const val BHAJAN = "devBhajan"
}