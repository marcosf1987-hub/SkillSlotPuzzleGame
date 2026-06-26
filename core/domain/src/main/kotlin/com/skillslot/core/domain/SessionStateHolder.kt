package com.skillslot.core.domain

import com.skillslot.core.model.GameState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SessionStateHolder(initialState: GameState) {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<GameState> = _state.asStateFlow()

    fun update(transform: (GameState) -> GameState) {
        _state.update(transform)
    }

    fun replace(state: GameState) {
        _state.value = state
    }
}
