package com.nisrulz.example.spacexapi.presentation.features.bookmarkedlaunches

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nisrulz.example.spacexapi.common.contract.utils.EmptyCallback
import com.nisrulz.example.spacexapi.common.contract.utils.SingleValueCallback
import com.nisrulz.example.spacexapi.presentation.features.components.EmptyComponent
import com.nisrulz.example.spacexapi.presentation.features.components.LoadingComponent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow

@SuppressLint("VisibleForTests")
@Composable
fun BookmarkedLaunchesScreen(
    viewModel: BookmarkedLaunchesViewModel = hiltViewModel(),
    onBackAction: EmptyCallback = {},
    navigateToDetails: SingleValueCallback<String> = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        with(viewModel) {
            // Analytics
            getListOfBookmarkedLaunches()

            // collectLatest() Consumes only the most recent value, cancelling any previous
            // uncompleted processing.
            eventFlow.receiveAsFlow().collectLatest { event ->
                when (event) {
                    BookmarkedLaunchesViewModel.UiEvent.NavigateBack -> onBackAction()
                    is BookmarkedLaunchesViewModel.UiEvent.NavigateToDetails -> {
                        navigateToDetails(event.launchId)
                    }

                    is BookmarkedLaunchesViewModel.UiEvent.ShowSnackBar -> {
                        snackbarHostState.showSnackbar(
                            message = event.message
                        )
                    }
                }
            }
        }
    }

    BackHandler(true) {
        viewModel.navigateBack()
    }

    with(state) {
        if (error.isNotEmpty()) viewModel.showError(error)
        if (isLoading) {
            LoadingComponent()
        } else if (data.isEmpty()) {
            EmptyComponent(message = "No bookmarked launches") {
                viewModel.navigateBack()
            }
        } else {
            BookmarkedLaunchesListComponent(
                state = state,
                snackbarHostState = snackbarHostState,
                navigateToDetails = {
                    viewModel.navigateToDetails(it)
                },
                bookmark = {
                    viewModel.bookmark(it)
                },
                navigateBack = {
                    viewModel.navigateBack()
                })
        }
    }
}
