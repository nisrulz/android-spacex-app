package com.nisrulz.example.spacexapi.analytics.contract

interface AnalyticsService {
    fun trackScreen(screenName: String)

    fun trackEvent(event: AnalyticsEvent)
}
