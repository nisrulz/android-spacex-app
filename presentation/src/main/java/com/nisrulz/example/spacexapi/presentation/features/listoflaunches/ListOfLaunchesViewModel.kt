package com.nisrulz.example.spacexapi.presentation.features.listoflaunches

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisrulz.example.spacexapi.analytics.InUseAnalytics
import com.nisrulz.example.spacexapi.analytics.trackScreenListOfLaunches
import com.nisrulz.example.spacexapi.domain.model.LaunchInfo
import com.nisrulz.example.spacexapi.domain.usecase.GetAllLaunches
import com.nisrulz.example.spacexapi.domain.usecase.ToggleBookmarkLaunchInfo
import com.nisrulz.example.spacexapi.logger.InUseLoggers
import com.nisrulz.example.spacexapi.presentation.features.listoflaunches.ListOfLaunchesViewModel.UiEvent.ShowSnackBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListOfLaunchesViewModel
@Inject constructor(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val getAllLaunches: GetAllLaunches,
    private val bookmarkLaunchInfo: ToggleBookmarkLaunchInfo,
    private val logger: InUseLoggers,
    private val analytics: InUseAnalytics
) : ViewModel() {
    var uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState(isLoading = true))
        private set

    private val _eventFlow = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    init {
        // Track screen entered
        trackScreenEntered()
        // Fetch data when ViewModel is created
        getListOfLaunches()
    }

    @VisibleForTesting
    fun getListOfLaunches() = viewModelScope.launch(coroutineDispatcher) {
        getAllLaunches().onEach {
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

    private fun setError(message: String) {
        stopLoading()
        sendEvent(ShowSnackBar(message))
    }

    private fun sendEvent(uiEvent: UiEvent) = viewModelScope.launch(coroutineDispatcher) {
        _eventFlow.emit(uiEvent)
        logger.log("Ui Event: $uiEvent")
    }

    private fun stopLoading() = uiState.update { it.copy(isLoading = false) }

    fun trackScreenEntered() = analytics.trackScreenListOfLaunches()

    sealed interface UiEvent {
        data class ShowSnackBar(val message: String) : UiEvent
    }

    data class UiState(
        val isLoading: Boolean = false,
        val data: List<LaunchInfo> = emptyList()
    )
}
