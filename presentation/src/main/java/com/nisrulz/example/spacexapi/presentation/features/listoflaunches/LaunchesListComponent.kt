package com.nisrulz.example.spacexapi.presentation.features.listoflaunches

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nisrulz.example.spacexapi.common.contract.utils.EmptyCallback
import com.nisrulz.example.spacexapi.common.contract.utils.SingleValueCallback
import com.nisrulz.example.spacexapi.domain.model.LaunchInfo
import com.nisrulz.example.spacexapi.presentation.R
import com.nisrulz.example.spacexapi.presentation.features.components.LaunchInfoItem
import com.nisrulz.example.spacexapi.presentation.features.components.TitleBar
import com.nisrulz.example.spacexapi.presentation.theme.SpacexAPITheme
import com.nisrulz.example.spacexapi.presentation.theme.dimens

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListOfLaunchesSuccessComponent(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    state: ListOfLaunchesViewModel.UiState,
    navigateToDetails: SingleValueCallback<String>,
    bookmark: SingleValueCallback<LaunchInfo>,
    navigateToBookmarks: EmptyCallback
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column {
            TitleBar(rightNavButtonIcon = R.drawable.bookmarks, rightNavButtonAction = {
                navigateToBookmarks()
            })
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(
                    items = state.data,
                    key = { _, item -> item.id } // Stable key for better performance
                ) { index, item ->
                    if (index == 0) {
                        Spacer(modifier = Modifier.height(MaterialTheme.dimens.small))
                    }
                    LaunchInfoItem(
                        modifier = Modifier.animateItem(
                            fadeInSpec = tween(
                                durationMillis = 250, easing = LinearOutSlowInEasing
                            )
                        ),
                        launchInfo = item,
                        onClick = navigateToDetails,
                        onBookmark = bookmark
                    )
                }
            }
        }

        // Wire in Snackbar Widget
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }
}

@Preview
@Composable
private fun Preview() {
    val testLaunchInfo = LaunchInfo(
        date_local = "",
        details = "",
        flight_number = 1,
        id = "1",
        logo = "",
        name = "Name 1",
        success = false,
        isBookmarked = false
    )
    SpacexAPITheme {
        ListOfLaunchesSuccessComponent(
            state = ListOfLaunchesViewModel.UiState(
                data = listOf(
                    testLaunchInfo, testLaunchInfo.copy(name = "Name 2")
                )
            ),
            snackbarHostState = SnackbarHostState(),
            navigateToDetails = {},
            bookmark = { },
            navigateToBookmarks = {})
    }
}
