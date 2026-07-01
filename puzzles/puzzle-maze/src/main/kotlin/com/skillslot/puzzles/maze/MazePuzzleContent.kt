package com.skillslot.puzzles.maze

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.skillslot.puzzle.engine.PuzzleAction
import com.skillslot.puzzle.engine.PuzzleSession
import com.skillslot.puzzle.engine.PuzzleUiPayload

@Composable
fun MazePuzzleContent(
    session: PuzzleSession,
    modifier: Modifier = Modifier,
    interactionEnabled: Boolean = true,
) {
    val uiState by session.state.collectAsState()
    val payload = uiState.payload as? PuzzleUiPayload.Maze ?: return

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = uiState.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        MazeGrid(
            payload = payload,
            interactionEnabled = interactionEnabled,
            onCell = { row, col -> session.onUserAction(PuzzleAction.DragToCell(row, col)) },
            onDragStart = { row, col -> session.onUserAction(PuzzleAction.SelectCell(row, col)) },
        )
    }
}

@Composable
private fun MazeGrid(
    payload: PuzzleUiPayload.Maze,
    interactionEnabled: Boolean,
    onCell: (Int, Int) -> Unit,
    onDragStart: (Int, Int) -> Unit,
) {
    val trailSet = payload.trail.toSet()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .then(
                if (interactionEnabled) {
                    Modifier.pointerInput(payload.walls) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                cellAt(offset.x, offset.y, payload.walls.size, size.width.toFloat())
                                    ?.let { (r, c) -> onDragStart(r, c) }
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                cellAt(change.position.x, change.position.y, payload.walls.size, size.width.toFloat())
                                    ?.let { (r, c) -> onCell(r, c) }
                            },
                        )
                    }
                } else {
                    Modifier
                },
            ),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            payload.walls.forEachIndexed { row, line ->
                Row(modifier = Modifier.weight(1f)) {
                    line.forEachIndexed { col, isWall ->
                        val pos = row to col
                        val color = when {
                            isWall -> MaterialTheme.colorScheme.surfaceContainerHighest
                            pos == payload.goal -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
                            pos == payload.start -> MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                            pos in trailSet -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.45f)
                            else -> MaterialTheme.colorScheme.surface
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(color),
                        )
                    }
                }
            }
        }
    }
}

private fun cellAt(x: Float, y: Float, gridSize: Int, width: Float): Pair<Int, Int>? {
    if (width <= 0f) return null
    val cellSize = width / gridSize
    val col = (x / cellSize).toInt().coerceIn(0, gridSize - 1)
    val row = (y / cellSize).toInt().coerceIn(0, gridSize - 1)
    return row to col
}
