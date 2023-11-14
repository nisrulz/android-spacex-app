package com.nisrulz.example.spacexapi.presentation.navigation

sealed class NavigationRoute(val route: String) {
    data object ListOfLaunches : NavigationRoute("list_of_launches")
    data object LaunchDetail : NavigationRoute("launch_detail/{launchId}") {
        fun build(id: String): String = route
            .replace("{launchId}", id)
    }
}
