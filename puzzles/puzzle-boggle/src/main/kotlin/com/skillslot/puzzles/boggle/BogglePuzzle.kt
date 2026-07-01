package com.skillslot.puzzles.boggle

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

class BogglePuzzle : IPuzzle {
    override val type: PuzzleType = PuzzleType.BOGGLE

    override fun createSession(difficulty: Int, seed: Long): PuzzleSession =
        BoggleSession(difficulty, seed)
}

private class BoggleSession(
    difficulty: Int,
    seed: Long,
) : PuzzleSession {
    private val generated = BoggleGenerator().generate(difficulty, seed)
    private var anchorCell: Pair<Int, Int>? = null

    private val _state = MutableStateFlow(
        PuzzleUiState(
            title = PuzzleType.BOGGLE.displayName,
            subtitle = "Encuentra ${generated.words.size} palabras enlazando letras",
            payload = PuzzleUiPayload.Boggle(
                grid = generated.grid,
                wordsToFind = generated.words,
                foundWords = emptySet(),
                selectedCells = emptyList(),
            ),
        ),
    )
    override val state = _state.asStateFlow()

    private val _result = MutableSharedFlow<PuzzleResult>(extraBufferCapacity = 1)
    override val result: Flow<PuzzleResult> = _result.asSharedFlow()

    override fun onUserAction(action: PuzzleAction) {
        when (action) {
            PuzzleAction.Reset, PuzzleAction.ClearSelection -> resetSelection()
            PuzzleAction.ConfirmSelection -> confirmSelection()
            is PuzzleAction.SelectCell -> onSelectCell(action.row, action.col)
            is PuzzleAction.DragToCell -> onDragToCell(action.row, action.col)
            else -> Unit
        }
    }

    private fun onSelectCell(row: Int, col: Int) {
        val payload = currentPayload()
        anchorCell = row to col
        updatePayload(payload.copy(selectedCells = listOf(row to col)))
    }

    private fun onDragToCell(row: Int, col: Int) {
        val payload = currentPayload()
        val anchor = anchorCell ?: run {
            onSelectCell(row, col)
            return
        }
        if (payload.selectedCells.isEmpty()) {
            updatePayload(payload.copy(selectedCells = listOf(anchor)))
        }
        val selection = if (payload.selectedCells.size <= 1) {
            buildAdjacentSelection(payload.grid, listOf(anchor), row to col)
        } else {
            buildAdjacentSelection(payload.grid, payload.selectedCells, row to col)
        }
        updatePayload(payload.copy(selectedCells = selection))
    }

    private fun confirmSelection() {
        val payload = currentPayload()
        if (payload.selectedCells.size < 3) {
            resetSelection()
            return
        }
        val word = isValidBogglePath(payload.grid, payload.selectedCells) ?: run {
            resetSelection()
            return
        }
        val match = payload.wordsToFind.firstOrNull { candidate ->
            !payload.foundWords.contains(candidate) &&
                candidate.equals(word, ignoreCase = true)
        }
        if (match != null) {
            val found = payload.foundWords + match.uppercase()
            val remaining = payload.wordsToFind.size - found.size
            updatePayload(
                payload.copy(foundWords = found, selectedCells = emptyList()),
                subtitle = if (remaining == 0) "¡Completado!" else "Quedan $remaining palabras",
            )
            anchorCell = null
            if (remaining == 0) {
                _result.tryEmit(PuzzleResult.Completed)
            }
        } else {
            resetSelection()
        }
    }

    private fun resetSelection() {
        anchorCell = null
        val payload = currentPayload()
        updatePayload(payload.copy(selectedCells = emptyList()))
    }

    private fun updatePayload(payload: PuzzleUiPayload.Boggle, subtitle: String? = null) {
        _state.value = _state.value.copy(
            subtitle = subtitle ?: _state.value.subtitle,
            payload = payload,
        )
    }

    private fun currentPayload(): PuzzleUiPayload.Boggle =
        _state.value.payload as PuzzleUiPayload.Boggle
}
