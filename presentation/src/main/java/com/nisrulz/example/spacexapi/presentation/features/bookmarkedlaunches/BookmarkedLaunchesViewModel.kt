package com.nisrulz.example.spacexapi.presentation.features.bookmarkedlaunches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisrulz.example.spacexapi.domain.model.LaunchInfo
import com.nisrulz.example.spacexapi.domain.usecase.GetAllBookmarkedLaunches
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
class BookmarkedLaunchesViewModel
@Inject
constructor(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val bookmarkLaunchInfo: ToggleBookmarkLaunchInfo,
    private val getAllBookmarkedLaunches: GetAllBookmarkedLaunches
) : ViewModel() {
    var uiState: MutableStateFlow<BookmarkedUiState> = MutableStateFlow(BookmarkedUiState(isLoading = true))
        private set

    private val _eventFlow = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    init {
        getListOfBookmarkedLaunches()
    }

    fun getListOfBookmarkedLaunches() = viewModelScope.launch(coroutineDispatcher) {
        getAllBookmarkedLaunches().onEach { list ->
            uiState.update { it.copy(data = list, isLoading = false) }
        }.catch { error ->
            uiState.update { it.copy(isLoading = false) }
            sendUiEvent(_eventFlow, UiEvent.ShowSnackBar(error.message ?: "Error"))
        }.collect()
    }

    fun bookmark(launchInfo: LaunchInfo) = viewModelScope.launch(coroutineDispatcher) {
        bookmarkLaunchInfo(launchInfo)
    }

    data class BookmarkedUiState(
        override val isLoading: Boolean = false,
        val data: List<LaunchInfo> = emptyList()
    ) : UiState
}
