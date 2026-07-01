package com.skillslot.puzzles.nonogram

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

class NonogramPuzzle : IPuzzle {
    override val type: PuzzleType = PuzzleType.NONOGRAM

    override fun createSession(difficulty: Int, seed: Long): PuzzleSession =
        NonogramSession(difficulty, seed)
}

private class NonogramSession(
    difficulty: Int,
    seed: Long,
) : PuzzleSession {
    private val generated = NonogramGenerator().generate(difficulty, seed)
    private var filled = List(generated.solution.size) {
        List(generated.solution.size) { false }
    }

    private val _state = MutableStateFlow(
        PuzzleUiState(
            title = PuzzleType.NONOGRAM.displayName,
            subtitle = "Rellena según las pistas",
            payload = buildPayload(),
        ),
    )
    override val state = _state.asStateFlow()

    private val _result = MutableSharedFlow<PuzzleResult>(extraBufferCapacity = 1)
    override val result: Flow<PuzzleResult> = _result.asSharedFlow()

    override fun onUserAction(action: PuzzleAction) {
        when (action) {
            is PuzzleAction.SelectCell -> toggleCell(action.row, action.col)
            else -> Unit
        }
    }

    private fun toggleCell(row: Int, col: Int) {
        if (row !in filled.indices || col !in filled[row].indices) return
        filled = filled.mapIndexed { r, line ->
            if (r != row) line
            else line.mapIndexed { c, value -> if (c == col) !value else value }
        }
        publish()
        if (isNonogramSolved(generated.solution, filled)) {
            _result.tryEmit(PuzzleResult.Completed)
        }
    }

    private fun publish() {
        _state.value = _state.value.copy(payload = buildPayload())
    }

    private fun buildPayload(): PuzzleUiPayload.Nonogram =
        PuzzleUiPayload.Nonogram(
            size = generated.solution.size,
            rowClues = generated.rowClues,
            colClues = generated.colClues,
            filled = filled,
        )
}
