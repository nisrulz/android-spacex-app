package com.nisrulz.example.spacexapi.presentation.features.launchdetail

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nisrulz.example.spacexapi.common.contract.utils.EmptyCallback
import com.nisrulz.example.spacexapi.presentation.features.components.EmptyComponent
import com.nisrulz.example.spacexapi.presentation.features.components.LoadingComponent
import com.nisrulz.example.spacexapi.presentation.common.UiEvent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LaunchDetailScreen(
    viewModel: LaunchDetailViewModel = hiltViewModel(),
    launchId: String,
    onBackAction: EmptyCallback = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel, launchId) {
        // Fetch data for this specific launch
        viewModel.getLaunchInfoDetails(launchId)

        viewModel.eventFlow.collectLatest {
            when (it) {
                is UiEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(message = it.message)
                }
            }
        }
    }

    with(state) {
        if (isLoading) {
            LoadingComponent()
        } else if (data == null) {
            EmptyComponent(message = "No launch info available") {
                onBackAction()
            }
        } else {
            LaunchDetailSuccessComponent(
                state = state,
                snackbarHostState = snackbarHostState,
                bookmark = {
                    viewModel.bookmark(data)
                },
                navigateBack = {
                    // Analytics
                    viewModel.trackOnBack()
                    onBackAction()
                }
            )
        }
    }
}
