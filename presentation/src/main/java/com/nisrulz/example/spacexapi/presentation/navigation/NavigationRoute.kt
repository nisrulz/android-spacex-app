package com.nisrulz.example.spacexapi.presentation.navigation

sealed interface NavigationRoute {
    data object Home : NavigationRoute
    data object Bookmarks : NavigationRoute
    data class Details(val launchId: String) : NavigationRoute
}
