package com.google.android.piyush.dopamine.utilities

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.piyush.dopamine.R

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