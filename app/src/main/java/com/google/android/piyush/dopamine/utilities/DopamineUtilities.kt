package com.google.android.piyush.dopamine.utilities

import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
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
    const val REQUEST_CODE_SPEECH_INPUT = 100
}

class CustomDialog(context: Context) : Dialog(context) {
    private lateinit var binding: ItemCustomDialogBinding
    private lateinit var databaseViewModel: DatabaseViewModel
    init {
        setCancelable(true)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseViewModel = DatabaseViewModel(context)
        binding = ItemCustomDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                    dismiss()
                    ToastUtilities.showToast(context, "$playlistName Created ✅")
                }
            }
        }
    }
}