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

    data class Sudoku(
        val grid: List<List<Int>>,
        val fixedCells: Set<Pair<Int, Int>>,
        val selectedCell: Pair<Int, Int>?,
        val conflictCells: Set<Pair<Int, Int>>,
    ) : PuzzleUiPayload

    data class BallSort(
        val tubes: List<List<Int>>,
        val selectedTube: Int?,
        val colorCount: Int,
    ) : PuzzleUiPayload

    data class Maze(
        val walls: List<List<Boolean>>,
        val playerPos: Pair<Int, Int>,
        val start: Pair<Int, Int>,
        val goal: Pair<Int, Int>,
        val trail: List<Pair<Int, Int>>,
    ) : PuzzleUiPayload

    data class Boggle(
        val grid: List<List<Char>>,
        val wordsToFind: List<String>,
        val foundWords: Set<String>,
        val selectedCells: List<Pair<Int, Int>>,
    ) : PuzzleUiPayload

    data class Memory(
        val cardSymbols: List<Int>,
        val faceUpIndices: Set<Int>,
        val matchedIndices: Set<Int>,
        val firstPick: Int?,
    ) : PuzzleUiPayload

    data class Nonogram(
        val size: Int,
        val rowClues: List<List<Int>>,
        val colClues: List<List<Int>>,
        val filled: List<List<Boolean>>,
    ) : PuzzleUiPayload

    data class Sliding(
        val size: Int,
        val tiles: List<List<Int>>,
    ) : PuzzleUiPayload

    data class Connect(
        val size: Int,
        val endpoints: Map<Int, Pair<Pair<Int, Int>, Pair<Int, Int>>>,
        val completedPairs: Set<Int>,
        val activePair: Int?,
        val currentPath: List<Pair<Int, Int>>,
    ) : PuzzleUiPayload

    data class Sequence(
        val padCount: Int,
        val targetLength: Int,
        val inputProgress: Int,
        val highlightedPad: Int?,
        val awaitingInput: Boolean,
        val playbackActive: Boolean,
    ) : PuzzleUiPayload
}

sealed interface PuzzleAction {
    data object Reset : PuzzleAction
    data class SelectCell(val row: Int, val col: Int) : PuzzleAction
    data class DragToCell(val row: Int, val col: Int) : PuzzleAction
    data object ClearSelection : PuzzleAction
    data object ConfirmSelection : PuzzleAction
    data class SetDigit(val digit: Int) : PuzzleAction
    data class TapIndex(val index: Int) : PuzzleAction
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
