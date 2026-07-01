package com.skillslot.puzzles.sudoku

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skillslot.puzzle.engine.PuzzleAction
import com.skillslot.puzzle.engine.PuzzleSession
import com.skillslot.puzzle.engine.PuzzleUiPayload

@Composable
fun SudokuPuzzleContent(
    session: PuzzleSession,
    modifier: Modifier = Modifier,
    interactionEnabled: Boolean = true,
) {
    val uiState by session.state.collectAsState()
    val payload = uiState.payload as? PuzzleUiPayload.Sudoku ?: return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = uiState.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        SudokuGrid(
            payload = payload,
            interactionEnabled = interactionEnabled,
            onCellClick = { row, col -> session.onUserAction(PuzzleAction.SelectCell(row, col)) },
        )
        DigitPad(
            enabled = interactionEnabled && payload.selectedCell != null,
            onDigit = { session.onUserAction(PuzzleAction.SetDigit(it)) },
        )
    }
}

@Composable
private fun SudokuGrid(
    payload: PuzzleUiPayload.Sudoku,
    interactionEnabled: Boolean,
    onCellClick: (Int, Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .border(2.dp, MaterialTheme.colorScheme.primary),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            payload.grid.forEachIndexed { row, line ->
                Row(modifier = Modifier.weight(1f)) {
                    line.forEachIndexed { col, value ->
                        val isFixed = (row to col) in payload.fixedCells
                        val isSelected = payload.selectedCell == (row to col)
                        val hasConflict = (row to col) in payload.conflictCells
                        val thickRight = col == 2 || col == 5
                        val thickBottom = row == 2 || row == 5
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    when {
                                        hasConflict -> MaterialTheme.colorScheme.error.copy(alpha = 0.25f)
                                        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                        else -> MaterialTheme.colorScheme.surfaceContainerHigh
                                    },
                                )
                                .border(
                                    width = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                )
                                .then(
                                    if (thickRight) {
                                        Modifier.border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                    } else {
                                        Modifier
                                    },
                                )
                                .then(
                                    if (interactionEnabled && !isFixed) {
                                        Modifier.clickable { onCellClick(row, col) }
                                    } else {
                                        Modifier
                                    },
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (value != 0) {
                                Text(
                                    text = value.toString(),
                                    fontSize = 18.sp,
                                    fontWeight = if (isFixed) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isFixed) {
                                        MaterialTheme.colorScheme.onSurface
                                    } else {
                                        MaterialTheme.colorScheme.secondary
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DigitPad(
    enabled: Boolean,
    onDigit: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        listOf(listOf(1, 2, 3, 4, 5), listOf(6, 7, 8, 9, 0)).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                row.forEach { digit ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .clickable(enabled = enabled) { onDigit(digit) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (digit == 0) "⌫" else digit.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }
    }
}
