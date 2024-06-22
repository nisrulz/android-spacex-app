package com.nisrulz.example.spacexapi.presentation.navigation

import kotlinx.serialization.Serializable


@Serializable
object RouteHome

@Serializable
object RouteBookmark

@Serializable
data class RouteDetails(val launchId: String)
