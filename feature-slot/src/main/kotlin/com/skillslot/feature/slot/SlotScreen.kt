package com.skillslot.feature.slot

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skillslot.core.model.GameState
import com.skillslot.core.model.SlotSpinResult
import com.skillslot.core.model.SlotSymbol
import com.skillslot.core.domain.SlotEngine
import kotlin.random.Random

@Composable
fun SlotScreen(
    gameState: GameState,
    lastSpin: SlotSpinResult?,
    isSpinning: Boolean,
    showUnlockDialog: Boolean,
    returnMessage: String? = null,
    onDismissReturnMessage: () -> Unit = {},
    onSpin: () -> Unit,
    onPlayPuzzle: () -> Unit,
    onDismissUnlock: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val displayGrid = lastSpin?.grid ?: defaultGrid()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AnimatedVisibility(
            visible = returnMessage != null,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300)),
        ) {
            returnMessage?.let { message ->
                LaunchedEffect(message) {
                    kotlinx.coroutines.delay(4_000)
                    onDismissReturnMessage()
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(12.dp),
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
        HeaderStats(gameState = gameState)
        ProgressToPuzzle(gameState = gameState)
        SlotReels(
            grid = displayGrid,
            isSpinning = isSpinning,
        )
        lastSpin?.winLabel?.let { label ->
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
        SpinButton(
            enabled = gameState.isSessionActive && !isSpinning,
            onClick = onSpin,
        )
    }

    if (showUnlockDialog && gameState.puzzleUnlockAvailable) {
        AlertDialog(
            onDismissRequest = onDismissUnlock,
            title = { Text("¡Puzzle desbloqueado!") },
            text = {
                Text(
                    "Tienes ${gameState.slotPoints} pts. ¿Jugar ${gameState.currentPuzzleType?.displayName ?: "puzzle"}?",
                )
            },
            confirmButton = {
                TextButton(onClick = onPlayPuzzle) {
                    Text("Jugar puzzle")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissUnlock) {
                    Text("Seguir en slots")
                }
            },
        )
    }
}

@Composable
private fun HeaderStats(gameState: GameState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = "${gameState.slotPoints} pts",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "Meta: ${gameState.pointsThreshold}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "Tier ${gameState.currentTier}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
            Text(
                text = "Puzzle ${gameState.completedPuzzlesInTier + 1}/10",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "❤ ${gameState.lives}/${gameState.maxLives}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
    }
}

@Composable
private fun ProgressToPuzzle(gameState: GameState) {
    val progress = (gameState.slotPoints.toFloat() / gameState.pointsThreshold.coerceAtLeast(1))
        .coerceIn(0f, 1f)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Progreso al puzzle",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
    }
}

@Composable
private fun SlotReels(
    grid: List<List<SlotSymbol>>,
    isSpinning: Boolean,
) {
    var animatedGrid by remember { mutableStateOf(grid) }
    LaunchedEffect(isSpinning, grid) {
        if (isSpinning) {
            repeat(12) {
                animatedGrid = List(SlotEngine.GRID_SIZE) {
                    List(SlotEngine.GRID_SIZE) { SlotSymbol.entries.random(Random) }
                }
                kotlinx.coroutines.delay(80)
            }
        }
        animatedGrid = grid
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(12.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            animatedGrid.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    row.forEach { symbol ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = symbol.display,
                                fontSize = 28.sp,
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
private fun SpinButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    ) {
        Text(
            text = if (enabled) "SPIN" else "Girando…",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}

private fun defaultGrid(): List<List<SlotSymbol>> =
    List(SlotEngine.GRID_SIZE) {
        List(SlotEngine.GRID_SIZE) { SlotSymbol.CHERRY }
    }
