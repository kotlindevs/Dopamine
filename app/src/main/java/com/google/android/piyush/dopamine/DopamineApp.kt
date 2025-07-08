package com.google.android.piyush.dopamine

import android.app.Application
import android.os.StrictMode
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.piyush.dopamine.utilities.Utilities


class DopamineApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)

            DynamicColors.applyToActivitiesIfAvailable(
                this, DynamicColorsOptions.Builder().build()
            )

        val dopamineApp = applicationContext.getSharedPreferences("DopamineApp", MODE_PRIVATE)

        if(dopamineApp.getString("Theme", Utilities.LIGHT_MODE) == Utilities.LIGHT_MODE){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }else if(dopamineApp.getString("Theme", Utilities.DARK_MODE) == Utilities.DARK_MODE){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        if(dopamineApp.getBoolean("ExperimentalUserColor", false)){
            DynamicColors.applyToActivitiesIfAvailable(this)
        }
    }
}