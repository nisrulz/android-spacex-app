package com.nisrulz.example.spacexapi.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class NavigationRoute : NavKey

@Serializable
data object Home : NavigationRoute()

@Serializable
data object Bookmarks : NavigationRoute()

@Serializable
data class Details(val launchId: String) : NavigationRoute()

