package com.skillslot.puzzles.ballsort

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

class BallsortPuzzle : IPuzzle {
    override val type: PuzzleType = PuzzleType.BALL_SORT

    override fun createSession(difficulty: Int, seed: Long): PuzzleSession =
        BallSortSession(difficulty, seed)
}

private class BallSortSession(
    difficulty: Int,
    seed: Long,
) : PuzzleSession {
    private val generated = BallSortGenerator().generate(difficulty, seed)
    private var tubes = generated.tubes
    private var selectedTube: Int? = null

    private val _state = MutableStateFlow(
        PuzzleUiState(
            title = PuzzleType.BALL_SORT.displayName,
            subtitle = "Ordena los colores en cada tubo",
            payload = buildPayload(),
        ),
    )
    override val state = _state.asStateFlow()

    private val _result = MutableSharedFlow<PuzzleResult>(extraBufferCapacity = 1)
    override val result: Flow<PuzzleResult> = _result.asSharedFlow()

    override fun onUserAction(action: PuzzleAction) {
        when (action) {
            is PuzzleAction.TapIndex -> onTubeTap(action.index)
            else -> Unit
        }
    }

    private fun onTubeTap(index: Int) {
        if (index !in tubes.indices) return
        val current = selectedTube
        if (current == null) {
            if (tubes[index].isNotEmpty()) {
                selectedTube = index
                publish()
            }
            return
        }
        if (current == index) {
            selectedTube = null
            publish()
            return
        }
        tubes = moveBall(tubes, current, index)
        selectedTube = null
        publish()
        if (isBallSortSolved(tubes)) {
            _result.tryEmit(PuzzleResult.Completed)
        }
    }

    private fun publish() {
        _state.value = _state.value.copy(
            subtitle = "Toca un tubo origen y luego un destino",
            payload = buildPayload(),
        )
    }

    private fun buildPayload(): PuzzleUiPayload.BallSort =
        PuzzleUiPayload.BallSort(
            tubes = tubes,
            selectedTube = selectedTube,
            colorCount = generated.colorCount,
        )
}
