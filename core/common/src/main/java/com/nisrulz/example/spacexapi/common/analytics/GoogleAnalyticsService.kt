package com.nisrulz.example.spacexapi.common.analytics

import android.util.Log
import com.nisrulz.example.spacexapi.common.BuildConfig

class GoogleAnalyticsService : AnalyticsService {
    override fun trackEvent(message: String) {
        if (BuildConfig.DEBUG) {
            // Replace and implement code from Analytics SDK from Google Analytics
            Log.d("GoogleAnalytics", "Tracking Event: $message")
        }
    }
}
