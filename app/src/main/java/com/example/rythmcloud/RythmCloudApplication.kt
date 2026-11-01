package com.example.rythmcloud

import android.app.Application
import com.example.rythmcloud.data.remote.RetrofitInstance
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class RythmCloudApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitInstance.initialize(this)
    }
}