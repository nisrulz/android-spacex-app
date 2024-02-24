package com.nisrulz.example.spacexapi.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.nisrulz.example.spacexapi.presentation.navigation.NavigationRoute.bookmarkScreen
import com.nisrulz.example.spacexapi.presentation.navigation.NavigationRoute.detailsScreen
import com.nisrulz.example.spacexapi.presentation.navigation.NavigationRoute.homeScreen
import com.nisrulz.example.spacexapi.presentation.navigation.NavigationRoute.navigateBack
import com.nisrulz.example.spacexapi.presentation.navigation.NavigationRoute.navigateToBookmarks
import com.nisrulz.example.spacexapi.presentation.navigation.NavigationRoute.navigateToLaunchDetail

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.HOME_ROUTE,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        homeScreen(
            onNavigateToDetails = { launchId ->
                navController.navigateToLaunchDetail(launchId)
            },
            onNavigateToBookmarks = {
                navController.navigateToBookmarks()
            }
        )

        bookmarkScreen(
            onNavigateToDetails = { launchId ->
                navController.navigateToLaunchDetail(launchId)
            },
            onBackAction = {
                navController.navigateBack()
            }
        )

        detailsScreen(
            onBackAction = {
                navController.navigateBack()
            }
        )
    }
}
