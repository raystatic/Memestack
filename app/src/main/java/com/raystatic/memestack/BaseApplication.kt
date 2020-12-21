package com.raystatic.memestack

import android.app.Application
import android.os.StrictMode
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class BaseApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }

}