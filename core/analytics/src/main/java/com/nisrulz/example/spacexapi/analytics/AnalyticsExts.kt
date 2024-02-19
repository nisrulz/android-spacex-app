package com.nisrulz.example.spacexapi.analytics

fun InUseAnalytics.trackScreenListOfLaunches() = this.trackScreen("list_of_launches")
fun InUseAnalytics.trackScreenLaunchDetail() = this.trackScreen("launch_detail")
fun InUseAnalytics.trackNavigateToDetail() = this.trackEvent(
    SpaceXAnalyticsEvent(
        eventName = "navigation",
        eventProperties = mapOf(
            "destination" to "details",
            "label" to "launch",
            "param_1" to "value_1"
        )
    )
)

fun InUseAnalytics.trackNavigateToListOfLaunches() = this.trackEvent(
    SpaceXAnalyticsEvent(
        eventName = "navigation",
        eventProperties = mapOf(
            "destination" to "list_of_launches"
        )
    )
)
