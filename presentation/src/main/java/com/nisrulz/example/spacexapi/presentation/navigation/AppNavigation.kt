package com.nisrulz.example.spacexapi.presentation.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.nisrulz.example.spacexapi.presentation.features.bookmarkedlaunches.BookmarkedLaunchesScreen
import com.nisrulz.example.spacexapi.presentation.features.launchdetail.LaunchDetailScreen
import com.nisrulz.example.spacexapi.presentation.features.listoflaunches.ListOfLaunchesScreen

@Composable
fun AppNavigation() {
    // back stack that persists across configuration changes and process death.
    val backStack = rememberNavBackStack(Home)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull<NavKey>() },
        // Essential decorators for state preservation and ViewModel scoping
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        // Entry provider using DSL
        entryProvider = entryProvider {
            entry<Home> {
                ListOfLaunchesScreen(
                    navigateToDetails = { id -> backStack.add(Details(id)) },
                    navigateToBookmarks = { backStack.add(Bookmarks) }
                )
            }

            entry<Bookmarks> {
                BookmarkedLaunchesScreen(
                    navigateToDetails = { id -> backStack.add(Details(id)) },
                    onBackAction = { backStack.removeLastOrNull() }
                )
            }

            entry<Details> { key ->
                LaunchDetailScreen(
                    launchId = key.launchId,
                    onBackAction = { backStack.removeLastOrNull() }
                )
            }
        },
        // Smooth animations
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        modifier = Modifier.fillMaxSize()
    )
}