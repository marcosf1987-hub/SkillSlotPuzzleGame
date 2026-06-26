package com.skillslot.feature.puzzle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skillslot.core.model.GameState
import com.skillslot.core.model.ProgressionConfig
import com.skillslot.core.model.PuzzleType
import com.skillslot.puzzle.engine.PuzzleResult
import com.skillslot.puzzle.engine.PuzzleSession
import com.skillslot.puzzles.wordsearch.WordSearchPuzzleContent
import kotlinx.coroutines.delay

@Composable
fun PuzzleShellScreen(
    gameState: GameState,
    puzzleSession: PuzzleSession?,
    puzzleType: PuzzleType?,
    onPuzzleCompleted: () -> Unit,
    onPuzzleFailed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val timeLimit = ProgressionConfig.puzzleTimeLimitSeconds(gameState.currentTier)
    var remainingSeconds by remember(puzzleSession) { mutableIntStateOf(timeLimit) }
    var phase by remember(puzzleSession) { mutableStateOf<PuzzleShellPhase>(PuzzleShellPhase.Playing) }

    LaunchedEffect(puzzleSession) {
        remainingSeconds = timeLimit
        phase = PuzzleShellPhase.Playing
    }

    LaunchedEffect(phase, puzzleSession) {
        if (phase != PuzzleShellPhase.Playing || puzzleSession == null) return@LaunchedEffect
        while (remainingSeconds > 0) {
            delay(1_000)
            if (phase != PuzzleShellPhase.Playing) return@LaunchedEffect
            remainingSeconds--
        }
        if (phase == PuzzleShellPhase.Playing) {
            phase = PuzzleShellPhase.Defeat("Tiempo agotado")
        }
    }

    LaunchedEffect(puzzleSession) {
        puzzleSession?.result?.collect { result ->
            when (result) {
                PuzzleResult.Completed -> phase = PuzzleShellPhase.Victory
                is PuzzleResult.Failed -> phase = PuzzleShellPhase.Defeat(result.reason)
            }
        }
    }

    val isPlaying = phase == PuzzleShellPhase.Playing
    val defeatReason = (phase as? PuzzleShellPhase.Defeat)?.reason

    if (phase == PuzzleShellPhase.Paused) {
        PauseDialog(
            onResume = { phase = PuzzleShellPhase.Playing },
            onAbandon = { phase = PuzzleShellPhase.Defeat("Abandonaste el puzzle") },
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PuzzleShellHeader(
                puzzleType = puzzleType,
                tier = gameState.currentTier,
                lives = gameState.lives,
                maxLives = gameState.maxLives,
                remainingSeconds = remainingSeconds,
                isTimerCritical = remainingSeconds <= 15,
                onPauseClick = {
                    if (puzzleSession != null && phase == PuzzleShellPhase.Playing) {
                        phase = PuzzleShellPhase.Paused
                    }
                },
            )

            when {
                puzzleSession != null && puzzleType == PuzzleType.WORD_SEARCH -> {
                    WordSearchPuzzleContent(
                        session = puzzleSession,
                        interactionEnabled = isPlaying,
                        modifier = Modifier.weight(1f),
                    )
                }
                puzzleType != null -> {
                    Text(
                        text = "${puzzleType.displayName} — próximamente",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                    )
                }
                else -> {
                    Text(
                        text = "Acumula ${gameState.pointsThreshold} pts en tragamonedas para desbloquear un puzzle.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        PuzzleVictoryOverlay(
            visible = phase == PuzzleShellPhase.Victory,
            gameState = gameState,
            puzzleType = puzzleType,
            onContinue = onPuzzleCompleted,
        )

        PuzzleDefeatOverlay(
            visible = phase is PuzzleShellPhase.Defeat,
            reason = defeatReason ?: "",
            livesRemaining = if (phase is PuzzleShellPhase.Defeat) {
                (gameState.lives - 1).coerceAtLeast(0)
            } else {
                gameState.lives
            },
            onContinue = onPuzzleFailed,
        )
    }
}

@Composable
private fun PauseDialog(
    onResume: () -> Unit,
    onAbandon: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onResume,
        title = { Text("Pausa") },
        text = { Text("¿Reanudar o abandonar? Abandonar cuesta 1 vida.") },
        confirmButton = {
            TextButton(onClick = onResume) {
                Text("Reanudar")
            }
        },
        dismissButton = {
            TextButton(onClick = onAbandon) {
                Text("Abandonar")
            }
        },
    )
}
