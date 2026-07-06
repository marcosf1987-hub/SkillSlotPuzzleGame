package com.skillslot.feature.puzzle

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.skillslot.core.model.GameState
import com.skillslot.core.model.PuzzleType

@Composable
internal fun PuzzleVictoryOverlay(
    visible: Boolean,
    gameState: GameState,
    puzzleType: PuzzleType?,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ResultOverlay(
        visible = visible,
        modifier = modifier,
        title = "¡Puzzle completado!",
        subtitle = buildString {
            append("Completaste el puzzle ${gameState.completedPuzzlesInTier + 1}/10")
            append(" · Tier ${gameState.currentTier}")
        },
        accent = MaterialTheme.colorScheme.primary,
        buttonText = "Volver a tragamonedas",
        onAction = onContinue,
    )
}

@Composable
internal fun PuzzleDefeatOverlay(
    visible: Boolean,
    reason: String,
    livesRemaining: Int,
    showRewardedOption: Boolean,
    onWatchRewarded: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ResultOverlay(
        visible = visible,
        modifier = modifier,
        title = "Puzzle fallido",
        subtitle = buildString {
            append(reason)
            append("\n")
            if (livesRemaining > 0) {
                append("Te quedan $livesRemaining vida(s).")
            } else {
                append("Sin vidas — Game Over.")
            }
        },
        accent = MaterialTheme.colorScheme.error,
        buttonText = if (livesRemaining > 0) "Volver a tragamonedas" else "Ver Game Over",
        secondaryButtonText = if (showRewardedOption) "Ver video (+1 vida)" else null,
        onSecondaryAction = onWatchRewarded,
        onAction = onContinue,
    )
}

@Composable
private fun ResultOverlay(
    visible: Boolean,
    title: String,
    subtitle: String,
    accent: androidx.compose.ui.graphics.Color,
    buttonText: String,
    onAction: () -> Unit,
    secondaryButtonText: String? = null,
    onSecondaryAction: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300)) + scaleIn(initialScale = 0.92f, animationSpec = tween(300)),
        exit = fadeOut(tween(200)),
        modifier = modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.72f)),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = RoundedCornerShape(20.dp),
                    )
                    .border(
                        width = 2.dp,
                        color = accent.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(20.dp),
                    )
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = accent,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                if (secondaryButtonText != null) {
                    Button(
                        onClick = onSecondaryAction,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        ),
                    ) {
                        Text(secondaryButtonText)
                    }
                }
                Button(
                    onClick = onAction,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Text(buttonText.uppercase())
                }
            }
        }
    }
}
