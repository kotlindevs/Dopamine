package com.google.android.piyush.dopamine.utilities

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.piyush.database.model.CustomPlaylistView
import com.google.android.piyush.database.viewModel.DatabaseViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.databinding.ItemCustomDialogBinding
import com.google.android.piyush.dopamine.viewModels.RealtimeResource
import com.google.android.piyush.dopamine.viewModels.RealtimeViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Suppress("DEPRECATION")
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
    const val RELEASE_DATE = "05/04/2024"
    const val PROJECT_TIMELINE = "© 2024 Dopamine Team "
    const val PROJECT_VERSION = "dopamine_20242703_03.phone.stable.dynamic"
    const val PRE_RELEASE_VERSION = "dopamine_20240405_01.phone.prerelease.dynamic"
    const val STABLE = "stable"
    const val PRE_RELEASE = "prerelease"
    const val DEFAULT_LOGO = "https://cdn-images-1.medium.com/v2/resize:fit:1200/1*3tLD4Ve66pbBpuawm9Fu9Q.png"
    const val DEFAULT_BANNER = "https://developer.android.com/static/images/social/android-developers.png"
    const val LIGHT_MODE = "light"
    const val DARK_MODE = "dark"
    const val SYSTEM_MODE = "system"
    const val HOME = "home"
    const val SEARCH = "search"
    const val LIBRARY = "library"
    const val USER = "user"
    const val SHORTS = "shorts"
    const val CURRENT_SCREEN = "currentScreen"
    val THEME = arrayOf(
        LIGHT_MODE,
        DARK_MODE,
        SYSTEM_MODE
    )
    const val GITHUB = "https://github.com/kotlindevs/dopamine"
    const val EMAIL = "kotlindevslife@gmail.com"
    const val EMAIL1 = "piyushmakwana5617@gmail.com"

    val DEFAULT_REGION = arrayListOf("IN","India")
    val ARGENTINA = arrayListOf("AG","Argentina")           // Service not available
    val AUSTRALIA = arrayListOf("AU","Australia")
    val BANGLADESH = arrayListOf("BD","Bangladesh")
    val BRAZIL = arrayListOf("BR","Brazil")
    val BHUTAN = arrayListOf("BT","Bhutan")                 // Service not available
    val CANADA = arrayListOf("CA","Canada")
    val CHINA = arrayListOf("CN","China")                   // Service not available
    val COLOMBIA = arrayListOf("CO","Colombia")
    val DENMARK = arrayListOf("DK","Denmark")
    val EGYPT = arrayListOf("EG","Egypt")
    val FRANCE = arrayListOf("FR","France")
    val GERMANY = arrayListOf("DE","Germany")
    val HONG_KONG = arrayListOf("HK","Hong Kong")
    val ISRAEL = arrayListOf("IL","Israel")
    val ITALY = arrayListOf("IT","Italy")
    val IRAN = arrayListOf("IR","Iran")                 // Service not available
    val JAPAN = arrayListOf("JP","Japan")
    val JERSEY = arrayListOf("JE","Jersey")             // Service not available
    val KENYA = arrayListOf("KE","Kenya")
    val KOREA = arrayListOf("KR","Korea")
    val LEBANON = arrayListOf("LB","Lebanon")
    val MALAYSIA = arrayListOf("MY","Malaysia")
    val MALDIVES = arrayListOf("MV","Maldives")         // Service not available
    val MEXICO = arrayListOf("MX","Mexico")
    val MONGOLIA = arrayListOf("MN","Mongolia")
    val MYANMAR = arrayListOf("MM","Myanmar")           // Service not available
    val NETHERLANDS = arrayListOf("NL","Netherlands")
    val NEPAL = arrayListOf("NP","Nepal")
    val NEW_ZEALAND = arrayListOf("NZ","New Zealand")
    val NIGERIA = arrayListOf("NG","Nigeria")
    val NORWAY = arrayListOf("NO","Norway")
    val PAKISTAN = arrayListOf("PK","Pakistan")
    val PANAMA = arrayListOf("PA","Panama")
    val PARAGUAY = arrayListOf("PY","Paraguay")
    val PERU = arrayListOf("PE","Peru")
    val PHILIPPINES = arrayListOf("PH","Philippines")
    val POLAND = arrayListOf("PL","Poland")
    val PORTUGAL = arrayListOf("PT","Portugal")
    val RUSSIA = arrayListOf("RU","Russia")
    val ROMANIA = arrayListOf("RO","Romania")
    val SAUDI_ARABIA = arrayListOf("SA","Saudi Arabia")
    val SINGAPORE = arrayListOf("SG","Singapore")
    val SPAIN = arrayListOf("ES","Spain")
    val SRI_LANKA = arrayListOf("LK","Sri Lanka")
    val SWEDEN = arrayListOf("SE","Sweden")
    val SWITZERLAND = arrayListOf("CH","Switzerland")
    val TAIWAN = arrayListOf("TW","Taiwan")
    val UKRAINE = arrayListOf("UA","Ukraine")
    val UNITED_KINGDOM = arrayListOf("GB","United Kingdom")
    val UNITED_STATES = arrayListOf("US","United States")
    val ZIMBABWE = arrayListOf("ZW","Zimbabwe")

    val REGIONS = arrayOf(
        DEFAULT_REGION[1],
        ARGENTINA[1],
        AUSTRALIA[1],
        BANGLADESH[1],
        BRAZIL[1],
        BHUTAN[1],
        CANADA[1],
        CHINA[1],
        COLOMBIA[1],
        DENMARK[1],
        EGYPT[1],
        FRANCE[1],
        GERMANY[1],
        HONG_KONG[1],
        ISRAEL[1],
        ITALY[1],
        IRAN[1],
        JAPAN[1],
        JERSEY[1],
        KENYA[1],
        KOREA[1],
        LEBANON[1],
        MALAYSIA[1],
        MALDIVES[1],
        MEXICO[1],
        MONGOLIA[1],
        MYANMAR[1],
        NETHERLANDS[1],
        NEPAL[1],
        NEW_ZEALAND[1],
        NIGERIA[1],
        NORWAY[1],
        PAKISTAN[1],
        PANAMA[1],
        PARAGUAY[1],
        PERU[1],
        PHILIPPINES[1],
        POLAND[1],
        PORTUGAL[1],
        RUSSIA[1],
        ROMANIA[1],
        SAUDI_ARABIA[1],
        SINGAPORE[1],
        SPAIN[1],
        SRI_LANKA[1],
        SWEDEN[1],
        SWITZERLAND[1],
        TAIWAN[1],
        UKRAINE[1],
        UNITED_KINGDOM[1],
        UNITED_STATES[1],
        ZIMBABWE[1]
    )



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
            if(Firebase.auth.currentUser?.uid.isNullOrEmpty()) {
                if (databaseViewModel.isPlaylistExist(playlistName.toString())) {
                    binding.textInputLayout1.isErrorEnabled = true
                    binding.textInputLayout1.error = "Playlist Already Exists"
                } else {
                    if (playlistName.toString().isEmpty()) {
                        ToastUtilities.showToast(context, "Please Fill All Fields")
                    } else {
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
            }else{
                if (playlistName.toString().isEmpty()) {
                    ToastUtilities.showToast(context, "Please Fill All Fields")
                } else {
                    val realtimeViewModel = RealtimeViewModel()
                    realtimeViewModel.addInMasterRecords(
                        playlist = CustomPlaylistView(
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

fun dopamineSharedPreferences(context: Context) : SharedPreferences{
    return context.getSharedPreferences("DopamineApp", Context.MODE_PRIVATE)
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun createDefaultNotification(
    context: Context,title: String , content: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "dopamineUpdateChannel",
            "Update Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    val notificationBuilder = NotificationCompat.Builder(context, "dopamineUpdateChannel")
        .setContentTitle(title)
        .setContentText(content)
        .setSmallIcon(R.drawable.ic_update)
        .setAutoCancel(true)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setOnlyAlertOnce(true)
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