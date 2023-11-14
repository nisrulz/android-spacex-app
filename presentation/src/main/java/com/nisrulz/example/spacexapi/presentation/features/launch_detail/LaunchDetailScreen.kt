package com.nisrulz.example.spacexapi.presentation.features.launch_detail

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nisrulz.example.spacexapi.presentation.features.components.LoadingComponent
import com.nisrulz.example.spacexapi.presentation.features.launch_detail.LaunchDetailViewModel.LaunchDetailUiEvent
import com.nisrulz.example.spacexapi.presentation.features.launch_detail.LaunchDetailViewModel.LaunchDetailUiState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
fun LaunchDetailScreen(
    viewModel: LaunchDetailViewModel = hiltViewModel(),
    launchId: String,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        with(viewModel) {
            getLaunchInfoDetails(launchId)

            viewModel.eventFlow.receiveAsFlow().collectLatest {
                when (it) {
                    is LaunchDetailUiEvent.ShowSnackBar -> {
                        snackbarHostState.showSnackbar(message = it.message)
                    }
                }
            }
        }
    }

    when (state) {
        LaunchDetailUiState.Loading -> LoadingComponent()
        is LaunchDetailUiState.Error -> viewModel.showError((state as LaunchDetailUiState.Error).message)
        is LaunchDetailUiState.Success -> LaunchDetailSuccessComponent(
            state = state as LaunchDetailUiState.Success,
            snackbarHostState = snackbarHostState
        )
    }
}
