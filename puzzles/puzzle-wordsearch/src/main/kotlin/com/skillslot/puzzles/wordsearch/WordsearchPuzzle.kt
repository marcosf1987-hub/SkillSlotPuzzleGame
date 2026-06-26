package com.skillslot.puzzles.wordsearch

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

class WordsearchPuzzle : IPuzzle {
    override val type: PuzzleType = PuzzleType.WORD_SEARCH

    override fun createSession(difficulty: Int, seed: Long): PuzzleSession =
        WordSearchSession(difficulty, seed)
}

private class WordSearchSession(
    difficulty: Int,
    seed: Long,
) : PuzzleSession {
    private val generated = WordSearchGenerator().generate(difficulty, seed)
    private val _state = MutableStateFlow(
        PuzzleUiState(
            title = PuzzleType.WORD_SEARCH.displayName,
            subtitle = "Encuentra ${generated.words.size} palabras",
            payload = PuzzleUiPayload.WordSearch(
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

    private var anchorCell: Pair<Int, Int>? = null

    override fun onUserAction(action: PuzzleAction) {
        when (action) {
            PuzzleAction.Reset -> resetSelection()
            PuzzleAction.ClearSelection -> resetSelection()
            PuzzleAction.ConfirmSelection -> confirmSelection()
            is PuzzleAction.SelectCell -> onSelectCell(action.row, action.col)
            is PuzzleAction.DragToCell -> onDragToCell(action.row, action.col)
        }
    }

    private fun onSelectCell(row: Int, col: Int) {
        val payload = currentPayload() ?: return
        if (anchorCell == null) {
            anchorCell = row to col
            updatePayload(payload.copy(selectedCells = listOf(row to col)))
        } else {
            val selection = buildLineSelection(anchorCell!!, row to col, payload.grid.size)
            validateSelection(payload, selection)
            anchorCell = null
        }
    }

    private fun onDragToCell(row: Int, col: Int) {
        val payload = currentPayload() ?: return
        val anchor = anchorCell ?: run {
            anchorCell = row to col
            updatePayload(payload.copy(selectedCells = listOf(row to col)))
            return
        }
        val selection = buildLineSelection(anchor, row to col, payload.grid.size)
        updatePayload(payload.copy(selectedCells = selection))
    }

    private fun validateSelection(payload: PuzzleUiPayload.WordSearch, selection: List<Pair<Int, Int>>) {
        if (selection.size < 2) {
            resetSelection()
            return
        }
        val word = selection.joinToString("") { (r, c) ->
            payload.grid[r][c].toString()
        }
        val reversed = word.reversed()
        val match = payload.wordsToFind.firstOrNull { candidate ->
            !payload.foundWords.contains(candidate) &&
                (candidate.equals(word, ignoreCase = true) || candidate.equals(reversed, ignoreCase = true))
        }
        if (match != null) {
            val found = payload.foundWords + match.uppercase()
            val remaining = payload.wordsToFind.size - found.size
            updatePayload(
                payload.copy(
                    foundWords = found,
                    selectedCells = emptyList(),
                ),
                subtitle = if (remaining == 0) "¡Completado!" else "Quedan $remaining palabras",
            )
            if (remaining == 0) {
                _result.tryEmit(PuzzleResult.Completed)
            }
        } else {
            resetSelection()
        }
    }

    private fun confirmSelection() {
        val payload = currentPayload() ?: return
        if (payload.selectedCells.size >= 2) {
            validateSelection(payload, payload.selectedCells)
        } else {
            resetSelection()
        }
        anchorCell = null
    }

    private fun resetSelection() {
        anchorCell = null
        val payload = currentPayload() ?: return
        updatePayload(payload.copy(selectedCells = emptyList()))
    }

    private fun updatePayload(payload: PuzzleUiPayload.WordSearch, subtitle: String? = null) {
        _state.value = _state.value.copy(
            subtitle = subtitle ?: _state.value.subtitle,
            payload = payload,
        )
    }

    private fun currentPayload(): PuzzleUiPayload.WordSearch? =
        _state.value.payload as? PuzzleUiPayload.WordSearch

    private fun buildLineSelection(
        start: Pair<Int, Int>,
        end: Pair<Int, Int>,
        size: Int,
    ): List<Pair<Int, Int>> {
        val (r1, c1) = start
        val (r2, c2) = end
        val dr = (r2 - r1).compareTo(0)
        val dc = (c2 - c1).compareTo(0)
        if (dr == 0 && dc == 0) return listOf(start)
        if (r1 != r2 && c1 != c2 && kotlin.math.abs(r2 - r1) != kotlin.math.abs(c2 - c1)) {
            return listOf(start, end)
        }
        val steps = maxOf(kotlin.math.abs(r2 - r1), kotlin.math.abs(c2 - c1))
        return buildList {
            for (step in 0..steps) {
                val r = r1 + dr * step
                val c = c1 + dc * step
                if (r !in 0 until size || c !in 0 until size) break
                add(r to c)
            }
        }
    }
}
