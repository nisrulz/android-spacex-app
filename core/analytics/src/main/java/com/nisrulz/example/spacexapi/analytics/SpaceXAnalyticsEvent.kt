package com.nisrulz.example.spacexapi.analytics

import com.nisrulz.example.spacexapi.analytics.contract.AnalyticsEvent

class SpaceXAnalyticsEvent(eventName: String, eventProperties: Map<String, Any>) :
    AnalyticsEvent(eventName, eventProperties)
