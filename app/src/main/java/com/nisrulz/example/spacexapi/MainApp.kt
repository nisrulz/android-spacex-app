package com.nisrulz.example.spacexapi

import android.app.Application
import com.nisrulz.example.spacexapi.utils.AnrDetector
import com.nisrulz.example.spacexapi.utils.CoilCache
import com.nisrulz.example.spacexapi.utils.DefaultAnrDetector
import com.nisrulz.example.spacexapi.utils.DefaultCoilCache
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApp :
    Application(),
    AnrDetector by DefaultAnrDetector(),
    CoilCache by DefaultCoilCache() {
    override fun onCreate() {
        super.onCreate()
        setupAnrDetector()
        setupCoilCache(this)
    }
}
