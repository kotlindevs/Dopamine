package com.example.dopamine

import android.app.Application
import com.google.android.material.color.DynamicColors

class Dynamic : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}