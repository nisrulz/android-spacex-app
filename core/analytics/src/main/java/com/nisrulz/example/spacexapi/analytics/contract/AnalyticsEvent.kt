package com.nisrulz.example.spacexapi.analytics.contract

abstract class AnalyticsEvent(
    val eventName: String,
    open val eventProperties: Map<String, Any>
)
