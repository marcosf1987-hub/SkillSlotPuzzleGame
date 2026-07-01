package com.skillslot.puzzles.sequence

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.skillslot.puzzle.engine.PuzzleAction
import com.skillslot.puzzle.engine.PuzzleSession
import com.skillslot.puzzle.engine.PuzzleUiPayload

private val PAD_COLORS = listOf(
    Color(0xFFE53935),
    Color(0xFF1E88E5),
    Color(0xFF43A047),
    Color(0xFFFDD835),
)

@Composable
fun SequencePuzzleContent(
    session: PuzzleSession,
    modifier: Modifier = Modifier,
    interactionEnabled: Boolean = true,
) {
    val uiState by session.state.collectAsState()
    val payload = uiState.payload as? PuzzleUiPayload.Sequence ?: return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = uiState.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "Progreso: ${payload.inputProgress}/${payload.targetLength}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        ) {
            repeat(payload.padCount) { index ->
                val lit = payload.highlightedPad == index
                val color = PAD_COLORS[index % PAD_COLORS.size]
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(72.dp)
                        .clip(CircleShape)
                        .background(if (lit) color else color.copy(alpha = 0.35f))
                        .then(
                            if (interactionEnabled && payload.awaitingInput && !payload.playbackActive) {
                                Modifier.clickable {
                                    session.onUserAction(PuzzleAction.TapIndex(index))
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
