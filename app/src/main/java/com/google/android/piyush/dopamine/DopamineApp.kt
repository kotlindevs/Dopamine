package com.google.android.piyush.dopamine

import android.app.Application
import com.google.android.material.color.DynamicColors

class DopamineApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}