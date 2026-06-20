package com.nisrulz.example.spacexapi.presentation.features.launchdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisrulz.example.spacexapi.analytics.InUseAnalytics
import com.nisrulz.example.spacexapi.analytics.trackNavigateToListOfLaunches
import com.nisrulz.example.spacexapi.analytics.trackScreenLaunchDetail
import com.nisrulz.example.spacexapi.domain.model.LaunchInfo
import com.nisrulz.example.spacexapi.domain.usecase.GetLaunchDetail
import com.nisrulz.example.spacexapi.domain.usecase.ToggleBookmarkLaunchInfo
import com.nisrulz.example.spacexapi.presentation.common.UiEvent
import com.nisrulz.example.spacexapi.presentation.common.UiState
import com.nisrulz.example.spacexapi.presentation.common.sendUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
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
    var uiState: MutableStateFlow<DetailUiState> = MutableStateFlow(DetailUiState(isLoading = true))
        private set

    private val _eventFlow = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    fun getLaunchInfoDetails(launchId: String?) = viewModelScope.launch(coroutineDispatcher) {
        if (launchId.isNullOrBlank()) {
            uiState.update { it.copy(isLoading = false) }
            sendUiEvent(_eventFlow, UiEvent.ShowSnackBar("No Data"))
        } else {
            trackScreenEntered()
            getLaunchDetail(launchId)
                .onEach { launchInfo ->
                    uiState.update { it.copy(data = launchInfo, isLoading = false) }
                }.catch { error ->
                    uiState.update { it.copy(isLoading = false) }
                    sendUiEvent(_eventFlow, UiEvent.ShowSnackBar(error.message ?: "Error"))
                }.collect()
        }
    }

    fun bookmark(launchInfo: LaunchInfo) {
        viewModelScope.launch(coroutineDispatcher) {
            bookmarkLaunchInfo(launchInfo)
        }
    }

    fun trackScreenEntered() = analytics.trackScreenLaunchDetail()
    fun trackOnBack() = analytics.trackNavigateToListOfLaunches()

    data class DetailUiState(
        override val isLoading: Boolean = false,
        val data: LaunchInfo? = null
    ) : UiState
}
