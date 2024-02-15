package com.nisrulz.example.spacexapi.presentation.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nisrulz.example.spacexapi.presentation.features.launchdetail.LaunchDetailScreen
import com.nisrulz.example.spacexapi.presentation.features.listoflaunches.ListOfLaunchesScreen

/*
Read about Type safety in Navigation Compose:
https://developer.android.com/guide/navigation/design/type-safety
 */
internal object NavigationRoute {
    // Home
    const val HomeRoute = "list_of_launches"

    // Details
    private const val navArgLaunchId = "launchId"
    private const val DetailsRoute = "launch_detail/{$navArgLaunchId}"

    private fun buildDetailsRouteWithLaunchId(launchId: String) = DetailsRoute
        .replace("{$navArgLaunchId}", launchId)

    // Functions
    private fun NavBackStackEntry.getArgLaunchId(): String = arguments
        ?.getString(navArgLaunchId) ?: ""

    fun NavGraphBuilder.homeScreen(onNavigateToDetails: (launchId: String) -> Unit) {
        composable(HomeRoute) {
            ListOfLaunchesScreen(navigateToDetails = { launchId ->
                onNavigateToDetails(launchId)
            })
        }
    }

    fun NavGraphBuilder.detailsScreen() {
        composable(DetailsRoute) { backStackEntry ->
            val id = backStackEntry.getArgLaunchId()
            if (id.isNotEmpty()) {
                LaunchDetailScreen(launchId = id)
            }
        }
    }

    fun NavController.navigateToLaunchDetail(launchId: String) {
        this.navigate(buildDetailsRouteWithLaunchId(launchId))
    }
}
