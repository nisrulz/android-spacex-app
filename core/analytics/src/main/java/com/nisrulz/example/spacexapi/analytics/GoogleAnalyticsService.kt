package com.nisrulz.example.spacexapi.analytics

import android.util.Log

class GoogleAnalyticsService : AnalyticsService {
    override fun trackEvent(message: String) {
        if (BuildConfig.DEBUG) {
            // Replace and implement code from Analytics SDK from Google Analytics
            Log.d("GoogleAnalytics", "Tracking Event: $message")
        }
    }
}
