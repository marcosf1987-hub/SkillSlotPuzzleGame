package com.skillslot.puzzle.engine

import com.skillslot.core.model.PuzzleType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface IPuzzle {
    val type: PuzzleType
    fun createSession(difficulty: Int, seed: Long): PuzzleSession
}

interface PuzzleSession {
    val state: StateFlow<PuzzleUiState>
    fun onUserAction(action: PuzzleAction)
    val result: Flow<PuzzleResult>
}

data class PuzzleUiState(
    val title: String,
    val subtitle: String = "",
    val isInteractive: Boolean = true,
    val payload: PuzzleUiPayload = PuzzleUiPayload.Empty,
)

sealed interface PuzzleUiPayload {
    data object Empty : PuzzleUiPayload

    data class WordSearch(
        val grid: List<List<Char>>,
        val wordsToFind: List<String>,
        val foundWords: Set<String>,
        val selectedCells: List<Pair<Int, Int>>,
    ) : PuzzleUiPayload
}

sealed interface PuzzleAction {
    data object Reset : PuzzleAction
    data class SelectCell(val row: Int, val col: Int) : PuzzleAction
    data class DragToCell(val row: Int, val col: Int) : PuzzleAction
    data object ClearSelection : PuzzleAction
    data object ConfirmSelection : PuzzleAction
}

sealed interface PuzzleResult {
    data object Completed : PuzzleResult
    data class Failed(val reason: String) : PuzzleResult
}

class PuzzleRegistry(
    puzzles: Set<IPuzzle>,
) {
    private val byType: Map<PuzzleType, IPuzzle> = puzzles.associateBy { it.type }

    fun get(type: PuzzleType): IPuzzle? = byType[type]

    fun all(): List<IPuzzle> = byType.values.sortedBy { it.type.order }
}
