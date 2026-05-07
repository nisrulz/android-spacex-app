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
class BookmarkedLaunchesViewModel
@Inject
constructor(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val bookmarkLaunchInfo: ToggleBookmarkLaunchInfo,
    private val getAllBookmarkedLaunches: GetAllBookmarkedLaunches
) : ViewModel() {
    var uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState(isLoading = true))
        private set

    private val _eventFlow = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    init {
        // Fetch data when ViewModel is created
        getListOfBookmarkedLaunches()
    }

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
        stopLoading()
        sendEvent(UiEvent.ShowSnackBar(message))
    }

    private fun handleListOfLaunches(list: List<LaunchInfo>) {
        uiState.update { it.copy(data = list) }
        stopLoading()
    }

    private fun sendEvent(uiEvent: UiEvent) = viewModelScope.launch(coroutineDispatcher) {
        _eventFlow.emit(uiEvent)
    }

    sealed interface UiEvent {
        data class ShowSnackBar(val message: String) : UiEvent
    }

    data class UiState(
        val isLoading: Boolean = false,
        val data: List<LaunchInfo> = emptyList()
    )
}
