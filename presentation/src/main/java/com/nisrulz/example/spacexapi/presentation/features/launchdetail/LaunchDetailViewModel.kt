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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
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

    private val _eventFlow = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    fun getLaunchInfoDetails(launchId: String?) = viewModelScope.launch(coroutineDispatcher) {
        if (launchId.isNullOrBlank()) {
            setError("No Data")
            stopLoading()
        } else {
            trackScreenEntered()
            getLaunchDetail(launchId)
                .onEach {
                    update(it)
                    stopLoading()
                }.catch {
                    setError(it.message ?: "Error")
                    stopLoading()
                }.collect()
        }
    }

    fun bookmark(launchInfo: LaunchInfo) {
        viewModelScope.launch(coroutineDispatcher) {
            bookmarkLaunchInfo(launchInfo)
        }
    }

    private fun update(launchInfo: LaunchInfo?) = uiState.update { it.copy(data = launchInfo) }

    private fun setError(message: String) = sendEvent(ShowSnackBar(message))

    private fun stopLoading() = uiState.update { it.copy(isLoading = false) }

    private fun sendEvent(uiEvent: UiEvent) = viewModelScope.launch(coroutineDispatcher) {
        _eventFlow.emit(uiEvent)
    }

    fun trackScreenEntered() = analytics.trackScreenLaunchDetail()
    fun trackOnBack() = analytics.trackNavigateToListOfLaunches()

    data class UiState(
        val isLoading: Boolean = false,
        val data: LaunchInfo? = null
    )

    sealed interface UiEvent {
        data class ShowSnackBar(val message: String) : UiEvent
    }
}
