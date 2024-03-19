package com.google.android.piyush.dopamine.beta

import android.app.Application
import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.piyush.dopamine.beta.youtubedl.YoutubeDLException
import com.google.android.piyush.dopamine.utilities.Utilities
import com.google.firebase.auth.FirebaseAuth
import com.yausername.youtubedl_android.YoutubeDL
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
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

        configureRxJavaErrorHandler()
        Completable.fromAction { this.initLibraries() }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    // it worked
                }

                override fun onError(e: Throwable) {
                    Log.e(
                     TAG, "failed to initialize", e)
                    Toast.makeText(
                        applicationContext,
                        "initialization failed: " + e.localizedMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun configureRxJavaErrorHandler() {
        RxJavaPlugins.setErrorHandler { e: Throwable ->
            if (e is UndeliverableException) {
                Log.d(TAG, "Undeliverable exception received, not sure what to do", e)
            }

            if (e is InterruptedException) {
                return@setErrorHandler
            }
            Log.e(TAG, "Undeliverable exception received, not sure what to do", e)
        }
    }

    @Throws(YoutubeDLException::class)
    private fun initLibraries() {
        YoutubeDL.getInstance().init(this)
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