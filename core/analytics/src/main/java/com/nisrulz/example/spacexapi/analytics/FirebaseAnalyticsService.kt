package com.nisrulz.example.spacexapi.analytics

import android.util.Log

class FirebaseAnalyticsService : AnalyticsService {
    override fun trackEvent(message: String) {
        if (BuildConfig.DEBUG) {
            // Replace and implement code from Analytics SDK from Firebase
            Log.d("FirebaseAnalytics", "Tracking Event: $message")
        }
    }
}