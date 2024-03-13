package com.google.android.piyush.dopamine.beta

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import com.google.android.material.color.DynamicColors
import com.google.android.piyush.dopamine.beta.youtubedl.YoutubeDLException
import com.yausername.youtubedl_android.YoutubeDL
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers

class DopamineApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)

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
}