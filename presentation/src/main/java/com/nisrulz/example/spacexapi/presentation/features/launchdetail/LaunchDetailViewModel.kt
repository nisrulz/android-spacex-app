package com.nisrulz.example.spacexapi.presentation.features.launchdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisrulz.example.spacexapi.analytics.InUseAnalytics
import com.nisrulz.example.spacexapi.analytics.trackNavigateToListOfLaunches
import com.nisrulz.example.spacexapi.analytics.trackScreenLaunchDetail
import com.nisrulz.example.spacexapi.domain.model.LaunchInfo
import com.nisrulz.example.spacexapi.domain.usecase.GetLaunchDetail
import com.nisrulz.example.spacexapi.domain.usecase.ToggleBookmarkLaunchInfo
import com.nisrulz.example.spacexapi.presentation.features.launchdetail.LaunchDetailViewModel.UiEvent.ShowSnackBar
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LaunchDetailViewModel
@Inject
constructor(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val getLaunchDetail: GetLaunchDetail,
    private val bookmarkLaunchInfo: ToggleBookmarkLaunchInfo,
    private val analytics: InUseAnalytics
) : ViewModel() {
    var uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState(isLoading = true))
        private set

    var eventFlow: Channel<UiEvent> = Channel(Channel.CONFLATED)
        private set

    fun getLaunchInfoDetails(launchId: String?) = viewModelScope.launch(coroutineDispatcher) {
        stopLoading()
        if (launchId.isNullOrBlank()) {
            setError("No Data")
        } else {
            update(getLaunchDetail(launchId))
        }
    }

    fun bookmark(launchInfo: LaunchInfo) {
        update(launchInfo)
        viewModelScope.launch(coroutineDispatcher) {
            bookmarkLaunchInfo(launchInfo)
        }
    }

    fun showError(message: String) = viewModelScope.launch(coroutineDispatcher) {
        eventFlow.send(ShowSnackBar(message))
    }

    private fun update(launchInfo: LaunchInfo?) = uiState.update { it.copy(data = launchInfo) }

    private fun setError(message: String) = uiState.update { it.copy(error = message) }

    private fun stopLoading() = uiState.update { it.copy(isLoading = false) }

    fun navigateBack() = sendEvent(UiEvent.NavigateBack)

    private fun sendEvent(uiEvent: UiEvent) = viewModelScope.launch(coroutineDispatcher) {
        eventFlow.send(uiEvent)
    }

    fun trackScreenEntered() = analytics.trackScreenLaunchDetail()
    fun trackOnBack() = analytics.trackNavigateToListOfLaunches()

    data class UiState(
        val isLoading: Boolean = false,
        val error: String = "",
        val data: LaunchInfo? = null
    )

    sealed interface UiEvent {
        data class ShowSnackBar(val message: String) : UiEvent

        data object NavigateBack : UiEvent
    }
}
