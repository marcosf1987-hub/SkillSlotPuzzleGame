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
    showAds: Boolean,
    onBeforeVictory: (onReady: () -> Unit) -> Unit,
    onBeforeDefeat: (onReady: () -> Unit) -> Unit,
    onPuzzleCompleted: () -> Unit,
    onPuzzleFailed: () -> Unit,
    onWatchRewarded: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val timeLimit = ProgressionConfig.puzzleTimeLimitSeconds(gameState.currentTier)
    var remainingSeconds by remember(puzzleSession) { mutableIntStateOf(timeLimit) }
    var phase by remember(puzzleSession) { mutableStateOf<PuzzleShellPhase>(PuzzleShellPhase.Playing) }
    var victoryVisible by remember(puzzleSession) { mutableStateOf(false) }
    var defeatVisible by remember(puzzleSession) { mutableStateOf(false) }
    var defeatReason by remember(puzzleSession) { mutableStateOf("") }

    LaunchedEffect(puzzleSession) {
        remainingSeconds = timeLimit
        phase = PuzzleShellPhase.Playing
        victoryVisible = false
        defeatVisible = false
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

    LaunchedEffect(phase) {
        when (val current = phase) {
            PuzzleShellPhase.Victory -> onBeforeVictory { victoryVisible = true }
            is PuzzleShellPhase.Defeat -> {
                defeatReason = current.reason
                onBeforeDefeat { defeatVisible = true }
            }
            else -> Unit
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
                puzzleSession != null && puzzleType == PuzzleType.SUDOKU -> {
                    com.skillslot.puzzles.sudoku.SudokuPuzzleContent(
                        session = puzzleSession,
                        interactionEnabled = isPlaying,
                        modifier = Modifier.weight(1f),
                    )
                }
                puzzleSession != null && puzzleType == PuzzleType.BALL_SORT -> {
                    com.skillslot.puzzles.ballsort.BallSortPuzzleContent(
                        session = puzzleSession,
                        interactionEnabled = isPlaying,
                        modifier = Modifier.weight(1f),
                    )
                }
                puzzleSession != null && puzzleType == PuzzleType.MAZE -> {
                    com.skillslot.puzzles.maze.MazePuzzleContent(
                        session = puzzleSession,
                        interactionEnabled = isPlaying,
                        modifier = Modifier.weight(1f),
                    )
                }
                puzzleSession != null && puzzleType == PuzzleType.BOGGLE -> {
                    com.skillslot.puzzles.boggle.BogglePuzzleContent(
                        session = puzzleSession,
                        interactionEnabled = isPlaying,
                        modifier = Modifier.weight(1f),
                    )
                }
                puzzleSession != null && puzzleType == PuzzleType.MEMORY -> {
                    com.skillslot.puzzles.memory.MemoryPuzzleContent(
                        session = puzzleSession,
                        interactionEnabled = isPlaying,
                        modifier = Modifier.weight(1f),
                    )
                }
                puzzleSession != null && puzzleType == PuzzleType.NONOGRAM -> {
                    com.skillslot.puzzles.nonogram.NonogramPuzzleContent(
                        session = puzzleSession,
                        interactionEnabled = isPlaying,
                        modifier = Modifier.weight(1f),
                    )
                }
                puzzleSession != null && puzzleType == PuzzleType.SLIDING -> {
                    com.skillslot.puzzles.sliding.SlidingPuzzleContent(
                        session = puzzleSession,
                        interactionEnabled = isPlaying,
                        modifier = Modifier.weight(1f),
                    )
                }
                puzzleSession != null && puzzleType == PuzzleType.CONNECT -> {
                    com.skillslot.puzzles.connect.ConnectPuzzleContent(
                        session = puzzleSession,
                        interactionEnabled = isPlaying,
                        modifier = Modifier.weight(1f),
                    )
                }
                puzzleSession != null && puzzleType == PuzzleType.SEQUENCE -> {
                    com.skillslot.puzzles.sequence.SequencePuzzleContent(
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
            visible = victoryVisible,
            gameState = gameState,
            puzzleType = puzzleType,
            onContinue = onPuzzleCompleted,
        )

        PuzzleDefeatOverlay(
            visible = defeatVisible,
            reason = defeatReason,
            livesRemaining = (gameState.lives - 1).coerceAtLeast(0),
            showRewardedOption = showAds && gameState.lives <= 1,
            onWatchRewarded = onWatchRewarded,
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
