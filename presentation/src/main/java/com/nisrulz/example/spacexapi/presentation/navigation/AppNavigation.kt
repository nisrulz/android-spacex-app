package com.nisrulz.example.spacexapi.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.nisrulz.example.spacexapi.presentation.navigation.NavigationRoute.detailsScreen
import com.nisrulz.example.spacexapi.presentation.navigation.NavigationRoute.homeScreen
import com.nisrulz.example.spacexapi.presentation.navigation.NavigationRoute.navigateToLaunchDetail

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.HomeRoute,
    ) {
        homeScreen(
            onNavigateToDetails = { launchId ->
                navController.navigateToLaunchDetail(launchId)
            },
        )

        detailsScreen()
    }
}
