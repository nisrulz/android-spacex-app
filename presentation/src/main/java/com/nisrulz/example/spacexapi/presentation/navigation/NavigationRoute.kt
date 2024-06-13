package com.nisrulz.example.spacexapi.presentation.navigation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nisrulz.example.spacexapi.presentation.features.bookmarkedlaunches.BookmarkedLaunchesScreen
import com.nisrulz.example.spacexapi.presentation.features.launchdetail.LaunchDetailScreen
import com.nisrulz.example.spacexapi.presentation.features.listoflaunches.ListOfLaunchesScreen

/*
Read about Type safety in Navigation Compose:
https://developer.android.com/guide/navigation/design/type-safety
 */
internal object NavigationRoute {
    // Home
    const val HOME_ROUTE = "list_of_launches"

    // Bookmark
    private const val BOOKMARK_ROUTE = "bookmarked_launches"

    // Details
    private const val NAV_ARG_LAUNCH_ID = "launchId"
    private const val DETAILS_ROUTE = "launch_detail/{$NAV_ARG_LAUNCH_ID}"

    private fun buildDetailsRouteWithLaunchId(launchId: String) = DETAILS_ROUTE
        .replace("{$NAV_ARG_LAUNCH_ID}", launchId)

    // Functions
    private fun NavBackStackEntry.getArgLaunchId(): String = arguments
        ?.getString(NAV_ARG_LAUNCH_ID) ?: ""

    fun NavGraphBuilder.homeScreen(
        onNavigateToDetails: (launchId: String) -> Unit,
        onNavigateToBookmarks: () -> Unit
    ) {
        composable(
            HOME_ROUTE
        ) {
            ListOfLaunchesScreen(navigateToDetails = { launchId ->
                onNavigateToDetails(launchId)
            }, navigateToBookmarks = {
                onNavigateToBookmarks()
            })
        }
    }

    fun NavGraphBuilder.bookmarkScreen(
        onNavigateToDetails: (launchId: String) -> Unit,
        onBackAction: () -> Unit
    ) {
        composable(
            BOOKMARK_ROUTE
        ) {
            BookmarkedLaunchesScreen(
                navigateToDetails = { launchId ->
                    onNavigateToDetails(launchId)
                },
                navigateBack = onBackAction
            )
        }
    }

    fun NavGraphBuilder.detailsScreen(onBackAction: () -> Unit) {
        composable(
            DETAILS_ROUTE
        ) { backStackEntry ->
            val id = backStackEntry.getArgLaunchId()
            if (id.isNotEmpty()) {
                LaunchDetailScreen(launchId = id, onBackAction = onBackAction)
            }
        }
    }

    fun NavController.navigateToBookmarks() {
        this.navigate(BOOKMARK_ROUTE)
    }

    fun NavController.navigateToLaunchDetail(launchId: String) {
        this.navigate(buildDetailsRouteWithLaunchId(launchId))
    }

    fun NavController.navigateBack() {
        this.popBackStack()
    }

    private fun customFadeIn() = fadeIn(
        animationSpec = tween(
            300,
            easing = LinearEasing
        )
    )

    private fun customFadeOut() = fadeOut(
        animationSpec = tween(
            300,
            easing = LinearEasing
        )
    )
}
