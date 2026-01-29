package com.nisrulz.example.spacexapi.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
        AnimatedContent(
            targetState = currentRoute,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "NavDisplay"
        ) { route ->
            content(route)
        }
    }
}
