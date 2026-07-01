package com.skillslot.puzzles.connect

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skillslot.puzzle.engine.PuzzleAction
import com.skillslot.puzzle.engine.PuzzleSession
import com.skillslot.puzzle.engine.PuzzleUiPayload

@Composable
fun ConnectPuzzleContent(
    session: PuzzleSession,
    modifier: Modifier = Modifier,
    interactionEnabled: Boolean = true,
) {
    val uiState by session.state.collectAsState()
    val payload = uiState.payload as? PuzzleUiPayload.Connect ?: return
    val pathSet = payload.currentPath.toSet()

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = uiState.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .then(
                    if (interactionEnabled) {
                        Modifier.pointerInput(payload.size) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    cellAt(offset.x, offset.y, payload.size, size.width.toFloat())
                                        ?.let { (r, c) ->
                                            session.onUserAction(PuzzleAction.SelectCell(r, c))
                                        }
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    cellAt(change.position.x, change.position.y, payload.size, size.width.toFloat())
                                        ?.let { (r, c) ->
                                            session.onUserAction(PuzzleAction.DragToCell(r, c))
                                        }
                                },
                                onDragEnd = { session.onUserAction(PuzzleAction.ClearSelection) },
                                onDragCancel = { session.onUserAction(PuzzleAction.ClearSelection) },
                            )
                        }
                    } else {
                        Modifier
                    },
                ),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                repeat(payload.size) { row ->
                    Row(modifier = Modifier.weight(1f)) {
                        repeat(payload.size) { col ->
                            val cell = row to col
                            val endpointNumber = payload.endpoints.entries.firstOrNull { (_, pair) ->
                                pair.first == cell || pair.second == cell
                            }?.key
                            val onPath = cell in pathSet
                            val completed = endpointNumber != null && endpointNumber in payload.completedPairs
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        when {
                                            onPath -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.45f)
                                            completed -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                                            else -> MaterialTheme.colorScheme.surfaceContainerHigh
                                        },
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                endpointNumber?.let { number ->
                                    Text(
                                        text = number.toString(),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }
                        }
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
