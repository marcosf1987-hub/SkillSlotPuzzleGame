package com.skillslot.puzzles.memory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.skillslot.puzzle.engine.PuzzleAction
import com.skillslot.puzzle.engine.PuzzleSession
import com.skillslot.puzzle.engine.PuzzleUiPayload

private val MEMORY_ICONS = listOf(
    Icons.Default.Casino,
    Icons.Default.Diamond,
    Icons.Default.Star,
    Icons.Default.EmojiEvents,
)

private val MEMORY_COLORS = listOf(
    Color(0xFFE53935),
    Color(0xFF1E88E5),
    Color(0xFF43A047),
    Color(0xFFFDD835),
    Color(0xFF8E24AA),
    Color(0xFFFF7043),
    Color(0xFF00ACC1),
    Color(0xFF6D4C41),
)

@Composable
fun MemoryPuzzleContent(
    session: PuzzleSession,
    modifier: Modifier = Modifier,
    interactionEnabled: Boolean = true,
) {
    val uiState by session.state.collectAsState()
    val payload = uiState.payload as? PuzzleUiPayload.Memory ?: return
    val columns = if (payload.cardSymbols.size <= 8) 4 else 4

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = uiState.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        val rows = payload.cardSymbols.chunked(columns)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            rows.forEachIndexed { rowIndex, rowCards ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    rowCards.forEachIndexed { colIndex, _ ->
                        val index = rowIndex * columns + colIndex
                        MemoryCard(
                            symbol = payload.cardSymbols[index],
                            faceUp = index in payload.faceUpIndices || index in payload.matchedIndices,
                            matched = index in payload.matchedIndices,
                            enabled = interactionEnabled,
                            onClick = { session.onUserAction(PuzzleAction.TapIndex(index)) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MemoryCard(
    symbol: Int,
    faceUp: Boolean,
    matched: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val icon: ImageVector = MEMORY_ICONS[symbol % MEMORY_ICONS.size]
    val tint = MEMORY_COLORS[symbol % MEMORY_COLORS.size]
    Box(
        modifier = modifier
            .aspectRatio(0.75f)
            .clip(RoundedCornerShape(10.dp))
            .background(
                when {
                    matched -> tint.copy(alpha = 0.35f)
                    faceUp -> MaterialTheme.colorScheme.surfaceContainerHigh
                    else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                },
            )
            .then(if (enabled && !matched && !faceUp) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        if (faceUp || matched) {
            Icon(imageVector = icon, contentDescription = null, tint = tint)
        } else {
            Text("?", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        }
    }
}
