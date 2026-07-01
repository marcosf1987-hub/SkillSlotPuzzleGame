package com.skillslot.puzzles.nonogram

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skillslot.puzzle.engine.PuzzleAction
import com.skillslot.puzzle.engine.PuzzleSession
import com.skillslot.puzzle.engine.PuzzleUiPayload

@Composable
fun NonogramPuzzleContent(
    session: PuzzleSession,
    modifier: Modifier = Modifier,
    interactionEnabled: Boolean = true,
) {
    val uiState by session.state.collectAsState()
    val payload = uiState.payload as? PuzzleUiPayload.Nonogram ?: return

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = uiState.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.width(40.dp))
            payload.colClues.forEach { clues ->
                Text(
                    text = clues.joinToString("\n"),
                    modifier = Modifier.weight(1f),
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        payload.filled.forEachIndexed { row, line ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = payload.rowClues[row].joinToString(" "),
                    modifier = Modifier.width(40.dp),
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                line.forEachIndexed { col, filled ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(1.dp)
                            .background(
                                if (filled) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.surfaceContainerHigh
                                },
                            )
                            .then(
                                if (interactionEnabled) {
                                    Modifier.clickable {
                                        session.onUserAction(PuzzleAction.SelectCell(row, col))
                                    }
                                } else {
                                    Modifier
                                },
                            ),
                    )
                }
            }
        }
    }
}
