package com.nisrulz.example.spacexapi.presentation.features.launchdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisrulz.example.spacexapi.domain.model.LaunchInfo
import com.nisrulz.example.spacexapi.domain.usecase.GetLaunchDetail
import com.nisrulz.example.spacexapi.domain.usecase.ToggleBookmarkLaunchInfo
import com.nisrulz.example.spacexapi.presentation.features.launchdetail.LaunchDetailViewModel.LaunchDetailUiEvent.ShowSnackBar
import com.nisrulz.example.spacexapi.presentation.features.launchdetail.LaunchDetailViewModel.LaunchDetailUiState.Error
import com.nisrulz.example.spacexapi.presentation.features.launchdetail.LaunchDetailViewModel.LaunchDetailUiState.Loading
import com.nisrulz.example.spacexapi.presentation.features.launchdetail.LaunchDetailViewModel.LaunchDetailUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LaunchDetailViewModel
    @Inject
    constructor(
        private val coroutineDispatcher: CoroutineDispatcher,
        private val getLaunchDetail: GetLaunchDetail,
        private val bookmarkLaunchInfo: ToggleBookmarkLaunchInfo,
    ) : ViewModel() {
        var uiState: MutableStateFlow<LaunchDetailUiState> = MutableStateFlow(Loading)
            private set

        var eventFlow: Channel<LaunchDetailUiEvent> = Channel(Channel.CONFLATED)
            private set

        fun getLaunchInfoDetails(launchId: String?) =
            viewModelScope.launch(coroutineDispatcher) {
                if (launchId.isNullOrBlank()) {
                    uiState.update { Error("No Data") }
                } else {
                    val launchInfo = getLaunchDetail(launchId)
                    uiState.update { Success(launchInfo) }
                }
            }

        fun bookmark(launchInfo: LaunchInfo) =
            viewModelScope.launch(coroutineDispatcher) {
                uiState.update { Success(launchInfo) }
                bookmarkLaunchInfo(launchInfo)
            }

        fun showError(message: String) =
            viewModelScope.launch(coroutineDispatcher) {
                eventFlow.send(ShowSnackBar(message))
            }

        sealed interface LaunchDetailUiState {
            data object Loading : LaunchDetailUiState

            data class Error(val message: String) : LaunchDetailUiState

            data class Success(val data: LaunchInfo?) : LaunchDetailUiState
        }

        sealed interface LaunchDetailUiEvent {
            data class ShowSnackBar(val message: String) : LaunchDetailUiEvent
        }
    }
