package com.nisrulz.example.spacexapi.presentation.features.bookmarkedlaunches

import android.annotation.SuppressLint
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
                    is BookmarkedLaunchesViewModel.UiEvent.ShowSnackBar -> {
                        snackbarHostState.showSnackbar(
                            message = event.message
                        )
                    }
                }
            }
        }
    }

    with(state) {
        if (error.isNotEmpty()) viewModel.showError(error)
        if (isLoading) {
            LoadingComponent()
        } else if (data.isEmpty()) {
            EmptyComponent(message = "No bookmarked launches") {
                onBackAction()
            }
        } else {
            BookmarkedLaunchesListComponent(
                state = state,
                snackbarHostState = snackbarHostState,
                navigateToDetails = navigateToDetails,
                bookmark = {
                    viewModel.bookmark(it)
                },
                navigateBack = onBackAction)
        }
    }
}
