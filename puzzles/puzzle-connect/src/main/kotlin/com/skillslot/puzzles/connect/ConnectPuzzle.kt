package com.skillslot.puzzles.connect

import com.skillslot.core.model.PuzzleType
import com.skillslot.puzzle.engine.IPuzzle
import com.skillslot.puzzle.engine.PuzzleAction
import com.skillslot.puzzle.engine.PuzzleResult
import com.skillslot.puzzle.engine.PuzzleSession
import com.skillslot.puzzle.engine.PuzzleUiPayload
import com.skillslot.puzzle.engine.PuzzleUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class ConnectPuzzle : IPuzzle {
    override val type: PuzzleType = PuzzleType.CONNECT

    override fun createSession(difficulty: Int, seed: Long): PuzzleSession =
        ConnectSession(difficulty, seed)
}

private class ConnectSession(
    difficulty: Int,
    seed: Long,
) : PuzzleSession {
    private val generated = ConnectGenerator().generate(difficulty, seed)
    private var completedPairs = setOf<Int>()
    private var activePair: Int? = null
    private var currentPath = listOf<Pair<Int, Int>>()
    private val occupiedCells = mutableSetOf<Pair<Int, Int>>()

    init {
        generated.endpoints.values.forEach { (a, b) ->
            occupiedCells += a
            occupiedCells += b
        }
    }

    private val _state = MutableStateFlow(
        PuzzleUiState(
            title = PuzzleType.CONNECT.displayName,
            subtitle = "Une cada par de números sin cruzar caminos",
            payload = buildPayload(),
        ),
    )
    override val state = _state.asStateFlow()

    private val _result = MutableSharedFlow<PuzzleResult>(extraBufferCapacity = 1)
    override val result: Flow<PuzzleResult> = _result.asSharedFlow()

    override fun onUserAction(action: PuzzleAction) {
        when (action) {
            is PuzzleAction.SelectCell -> onSelect(action.row, action.col)
            is PuzzleAction.DragToCell -> onDrag(action.row, action.col)
            PuzzleAction.ClearSelection -> resetPath()
            else -> Unit
        }
    }

    private fun nextPair(): Int? =
        generated.endpoints.keys.sorted().firstOrNull { it !in completedPairs }

    private fun onSelect(row: Int, col: Int) {
        val cell = row to col
        val number = cellNumber(row, col, generated.endpoints) ?: return
        if (number in completedPairs) return
        val expected = nextPair()
        if (expected != null && number != expected) return

        val endpoints = generated.endpoints[number] ?: return
        if (currentPath.isEmpty()) {
            if (cell != endpoints.first && cell != endpoints.second) return
            activePair = number
            currentPath = listOf(cell)
            publish()
        }
    }

    private fun onDrag(row: Int, col: Int) {
        val pair = activePair ?: return
        if (currentPath.isEmpty()) return
        val cell = row to col
        val endpoints = generated.endpoints[pair] ?: return
        val last = currentPath.last()

        if (cell == last) return
        if (!isAdjacentConnect(last, cell)) return
        if (cell in currentPath) {
            if (currentPath.size > 1 && currentPath[currentPath.size - 2] == cell) {
                currentPath = currentPath.dropLast(1)
                publish()
            }
            return
        }
        val endpointNumber = cellNumber(row, col, generated.endpoints)
        if (endpointNumber != null && endpointNumber != pair) return
        if (cell in occupiedCells && cell != endpoints.first && cell != endpoints.second) return

        currentPath = currentPath + cell
        publish()

        if (cell == endpoints.first || cell == endpoints.second) {
            if (currentPath.size >= 2) {
                completedPairs = completedPairs + pair
                currentPath.forEach { occupiedCells += it }
                activePair = null
                currentPath = emptyList()
                publish()
                if (completedPairs.size == generated.endpoints.size) {
                    _result.tryEmit(PuzzleResult.Completed)
                }
            }
        }
    }

    private fun resetPath() {
        activePair = null
        currentPath = emptyList()
        publish()
    }

    private fun publish() {
        _state.value = _state.value.copy(payload = buildPayload())
    }

    private fun buildPayload(): PuzzleUiPayload.Connect =
        PuzzleUiPayload.Connect(
            size = generated.size,
            endpoints = generated.endpoints,
            completedPairs = completedPairs,
            activePair = activePair,
            currentPath = currentPath,
        )
}
