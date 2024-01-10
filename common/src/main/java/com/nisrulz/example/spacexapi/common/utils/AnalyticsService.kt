package com.nisrulz.example.spacexapi.common.utils

interface AnalyticsService {
    fun trackEvent(message: String)
}

class DefaultAnalyticsService(
    private val debugBuild: Boolean,
) : AnalyticsService by FirebaseAnalyticsService(debugBuild)

private class FirebaseAnalyticsService(
    private val debugBuild: Boolean,
) : AnalyticsService {
    override fun trackEvent(message: String) {
        if (debugBuild) {
            // Implement code from Analytics SDK from 3rd party company
            // i.e Firebase, Google Analytics
        }
    }
}
