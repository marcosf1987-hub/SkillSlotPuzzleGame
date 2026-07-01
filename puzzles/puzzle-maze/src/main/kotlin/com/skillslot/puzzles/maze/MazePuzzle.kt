package com.skillslot.puzzles.maze

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

class MazePuzzle : IPuzzle {
    override val type: PuzzleType = PuzzleType.MAZE

    override fun createSession(difficulty: Int, seed: Long): PuzzleSession =
        MazeSession(difficulty, seed)
}

private class MazeSession(
    difficulty: Int,
    seed: Long,
) : PuzzleSession {
    private val generated = MazeGenerator().generate(difficulty, seed)
    private var playerPos = generated.start
    private var trail = listOf(generated.start)

    private val _state = MutableStateFlow(
        PuzzleUiState(
            title = PuzzleType.MAZE.displayName,
            subtitle = "Arrastra desde la entrada hasta la salida",
            payload = buildPayload(),
        ),
    )
    override val state = _state.asStateFlow()

    private val _result = MutableSharedFlow<PuzzleResult>(extraBufferCapacity = 1)
    override val result: Flow<PuzzleResult> = _result.asSharedFlow()

    override fun onUserAction(action: PuzzleAction) {
        when (action) {
            is PuzzleAction.DragToCell -> moveTo(action.row, action.col)
            is PuzzleAction.SelectCell -> resetTo(action.row, action.col)
            PuzzleAction.Reset -> resetTrail()
            else -> Unit
        }
    }

    private fun resetTo(row: Int, col: Int) {
        if (row to col != generated.start) return
        if (generated.walls[row][col]) return
        playerPos = generated.start
        trail = listOf(generated.start)
        publish()
    }

    private fun moveTo(row: Int, col: Int) {
        if (row !in generated.walls.indices || col !in generated.walls[row].indices) return
        if (generated.walls[row][col]) return
        val next = row to col
        if (!isAdjacent(playerPos, next) && next != playerPos) return
        if (trail.size > 1 && trail[trail.size - 2] == next) {
            trail = trail.dropLast(1)
            playerPos = next
        } else if (next !in trail && isAdjacent(playerPos, next)) {
            trail = trail + next
            playerPos = next
        } else if (next == playerPos) {
            return
        } else {
            return
        }
        publish()
        if (playerPos == generated.goal) {
            _result.tryEmit(PuzzleResult.Completed)
        }
    }

    private fun resetTrail() {
        playerPos = generated.start
        trail = listOf(generated.start)
        publish()
    }

    private fun publish() {
        _state.value = _state.value.copy(payload = buildPayload())
    }

    private fun buildPayload(): PuzzleUiPayload.Maze =
        PuzzleUiPayload.Maze(
            walls = generated.walls,
            playerPos = playerPos,
            start = generated.start,
            goal = generated.goal,
            trail = trail,
        )
}
