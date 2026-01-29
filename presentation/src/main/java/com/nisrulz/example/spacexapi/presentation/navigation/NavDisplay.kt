package com.nisrulz.example.spacexapi.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NavDisplay(
    backStack: List<NavigationRoute>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (NavigationRoute) -> Unit
) {
    BackHandler(enabled = backStack.size > 1) {
        onBack()
    }

    val currentRoute = backStack.lastOrNull() ?: NavigationRoute.Home

    Box(modifier = modifier) {
        content(currentRoute)
    }
}
