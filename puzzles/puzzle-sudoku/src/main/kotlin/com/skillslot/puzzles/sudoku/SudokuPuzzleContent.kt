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
import androidx.compose.ui.text.style.TextAlign
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
        SudokuRulesHint()
        Text(
            text = uiState.subtitle,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
        )
        SudokuGrid(
            payload = payload,
            interactionEnabled = interactionEnabled,
            onCellClick = { row, col -> session.onUserAction(PuzzleAction.SelectCell(row, col)) },
        )
        if (payload.selectedCell == null) {
            Text(
                text = "Las celdas en negrita son fijas y no se pueden editar.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        DigitPad(
            enabled = interactionEnabled && payload.selectedCell != null,
            onDigit = { session.onUserAction(PuzzleAction.SetDigit(it)) },
        )
    }
}

@Composable
private fun SudokuRulesHint() {
    Text(
        text = "Completa el 9×9: cada fila, columna y bloque 3×3 debe tener los dígitos 1–9 sin repetir.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                shape = RoundedCornerShape(8.dp),
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
    )
}

@Composable
private fun SudokuGrid(
    payload: PuzzleUiPayload.Sudoku,
    interactionEnabled: Boolean,
    onCellClick: (Int, Int) -> Unit,
) {
    val selected = payload.selectedCell
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            payload.grid.forEachIndexed { row, line ->
                Row(modifier = Modifier.weight(1f)) {
                    line.forEachIndexed { col, value ->
                        val isFixed = (row to col) in payload.fixedCells
                        val isSelected = selected == (row to col)
                        val hasConflict = (row to col) in payload.conflictCells
                        val isRelated = selected != null && isRelatedCell(row, col, selected)
                        val thickRight = col == 2 || col == 5
                        val thickBottom = row == 2 || row == 5
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(cellBackground(hasConflict, isSelected, isRelated))
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
                                    color = when {
                                        hasConflict -> MaterialTheme.colorScheme.error
                                        isFixed -> MaterialTheme.colorScheme.onSurface
                                        else -> MaterialTheme.colorScheme.secondary
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
private fun cellBackground(
    hasConflict: Boolean,
    isSelected: Boolean,
    isRelated: Boolean,
) = when {
    hasConflict -> MaterialTheme.colorScheme.error.copy(alpha = 0.25f)
    isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
    isRelated -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
    else -> MaterialTheme.colorScheme.surfaceContainerHigh
}

private fun isRelatedCell(row: Int, col: Int, selected: Pair<Int, Int>): Boolean {
    val (selRow, selCol) = selected
    if (row == selRow || col == selCol) return true
    return row / 3 == selRow / 3 && col / 3 == selCol / 3
}

@Composable
private fun DigitPad(
    enabled: Boolean,
    onDigit: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (!enabled) {
            Text(
                text = "Selecciona una celda vacía para activar el teclado",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        listOf(listOf(1, 2, 3, 4, 5), listOf(6, 7, 8, 9, 0)).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                row.forEach { digit ->
                    val shape = RoundedCornerShape(8.dp)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(shape)
                            .background(
                                if (enabled) {
                                    MaterialTheme.colorScheme.surfaceContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f)
                                },
                            )
                            .border(
                                width = 1.dp,
                                color = if (enabled) {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                } else {
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                },
                                shape = shape,
                            )
                            .clickable(enabled = enabled) { onDigit(digit) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (digit == 0) "⌫" else digit.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (enabled) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            },
                        )
                    }
                }
            }
        }
    }
}
