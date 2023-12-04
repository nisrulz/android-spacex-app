package com.nisrulz.example.spacexapi.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nisrulz.example.spacexapi.presentation.features.launch_detail.LaunchDetailScreen
import com.nisrulz.example.spacexapi.presentation.features.list_of_launches.ListOfLaunchesScreen
import com.nisrulz.example.spacexapi.presentation.navigation.NavigationRoute.LaunchDetail
import com.nisrulz.example.spacexapi.presentation.navigation.NavigationRoute.ListOfLaunches


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = ListOfLaunches.route
    ) {
        composable(ListOfLaunches.route) {
            ListOfLaunchesScreen(navigateToDetails = { launchId ->
                navController.navigate(
                    LaunchDetail.build(launchId)
                )
            })

        }
        composable(LaunchDetail.route) { backStackEntry ->
            backStackEntry.arguments?.apply {
                val launchId = getString("launchId")
                if (launchId != null) {
                    LaunchDetailScreen(launchId = launchId)
                }
            }
        }
    }
}