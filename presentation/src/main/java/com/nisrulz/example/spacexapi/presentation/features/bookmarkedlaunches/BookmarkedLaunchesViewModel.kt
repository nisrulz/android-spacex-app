package com.nisrulz.example.spacexapi.presentation.features.bookmarkedlaunches

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisrulz.example.spacexapi.domain.model.LaunchInfo
import com.nisrulz.example.spacexapi.domain.usecase.GetAllBookmarkedLaunches
import com.nisrulz.example.spacexapi.domain.usecase.ToggleBookmarkLaunchInfo
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
class BookmarkedLaunchesViewModel
@Inject
constructor(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val bookmarkLaunchInfo: ToggleBookmarkLaunchInfo,
    private val getAllBookmarkedLaunches: GetAllBookmarkedLaunches
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
    fun getListOfBookmarkedLaunches() = viewModelScope.launch(coroutineDispatcher) {
        getAllBookmarkedLaunches()
            .onEach {
                handleListOfLaunches(it)
            }.catch {
                setError(it.message ?: "Error")
            }.collect()
    }

    fun bookmark(launchInfo: LaunchInfo) = viewModelScope.launch(coroutineDispatcher) {
        bookmarkLaunchInfo(launchInfo)
    }

    private fun stopLoading() = uiState.update { it.copy(isLoading = false) }
    private fun setError(message: String) {
        uiState.update { it.copy(error = message) }
        stopLoading()
    }

    private fun handleListOfLaunches(list: List<LaunchInfo>) {
        uiState.update { it.copy(data = list) }
        stopLoading()
    }

    fun showError(message: String) = sendEvent(UiEvent.ShowSnackBar(message))

    private fun sendEvent(uiEvent: UiEvent) = viewModelScope.launch(coroutineDispatcher) {
        eventFlow.send(uiEvent)
    }

    sealed interface UiEvent {
        data class ShowSnackBar(val message: String) : UiEvent
    }

    data class UiState(
        val isLoading: Boolean = false,
        val error: String = "",
        val data: List<LaunchInfo> = emptyList()
    )
}
