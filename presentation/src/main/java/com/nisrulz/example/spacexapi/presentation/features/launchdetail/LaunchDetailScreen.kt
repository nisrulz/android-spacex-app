package com.nisrulz.example.spacexapi.presentation.features.launchdetail

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nisrulz.example.spacexapi.common.contract.utils.EmptyCallback
import com.nisrulz.example.spacexapi.presentation.features.components.LoadingComponent
import com.nisrulz.example.spacexapi.presentation.features.launchdetail.LaunchDetailViewModel.UiEvent.ShowSnackBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
fun LaunchDetailScreen(
    viewModel: LaunchDetailViewModel = hiltViewModel(),
    launchId: String,
    onBackAction: EmptyCallback = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        with(viewModel) {
            // Analytics
            trackScreenEntered()

            getLaunchInfoDetails(launchId)

            eventFlow.receiveAsFlow().collectLatest {
                when (it) {
                    is ShowSnackBar -> {
                        snackbarHostState.showSnackbar(message = it.message)
                    }
                }
            }
        }
    }

    BackHandler(enabled = true) {
        // Analytics
        viewModel.trackOnBack()

        onBackAction()
    }

    with(state) {
        if (error.isNotEmpty()) viewModel.showError(error)
        if (isLoading) {
            LoadingComponent()
        } else {
            LaunchDetailSuccessComponent(
                state = state,
                snackbarHostState = snackbarHostState
            )
        }
    }
}
