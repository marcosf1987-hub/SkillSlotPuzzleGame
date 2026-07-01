package com.skillslot.puzzles.sliding

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

class SlidingPuzzle : IPuzzle {
    override val type: PuzzleType = PuzzleType.SLIDING

    override fun createSession(difficulty: Int, seed: Long): PuzzleSession =
        SlidingSession(difficulty, seed)
}

private class SlidingSession(
    difficulty: Int,
    seed: Long,
) : PuzzleSession {
    private val generated = SlidingGenerator().generate(difficulty, seed)
    private var tiles = generated.tiles

    private val _state = MutableStateFlow(
        PuzzleUiState(
            title = PuzzleType.SLIDING.displayName,
            subtitle = "Ordena las piezas (toca junto al hueco)",
            payload = buildPayload(),
        ),
    )
    override val state = _state.asStateFlow()

    private val _result = MutableSharedFlow<PuzzleResult>(extraBufferCapacity = 1)
    override val result: Flow<PuzzleResult> = _result.asSharedFlow()

    override fun onUserAction(action: PuzzleAction) {
        when (action) {
            is PuzzleAction.SelectCell -> move(action.row, action.col)
            else -> Unit
        }
    }

    private fun move(row: Int, col: Int) {
        val next = tryMove(tiles, row, col) ?: return
        tiles = next
        publish()
        if (isSlidingSolved(tiles)) {
            _result.tryEmit(PuzzleResult.Completed)
        }
    }

    private fun publish() {
        _state.value = _state.value.copy(payload = buildPayload())
    }

    private fun buildPayload(): PuzzleUiPayload.Sliding =
        PuzzleUiPayload.Sliding(size = generated.size, tiles = tiles)
}
