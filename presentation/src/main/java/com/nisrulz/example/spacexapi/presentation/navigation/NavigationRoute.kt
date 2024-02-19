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
    const val HOME_ROUTE = "list_of_launches"

    // Details
    private const val NAV_ARG_LAUNCH_ID = "launchId"
    private const val DETAILS_ROUTE = "launch_detail/{$NAV_ARG_LAUNCH_ID}"

    private fun buildDetailsRouteWithLaunchId(launchId: String) = DETAILS_ROUTE
        .replace("{$NAV_ARG_LAUNCH_ID}", launchId)

    // Functions
    private fun NavBackStackEntry.getArgLaunchId(): String = arguments
        ?.getString(NAV_ARG_LAUNCH_ID) ?: ""

    fun NavGraphBuilder.homeScreen(onNavigateToDetails: (launchId: String) -> Unit) {
        composable(HOME_ROUTE) {
            ListOfLaunchesScreen(navigateToDetails = { launchId ->
                onNavigateToDetails(launchId)
            })
        }
    }

    fun NavGraphBuilder.detailsScreen(onBackAction: () -> Unit) {
        composable(DETAILS_ROUTE) { backStackEntry ->
            val id = backStackEntry.getArgLaunchId()
            if (id.isNotEmpty()) {
                LaunchDetailScreen(launchId = id, onBackAction = onBackAction)
            }
        }
    }

    fun NavController.navigateToLaunchDetail(launchId: String) {
        this.navigate(buildDetailsRouteWithLaunchId(launchId))
    }

    fun NavController.backFromLaunchDetails() {
        this.popBackStack()
    }
}
