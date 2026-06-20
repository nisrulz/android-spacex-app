package com.nisrulz.example.spacexapi.presentation.features.listoflaunches

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
import com.nisrulz.example.spacexapi.presentation.common.UiEvent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ListOfLaunchesScreen(
    viewModel: ListOfLaunchesViewModel = hiltViewModel(),
    navigateToDetails: SingleValueCallback<String>,
    navigateToBookmarks: EmptyCallback
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
            }
        }
    }

    with(state) {
        if (isLoading) {
            LoadingComponent()
        } else if (data.isEmpty()) {
            EmptyComponent(message = "No launches to show")
        } else {
            ListOfLaunchesSuccessComponent(
                state = state,
                snackbarHostState = snackbarHostState,
                navigateToDetails = navigateToDetails,
                bookmark = {
                    viewModel.bookmark(it)
                },
                navigateToBookmarks = navigateToBookmarks
            )
        }
    }
}
