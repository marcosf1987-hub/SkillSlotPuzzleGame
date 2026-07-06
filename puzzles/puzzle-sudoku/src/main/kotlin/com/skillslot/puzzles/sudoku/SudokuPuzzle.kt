package com.skillslot.puzzles.sudoku

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

class SudokuPuzzle : IPuzzle {
    override val type: PuzzleType = PuzzleType.SUDOKU

    override fun createSession(difficulty: Int, seed: Long): PuzzleSession =
        SudokuSession(difficulty, seed)
}

private class SudokuSession(
    difficulty: Int,
    seed: Long,
) : PuzzleSession {
    private val generator = SudokuGenerator()
    private val generated = generator.generate(difficulty, seed)
    private val fixedCells = buildSet {
        generated.puzzle.forEachIndexed { row, line ->
            line.forEachIndexed { col, value ->
                if (value != 0) add(row to col)
            }
        }
    }
    private var grid = generated.puzzle.map { it.toMutableList() }.toMutableList()

    private val _state = MutableStateFlow(
        PuzzleUiState(
            title = PuzzleType.SUDOKU.displayName,
            subtitle = subtitleFor(selectedCell = null),
            payload = buildPayload(selectedCell = null),
        ),
    )
    override val state = _state.asStateFlow()

    private val _result = MutableSharedFlow<PuzzleResult>(extraBufferCapacity = 1)
    override val result: Flow<PuzzleResult> = _result.asSharedFlow()

    override fun onUserAction(action: PuzzleAction) {
        when (action) {
            is PuzzleAction.SelectCell -> selectCell(action.row, action.col)
            is PuzzleAction.SetDigit -> setDigit(action.digit)
            else -> Unit
        }
    }

    private fun selectCell(row: Int, col: Int) {
        if (row !in 0 until 9 || col !in 0 until 9) return
        if ((row to col) in fixedCells) return
        publish(selectedCell = row to col)
    }

    private fun setDigit(digit: Int) {
        val selected = currentPayload().selectedCell ?: return
        val (row, col) = selected
        if ((row to col) in fixedCells) return
        grid[row][col] = digit.coerceIn(0, 9)
        publish(selectedCell = selected)
        if (isComplete()) {
            _result.tryEmit(PuzzleResult.Completed)
        }
    }

    private fun isComplete(): Boolean {
        for (row in grid) {
            if (row.any { it == 0 }) return false
        }
        return generator.findConflicts(grid).isEmpty()
    }

    private fun publish(selectedCell: Pair<Int, Int>?) {
        _state.value = _state.value.copy(
            subtitle = subtitleFor(selectedCell),
            payload = buildPayload(selectedCell),
        )
    }

    private fun subtitleFor(selectedCell: Pair<Int, Int>?): String = when (selectedCell) {
        null -> "Toca una celda vacía para empezar"
        else -> "Elige 1–9 · ⌫ borra · Los números en rojo chocan"
    }

    private fun buildPayload(selectedCell: Pair<Int, Int>?): PuzzleUiPayload.Sudoku =
        PuzzleUiPayload.Sudoku(
            grid = grid.map { it.toList() },
            fixedCells = fixedCells,
            selectedCell = selectedCell,
            conflictCells = generator.findConflicts(grid),
        )

    private fun currentPayload(): PuzzleUiPayload.Sudoku =
        _state.value.payload as PuzzleUiPayload.Sudoku
}
