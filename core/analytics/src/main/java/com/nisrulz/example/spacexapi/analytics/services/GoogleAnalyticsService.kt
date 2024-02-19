package com.nisrulz.example.spacexapi.analytics.services

import android.util.Log
import com.nisrulz.example.spacexapi.analytics.BuildConfig
import com.nisrulz.example.spacexapi.analytics.contract.AnalyticsEvent
import com.nisrulz.example.spacexapi.analytics.contract.AnalyticsService

internal class GoogleAnalyticsService : AnalyticsService {

    override fun trackScreen(screenName: String) {
        if (BuildConfig.DEBUG) {
            // Replace and implement code from Analytics SDK from Firebase
            Log.d("GoogleAnalytics", "Screen Event: $screenName")
        }
    }

    override fun trackEvent(event: AnalyticsEvent) {
        if (BuildConfig.DEBUG) {
            // Replace and implement code from Analytics SDK from Firebase
            Log.d("GoogleAnalytics", "Tracking Event: ${event.eventName}")
        }
    }
}
