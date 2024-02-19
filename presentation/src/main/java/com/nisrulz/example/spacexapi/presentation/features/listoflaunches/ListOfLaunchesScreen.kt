package com.nisrulz.example.spacexapi.presentation.features.listoflaunches

import android.annotation.SuppressLint
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nisrulz.example.spacexapi.presentation.features.components.LoadingComponent
import com.nisrulz.example.spacexapi.presentation.features.listoflaunches.ListOfLaunchesViewModel.ListOfLaunchesUiEvent.NavigateToDetails
import com.nisrulz.example.spacexapi.presentation.features.listoflaunches.ListOfLaunchesViewModel.ListOfLaunchesUiEvent.ShowSnackBar
import com.nisrulz.example.spacexapi.presentation.features.listoflaunches.ListOfLaunchesViewModel.ListOfLaunchesUiState.Error
import com.nisrulz.example.spacexapi.presentation.features.listoflaunches.ListOfLaunchesViewModel.ListOfLaunchesUiState.Loading
import com.nisrulz.example.spacexapi.presentation.features.listoflaunches.ListOfLaunchesViewModel.ListOfLaunchesUiState.Success
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow

@SuppressLint("VisibleForTests")
@Composable
fun ListOfLaunchesScreen(
    viewModel: ListOfLaunchesViewModel = hiltViewModel(),
    navigateToDetails: (String) -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        with(viewModel) {
            // Analytics
            trackScreenEntered()

            getListOfLaunches()

            // collectLatest() Consumes only the most recent value, cancelling any previous
            // uncompleted processing.
            eventFlow.receiveAsFlow().collectLatest { event ->
                when (event) {
                    is ShowSnackBar -> {
                        snackbarHostState.showSnackbar(
                            message = event.message
                        )
                    }

                    is NavigateToDetails -> {
                        navigateToDetails(event.launchId)
                    }
                }
            }
        }
    }

    when (state) {
        Loading -> LoadingComponent()
        is Error -> viewModel.showError((state as Error).message)
        is Success ->
            ListOfLaunchesSuccessComponent(
                state = state as Success,
                snackbarHostState = snackbarHostState,
                navigateToDetails = {
                    viewModel.navigateToDetails(it)
                },
                bookmark = {
                    viewModel.bookmark(it)
                },
                toggleBookmarkList = {
                    viewModel.onClickBookmarkToolbarIcon(it)
                }
            )
    }
}
