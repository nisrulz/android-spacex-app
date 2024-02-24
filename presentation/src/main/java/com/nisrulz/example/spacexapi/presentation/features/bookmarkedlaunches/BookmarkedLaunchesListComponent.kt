package com.nisrulz.example.spacexapi.presentation.features.bookmarkedlaunches

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nisrulz.example.spacexapi.common.contract.utils.EmptyCallback
import com.nisrulz.example.spacexapi.common.contract.utils.SingleValueCallback
import com.nisrulz.example.spacexapi.domain.model.LaunchInfo
import com.nisrulz.example.spacexapi.presentation.R
import com.nisrulz.example.spacexapi.presentation.features.components.LaunchInfoItem
import com.nisrulz.example.spacexapi.presentation.theme.SpacexAPITheme
import com.nisrulz.example.spacexapi.presentation.theme.dimens

@Composable
fun BookmarkedLaunchesListComponent(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    state: BookmarkedLaunchesViewModel.UiState,
    navigateToDetails: SingleValueCallback<String> = {},
    bookmark: SingleValueCallback<LaunchInfo> = {},
    navigateBack: EmptyCallback = {}
) {
    var isShowingBookmarks by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Image(
                    modifier = Modifier.weight(0.8f, true),
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = stringResource(id = R.string.title_main)
                )

                Image(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = stringResource(id = R.string.title_bookmarks),
                    modifier = Modifier
                        .weight(0.2f, true)
                        .padding(top = MaterialTheme.dimens.large)
                        .clickable {
                            isShowingBookmarks = !isShowingBookmarks
                            navigateBack()
                        }
                )
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(state.data) { index, item ->
                    if (index == 0) {
                        Spacer(modifier = Modifier.height(MaterialTheme.dimens.small))
                    }
                    LaunchInfoItem(
                        launchInfo = item,
                        onClick = {
                            navigateToDetails(it)
                        },
                        onBookmark = {
                            bookmark(it)
                        }
                    )
                }
            }
        }

        // Wire in Snackbar Widget
        SnackbarHost(
            hostState = snackbarHostState,
            modifier =
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }
}

@Preview
@Composable
private fun Preview() {
    val testLaunchInfo =
        LaunchInfo(
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
        BookmarkedLaunchesListComponent(
            state =
            BookmarkedLaunchesViewModel.UiState(
                data = listOf(
                    testLaunchInfo,
                    testLaunchInfo.copy(name = "Name 2")
                )
            ),
            snackbarHostState = SnackbarHostState()
        )
    }
}
