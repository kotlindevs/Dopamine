package com.google.android.piyush.dopamine

import android.app.Application
import android.os.Build
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DopamineApp : Application(){
    override fun onCreate() {
        super.onCreate()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            DynamicColors.applyToActivitiesIfAvailable(this)
        }
    }
}