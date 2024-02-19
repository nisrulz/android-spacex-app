package com.nisrulz.example.spacexapi.analytics.services

import android.util.Log
import com.nisrulz.example.spacexapi.analytics.BuildConfig
import com.nisrulz.example.spacexapi.analytics.contract.AnalyticsEvent
import com.nisrulz.example.spacexapi.analytics.contract.AnalyticsService

internal class FirebaseAnalyticsService : AnalyticsService {

    var lastScreenName: String = ""
    override fun trackScreen(screenName: String) {
        if (BuildConfig.DEBUG) {
            lastScreenName = screenName
            // Replace and implement code from Analytics SDK from Firebase
            Log.d("FirebaseAnalytics", "Screen Event: $lastScreenName")
        }
    }

    override fun trackEvent(event: AnalyticsEvent) {
        if (BuildConfig.DEBUG) {
            val eventString = event.eventProperties.entries
                .joinToString("&") { (eventKey, eventValue) ->
                    "$eventKey=$eventValue"
                }

            // Replace and implement code from Analytics SDK from Firebase
            Log.d("FirebaseAnalytics", "Tracking Event: ${event.eventName}, Params:$eventString")
        }
    }
}
