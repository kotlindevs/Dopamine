package com.google.android.piyush.dopamine.beta

import android.app.Application
import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.color.DynamicColors
import com.google.android.piyush.dopamine.utilities.Utilities
import java.io.IOException
import java.net.URL


class DopamineApp : Application() {
    override fun onCreate() {
        super.onCreate()
 /*
        val policy = ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)
        val image = getBitmapFromUrl(FirebaseAuth.getInstance().currentUser?.photoUrl.toString())

        if(image != null) {
            DynamicColors.applyToActivitiesIfAvailable(
                this, DynamicColorsOptions.Builder().setContentBasedSource(
                    image
                ).build()
            )
        } */

        val dopamineApp = applicationContext.getSharedPreferences("DopamineApp", MODE_PRIVATE)

        if(dopamineApp.getString("Theme", Utilities.LIGHT_MODE) == Utilities.LIGHT_MODE){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }else if(dopamineApp.getString("Theme", Utilities.DARK_MODE) == Utilities.DARK_MODE){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        if(dopamineApp.getBoolean("ExperimentalUserColor", false).equals(true)){
            DynamicColors.applyToActivitiesIfAvailable(this)
        }
    }


    private fun getBitmapFromUrl(imageUrl: String): Bitmap? {
        try {
            val url = URL(imageUrl)
            val inputStream = url.openStream()
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            Log.e(TAG, "Error downloading image", e)
            return null
        }
    }
}