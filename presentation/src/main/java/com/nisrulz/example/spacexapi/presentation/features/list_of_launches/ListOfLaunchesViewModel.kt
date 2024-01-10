package com.nisrulz.example.spacexapi.presentation.features.list_of_launches

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisrulz.example.spacexapi.common.utils.DefaultLogger
import com.nisrulz.example.spacexapi.common.utils.Logger
import com.nisrulz.example.spacexapi.data.BuildConfig
import com.nisrulz.example.spacexapi.domain.model.LaunchInfo
import com.nisrulz.example.spacexapi.domain.usecase.GetAllBookmarkedLaunches
import com.nisrulz.example.spacexapi.domain.usecase.GetAllLaunches
import com.nisrulz.example.spacexapi.domain.usecase.ToggleBookmarkLaunchInfo
import com.nisrulz.example.spacexapi.presentation.features.list_of_launches.ListOfLaunchesViewModel.ListOfLaunchesUiEvent.NavigateToDetails
import com.nisrulz.example.spacexapi.presentation.features.list_of_launches.ListOfLaunchesViewModel.ListOfLaunchesUiEvent.ShowSnackBar
import com.nisrulz.example.spacexapi.presentation.features.list_of_launches.ListOfLaunchesViewModel.ListOfLaunchesUiState.Error
import com.nisrulz.example.spacexapi.presentation.features.list_of_launches.ListOfLaunchesViewModel.ListOfLaunchesUiState.Loading
import com.nisrulz.example.spacexapi.presentation.features.list_of_launches.ListOfLaunchesViewModel.ListOfLaunchesUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListOfLaunchesViewModel @Inject
constructor(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val getAllLaunches: GetAllLaunches,
    private val bookmarkLaunchInfo: ToggleBookmarkLaunchInfo,
    private val getAllBookmarkedLaunches: GetAllBookmarkedLaunches,
    ) : ViewModel(),
        Logger by DefaultLogger(BuildConfig.DEBUG) {
    var uiState: MutableStateFlow<ListOfLaunchesUiState> = MutableStateFlow(Loading)
        private set

    var eventFlow = Channel<ListOfLaunchesUiEvent>()
        private set

    @VisibleForTesting
    fun getListOfLaunches() = viewModelScope.launch(coroutineDispatcher) {
        getAllLaunches()
            .catch {
                uiState.update { Error("Error while fetching list of launches") }
            }
            .collectLatest {
                handleListOfLaunches(it)
            }

    }

    @VisibleForTesting
    fun getListOfBookmarkedLaunches() = viewModelScope.launch(coroutineDispatcher) {
        getAllBookmarkedLaunches()
            .catch {
                uiState.update { Error("Error while fetching list of bookmarked launches") }
            }
            .collectLatest {
                handleListOfLaunches(it)
            }
    }

    fun bookmark(launchInfo: LaunchInfo) =
        viewModelScope.launch(coroutineDispatcher) {
            bookmarkLaunchInfo(launchInfo)
        }

    private fun handleListOfLaunches(list: List<LaunchInfo>) {
        uiState.update { Success(list) }
    }

    fun onClickBookmarkToolbarIcon(isShowingBookmarks: Boolean) {
        if (isShowingBookmarks) {
            getListOfBookmarkedLaunches()
        } else {
            getListOfLaunches()
        }
    }

    fun showError(message: String) = sendEvent(ShowSnackBar(message))

    fun navigateToDetails(launchId: String) = sendEvent(NavigateToDetails(launchId))

    private fun sendEvent(listOfLaunchesUiEvent: ListOfLaunchesUiEvent) =
        viewModelScope.launch(coroutineDispatcher) {
            eventFlow.send(listOfLaunchesUiEvent)
                log("Ui Event: $listOfLaunchesUiEvent")
        }

    sealed interface ListOfLaunchesUiEvent {
        data class ShowSnackBar(val message: String) : ListOfLaunchesUiEvent
        data class NavigateToDetails(val launchId: String) : ListOfLaunchesUiEvent
    }

    sealed interface ListOfLaunchesUiState {
        data object Loading : ListOfLaunchesUiState
        data class Error(val message: String) : ListOfLaunchesUiState
        data class Success(val data: List<LaunchInfo>) : ListOfLaunchesUiState
    }
}