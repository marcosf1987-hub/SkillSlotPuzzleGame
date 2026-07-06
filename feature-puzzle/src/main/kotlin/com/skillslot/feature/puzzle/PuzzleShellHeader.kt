package com.skillslot.feature.puzzle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skillslot.core.model.PuzzleType

@Composable
internal fun PuzzleShellHeader(
    puzzleType: PuzzleType?,
    tier: Int,
    lives: Int,
    maxLives: Int,
    remainingSeconds: Int,
    isTimerCritical: Boolean,
    onPauseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp),
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ColumnHeaderInfo(
            puzzleType = puzzleType,
            tier = tier,
        )
        TimerAndLives(
            remainingSeconds = remainingSeconds,
            isTimerCritical = isTimerCritical,
            lives = lives,
            maxLives = maxLives,
        )
        IconButton(onClick = onPauseClick) {
            Icon(
                imageVector = Icons.Default.Pause,
                contentDescription = "Pausar",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun ColumnHeaderInfo(
    puzzleType: PuzzleType?,
    tier: Int,
) {
    androidx.compose.foundation.layout.Column {
        Text(
            text = puzzleType?.displayName ?: "Puzzle",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "TIER $tier",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun TimerAndLives(
    remainingSeconds: Int,
    isTimerCritical: Boolean,
    lives: Int,
    maxLives: Int,
) {
    androidx.compose.foundation.layout.Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = formatTimer(remainingSeconds),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = if (isTimerCritical) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.secondary
            },
        )
        Text(
            text = "♥".repeat(lives.coerceAtLeast(0)) +
                "♡".repeat((maxLives - lives).coerceAtLeast(0)),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary,
        )
    }
}
