package com.nisrulz.example.spacexapi.analytics

import com.nisrulz.example.spacexapi.analytics.contract.AnalyticsEvent
import com.nisrulz.example.spacexapi.analytics.contract.AnalyticsService
import javax.inject.Inject

class InUseAnalytics
@Inject
constructor(
    private val analyticsService: Set<@JvmSuppressWildcards AnalyticsService>
) {

    fun trackScreen(screenName: String) {
        analyticsService.forEach {
            it.trackScreen(screenName)
        }
    }

    fun trackEvent(analyticsEvent: AnalyticsEvent) {
        analyticsService.forEach {
            it.trackEvent(analyticsEvent)
        }
    }
}
