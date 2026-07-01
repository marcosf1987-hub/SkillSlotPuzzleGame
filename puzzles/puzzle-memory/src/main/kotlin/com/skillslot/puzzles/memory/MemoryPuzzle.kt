package com.skillslot.puzzles.memory

import com.skillslot.core.model.PuzzleType
import com.skillslot.puzzle.engine.IPuzzle
import com.skillslot.puzzle.engine.PuzzleAction
import com.skillslot.puzzle.engine.PuzzleResult
import com.skillslot.puzzle.engine.PuzzleSession
import com.skillslot.puzzle.engine.PuzzleUiPayload
import com.skillslot.puzzle.engine.PuzzleUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MemoryPuzzle : IPuzzle {
    override val type: PuzzleType = PuzzleType.MEMORY

    override fun createSession(difficulty: Int, seed: Long): PuzzleSession =
        MemorySession(difficulty, seed)
}

private class MemorySession(
    difficulty: Int,
    seed: Long,
) : PuzzleSession {
    private val generated = MemoryGenerator().generate(difficulty, seed)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var faceUp = setOf<Int>()
    private var matched = setOf<Int>()
    private var firstPick: Int? = null
    private var busy = false

    private val _state = MutableStateFlow(
        PuzzleUiState(
            title = PuzzleType.MEMORY.displayName,
            subtitle = "Encuentra todas las parejas",
            payload = buildPayload(),
        ),
    )
    override val state = _state.asStateFlow()

    private val _result = MutableSharedFlow<PuzzleResult>(extraBufferCapacity = 1)
    override val result: Flow<PuzzleResult> = _result.asSharedFlow()

    override fun onUserAction(action: PuzzleAction) {
        when (action) {
            is PuzzleAction.TapIndex -> onCardTap(action.index)
            else -> Unit
        }
    }

    private fun onCardTap(index: Int) {
        if (busy) return
        if (index !in generated.cardSymbols.indices) return
        if (index in matched || index in faceUp) return

        val first = firstPick
        if (first == null) {
            firstPick = index
            faceUp = faceUp + index
            publish()
            return
        }

        faceUp = faceUp + index
        publish()
        if (generated.cardSymbols[first] == generated.cardSymbols[index]) {
            matched = matched + first + index
            firstPick = null
            faceUp = faceUp - first - index
            publish()
            if (matched.size == generated.cardSymbols.size) {
                _result.tryEmit(PuzzleResult.Completed)
            }
        } else {
            busy = true
            scope.launch {
                delay(700)
                faceUp = faceUp - first - index
                firstPick = null
                busy = false
                publish()
            }
        }
    }

    private fun publish() {
        _state.value = _state.value.copy(payload = buildPayload())
    }

    private fun buildPayload(): PuzzleUiPayload.Memory =
        PuzzleUiPayload.Memory(
            cardSymbols = generated.cardSymbols,
            faceUpIndices = faceUp,
            matchedIndices = matched,
            firstPick = firstPick,
        )
}
