package com.skillslot.feature.progression

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skillslot.core.model.GameState
import com.skillslot.core.model.ProgressionConfig
import com.skillslot.core.model.PuzzleType

@Composable
fun ProgressionMapScreen(
    gameState: GameState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TierSummaryCard(gameState = gameState)
        Text(
            text = "Puzzles del tier ${gameState.currentTier}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        gameState.puzzleQueue.forEachIndexed { index, type ->
            PuzzleProgressRow(
                index = index,
                type = type,
                status = puzzleStatus(index, gameState),
            )
        }
        if (gameState.tiersCompleted > 0 || gameState.highestTierReached > 1) {
            Text(
                text = "Historial",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
            Text(
                text = "Tiers completados: ${gameState.tiersCompleted} · Máximo alcanzado: tier ${gameState.highestTierReached}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        TierLadder(currentTier = gameState.currentTier, highestTier = gameState.highestTierReached)
    }
}

@Composable
private fun TierSummaryCard(gameState: GameState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Tier ${gameState.currentTier} / ${ProgressionConfig.MAX_TIER}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Siguiente puzzle: ${gameState.pointsThreshold} pts · ${gameState.completedPuzzlesInTier}/10 completados",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        LinearProgressIndicator(
            progress = { gameState.puzzleProgressInTier },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun PuzzleProgressRow(
    index: Int,
    type: PuzzleType,
    status: PuzzleStatus,
) {
    val containerColor = when (status) {
        PuzzleStatus.Completed -> MaterialTheme.colorScheme.tertiaryContainer
        PuzzleStatus.Current -> MaterialTheme.colorScheme.primaryContainer
        PuzzleStatus.Locked -> MaterialTheme.colorScheme.surfaceContainer
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .border(
                width = if (status == PuzzleStatus.Current) 1.dp else 0.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StatusIcon(status = status, index = index)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = type.displayName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (status == PuzzleStatus.Current) FontWeight.Bold else FontWeight.Normal,
            )
            Text(
                text = when (status) {
                    PuzzleStatus.Completed -> "Completado"
                    PuzzleStatus.Current -> "En curso"
                    PuzzleStatus.Locked -> "Bloqueado"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun StatusIcon(status: PuzzleStatus, index: Int) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center,
    ) {
        when (status) {
            PuzzleStatus.Completed -> Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
            PuzzleStatus.Current -> Icon(Icons.Default.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            PuzzleStatus.Locked -> Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun TierLadder(currentTier: Int, highestTier: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Escalera de tiers",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        (ProgressionConfig.MAX_TIER downTo 1).forEach { tier ->
            val reached = tier <= highestTier
            val active = tier == currentTier
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Tier $tier",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                    color = if (reached) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = when {
                        active -> "Actual"
                        reached -> "✓"
                        else -> "—"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private enum class PuzzleStatus { Completed, Current, Locked }

private fun puzzleStatus(index: Int, gameState: GameState): PuzzleStatus = when {
    index < gameState.completedPuzzlesInTier -> PuzzleStatus.Completed
    index == gameState.completedPuzzlesInTier -> PuzzleStatus.Current
    else -> PuzzleStatus.Locked
}
