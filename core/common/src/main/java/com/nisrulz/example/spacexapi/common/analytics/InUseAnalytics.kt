package com.nisrulz.example.spacexapi.common.analytics

import javax.inject.Inject

class InUseAnalytics
    @Inject
    constructor(
        private val analyticsService: Set<@JvmSuppressWildcards AnalyticsService>,
    ) {
        fun trackEvent(message: String) {
            analyticsService.forEach {
                it.trackEvent(message)
            }
        }
    }
