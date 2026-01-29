package com.nisrulz.example.spacexapi.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.nisrulz.example.spacexapi.presentation.features.bookmarkedlaunches.BookmarkedLaunchesScreen
import com.nisrulz.example.spacexapi.presentation.features.bookmarkedlaunches.BookmarkedLaunchesViewModel
import com.nisrulz.example.spacexapi.presentation.features.launchdetail.LaunchDetailScreen
import com.nisrulz.example.spacexapi.presentation.features.listoflaunches.ListOfLaunchesScreen
import com.nisrulz.example.spacexapi.presentation.features.listoflaunches.ListOfLaunchesViewModel

@Composable
fun AppNavigation(navigationViewModel: NavigationViewModel = hiltViewModel()) {
    val listOfLaunchesViewModel: ListOfLaunchesViewModel = hiltViewModel()
    val bookmarkedLaunchesViewModel: BookmarkedLaunchesViewModel = hiltViewModel()

    NavDisplay(
        backStack = navigationViewModel.backStack,
        onBack = { navigationViewModel.navigateBack() },
        modifier = Modifier.fillMaxSize()
    ) { route ->
        when (route) {
            is NavigationRoute.Home -> {
                ListOfLaunchesScreen(
                    viewModel = listOfLaunchesViewModel,
                    navigateToDetails = { id -> navigationViewModel.navigateTo(NavigationRoute.Details(id)) },
                    navigateToBookmarks = { navigationViewModel.navigateTo(NavigationRoute.Bookmarks) }
                )
            }

            is NavigationRoute.Bookmarks -> {
                BookmarkedLaunchesScreen(
                    viewModel = bookmarkedLaunchesViewModel,
                    navigateToDetails = { id -> navigationViewModel.navigateTo(NavigationRoute.Details(id)) },
                    onBackAction = { navigationViewModel.navigateBack() }
                )
            }

            is NavigationRoute.Details -> {
                LaunchDetailScreen(
                    launchId = route.launchId,
                    onBackAction = { navigationViewModel.navigateBack() }
                )
            }
        }
    }
}