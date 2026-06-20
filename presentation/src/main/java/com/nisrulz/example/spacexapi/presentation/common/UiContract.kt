package com.nisrulz.example.spacexapi.presentation.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

sealed interface UiEvent {
    data class ShowSnackBar(val message: String) : UiEvent
}

interface UiState {
    val isLoading: Boolean
}

fun CoroutineScope.sendUiEvent(
    eventFlow: MutableSharedFlow<UiEvent>,
    event: UiEvent
) {
    launch {
        eventFlow.emit(event)
    }
}
