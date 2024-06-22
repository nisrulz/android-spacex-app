package com.nisrulz.example.spacexapi.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.nisrulz.example.spacexapi.presentation.features.bookmarkedlaunches.BookmarkedLaunchesScreen
import com.nisrulz.example.spacexapi.presentation.features.launchdetail.LaunchDetailScreen
import com.nisrulz.example.spacexapi.presentation.features.listoflaunches.ListOfLaunchesScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController, startDestination = RouteHome
    ) {
        composable<RouteHome> {
            ListOfLaunchesScreen(navigateToDetails = { launchId ->
                navController.navigate(RouteDetails(launchId))
            }, navigateToBookmarks = {
                navController.navigate(RouteBookmark)
            })

        }

        composable<RouteBookmark> {
            BookmarkedLaunchesScreen(navigateToDetails = { launchId ->
                navController.navigate(RouteDetails(launchId))
            }, onBackAction = {
                navController.navigate(RouteHome)
            })

        }

        composable<RouteDetails> { backStackEntry ->
            val id = backStackEntry.toRoute<RouteDetails>().launchId

            LaunchDetailScreen(launchId = id, onBackAction = {
                navController.navigate(RouteHome)
            })

        }
    }
}