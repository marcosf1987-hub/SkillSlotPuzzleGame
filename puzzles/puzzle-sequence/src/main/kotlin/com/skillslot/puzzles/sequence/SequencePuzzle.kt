package com.skillslot.puzzles.sequence

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

class SequencePuzzle : IPuzzle {
    override val type: PuzzleType = PuzzleType.SEQUENCE

    override fun createSession(difficulty: Int, seed: Long): PuzzleSession =
        SequenceSession(difficulty, seed)
}

private class SequenceSession(
    difficulty: Int,
    seed: Long,
) : PuzzleSession {
    private val generated = SequenceGenerator().generate(difficulty, seed)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var inputProgress = 0
    private var highlightedPad: Int? = null
    private var awaitingInput = false
    private var playbackActive = true

    private val _state = MutableStateFlow(
        PuzzleUiState(
            title = PuzzleType.SEQUENCE.displayName,
            subtitle = "Memoriza y repite la secuencia",
            payload = buildPayload(),
        ),
    )
    override val state = _state.asStateFlow()

    private val _result = MutableSharedFlow<PuzzleResult>(extraBufferCapacity = 1)
    override val result: Flow<PuzzleResult> = _result.asSharedFlow()

    init {
        scope.launch { playSequence() }
    }

    override fun onUserAction(action: PuzzleAction) {
        when (action) {
            is PuzzleAction.TapIndex -> onPadTap(action.index)
            else -> Unit
        }
    }

    private suspend fun playSequence() {
        playbackActive = true
        awaitingInput = false
        publish(subtitle = "Observa la secuencia…")
        delay(400)
        for (pad in generated.sequence) {
            highlightedPad = pad
            publish()
            delay(550)
            highlightedPad = null
            publish()
            delay(200)
        }
        playbackActive = false
        awaitingInput = true
        publish(subtitle = "Tu turno — repite la secuencia")
    }

    private fun onPadTap(index: Int) {
        if (!awaitingInput || playbackActive) return
        if (index !in 0 until generated.padCount) return
        highlightedPad = index
        publish()
        scope.launch {
            delay(250)
            highlightedPad = null
            publish()
        }
        val expected = generated.sequence[inputProgress]
        if (index != expected) {
            awaitingInput = false
            publish(subtitle = "Secuencia incorrecta")
            _result.tryEmit(PuzzleResult.Failed("Secuencia incorrecta"))
            return
        }
        inputProgress++
        publish()
        if (inputProgress >= generated.sequence.size) {
            awaitingInput = false
            _result.tryEmit(PuzzleResult.Completed)
        }
    }

    private fun publish(subtitle: String? = null) {
        _state.value = _state.value.copy(
            subtitle = subtitle ?: _state.value.subtitle,
            payload = buildPayload(),
        )
    }

    private fun buildPayload(): PuzzleUiPayload.Sequence =
        PuzzleUiPayload.Sequence(
            padCount = generated.padCount,
            targetLength = generated.sequence.size,
            inputProgress = inputProgress,
            highlightedPad = highlightedPad,
            awaitingInput = awaitingInput,
            playbackActive = playbackActive,
        )
}
