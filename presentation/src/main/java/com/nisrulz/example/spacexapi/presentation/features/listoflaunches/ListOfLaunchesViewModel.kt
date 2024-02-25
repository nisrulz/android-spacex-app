package com.nisrulz.example.spacexapi.presentation.features.listoflaunches

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisrulz.example.spacexapi.analytics.InUseAnalytics
import com.nisrulz.example.spacexapi.analytics.trackNavigateToDetail
import com.nisrulz.example.spacexapi.analytics.trackScreenListOfLaunches
import com.nisrulz.example.spacexapi.domain.model.LaunchInfo
import com.nisrulz.example.spacexapi.domain.usecase.GetAllBookmarkedLaunches
import com.nisrulz.example.spacexapi.domain.usecase.GetAllLaunches
import com.nisrulz.example.spacexapi.domain.usecase.ToggleBookmarkLaunchInfo
import com.nisrulz.example.spacexapi.logger.InUseLoggers
import com.nisrulz.example.spacexapi.presentation.features.listoflaunches.ListOfLaunchesViewModel.UiEvent.NavigateToDetails
import com.nisrulz.example.spacexapi.presentation.features.listoflaunches.ListOfLaunchesViewModel.UiEvent.ShowSnackBar
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ListOfLaunchesViewModel
@Inject
constructor(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val getAllLaunches: GetAllLaunches,
    private val bookmarkLaunchInfo: ToggleBookmarkLaunchInfo,
    private val getAllBookmarkedLaunches: GetAllBookmarkedLaunches,
    private val logger: InUseLoggers,
    private val analytics: InUseAnalytics
) : ViewModel() {
    var uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState(isLoading = true))
        private set

    /*
     A CONFLATED channel is a type of channel that only preserves the latest value.
     When a new value is sent to the channel, if the previous value has not been consumed yet,
     it will be discarded and replaced with the new value.
     This behavior can be useful in certain scenarios where one only care about the latest
     value and don’t want to process outdated values.
     */
    var eventFlow: Channel<UiEvent> = Channel(Channel.CONFLATED)
        private set

    @VisibleForTesting
    fun getListOfLaunches() = viewModelScope.launch(coroutineDispatcher) {
        getAllLaunches()
            .onEach {
                handleListOfLaunches(it)
            }.catch {
                setError(it.message ?: "Error")
            }.collect()
    }

    fun bookmark(launchInfo: LaunchInfo) = viewModelScope.launch(coroutineDispatcher) {
        bookmarkLaunchInfo(launchInfo)
    }

    private fun handleListOfLaunches(list: List<LaunchInfo>) {
        uiState.update { it.copy(data = list) }
        stopLoading()
    }

    fun onClickBookmarkToolbarIcon() {
        navigateToBookmarks()
    }

    private fun setError(message: String) {
        uiState.update { it.copy(error = message) }
        stopLoading()
    }

    fun showError(message: String) = sendEvent(ShowSnackBar(message))

    fun navigateToDetails(launchId: String) = sendEvent(NavigateToDetails(launchId))
    private fun navigateToBookmarks() = sendEvent(UiEvent.NavigateToBookmarks)

    private fun sendEvent(uiEvent: UiEvent) = viewModelScope.launch(coroutineDispatcher) {
        eventFlow.send(uiEvent)
        logger.log("Ui Event: $uiEvent")
        analytics.trackNavigateToDetail()
    }

    private fun stopLoading() = uiState.update { it.copy(isLoading = false) }

    fun trackScreenEntered() = analytics.trackScreenListOfLaunches()

    sealed interface UiEvent {
        data class ShowSnackBar(val message: String) : UiEvent

        data class NavigateToDetails(val launchId: String) : UiEvent
        data object NavigateToBookmarks : UiEvent
    }

    data class UiState(
        val isLoading: Boolean = false,
        val error: String = "",
        val data: List<LaunchInfo> = emptyList()
    )
}
