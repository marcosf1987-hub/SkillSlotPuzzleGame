package com.skillslot.puzzles.boggle

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skillslot.puzzle.engine.PuzzleAction
import com.skillslot.puzzle.engine.PuzzleSession
import com.skillslot.puzzle.engine.PuzzleUiPayload

@Composable
fun BogglePuzzleContent(
    session: PuzzleSession,
    modifier: Modifier = Modifier,
    interactionEnabled: Boolean = true,
) {
    val uiState by session.state.collectAsState()
    val payload = uiState.payload as? PuzzleUiPayload.Boggle ?: return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = uiState.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        BoggleGrid(
            grid = payload.grid,
            selectedCells = payload.selectedCells.toSet(),
            interactionEnabled = interactionEnabled,
            onCellDrag = { row, col -> session.onUserAction(PuzzleAction.DragToCell(row, col)) },
            onDragEnd = { session.onUserAction(PuzzleAction.ConfirmSelection) },
        )
        BoggleWordList(
            words = payload.wordsToFind,
            foundWords = payload.foundWords,
        )
    }
}

@Composable
private fun BoggleGrid(
    grid: List<List<Char>>,
    selectedCells: Set<Pair<Int, Int>>,
    interactionEnabled: Boolean,
    onCellDrag: (Int, Int) -> Unit,
    onDragEnd: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .then(
                if (interactionEnabled) {
                    Modifier.pointerInput(grid) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                cellAt(offset.x, offset.y, grid.size, size.width.toFloat())
                                    ?.let { (r, c) -> onCellDrag(r, c) }
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                cellAt(change.position.x, change.position.y, grid.size, size.width.toFloat())
                                    ?.let { (r, c) -> onCellDrag(r, c) }
                            },
                            onDragEnd = { onDragEnd() },
                            onDragCancel = { onDragEnd() },
                        )
                    }
                } else {
                    Modifier
                },
            ),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            grid.forEachIndexed { row, line ->
                Row(modifier = Modifier.weight(1f)) {
                    line.forEachIndexed { col, char ->
                        val selected = (row to col) in selectedCells
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (selected) {
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
                                    } else {
                                        MaterialTheme.colorScheme.surfaceContainerHigh
                                    },
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = char.toString(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BoggleWordList(
    words: List<String>,
    foundWords: Set<String>,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Palabras",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        words.forEach { word ->
            val found = foundWords.contains(word.uppercase())
            Text(
                text = word,
                style = MaterialTheme.typography.bodyMedium,
                color = if (found) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                textDecoration = if (found) TextDecoration.LineThrough else null,
            )
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
