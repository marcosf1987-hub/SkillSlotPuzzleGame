package com.skillslot.puzzles.ballsort

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

private val BALL_COLORS = listOf(
    Color(0xFFE53935),
    Color(0xFF1E88E5),
    Color(0xFF43A047),
    Color(0xFFFDD835),
    Color(0xFF8E24AA),
    Color(0xFFFF7043),
    Color(0xFF00ACC1),
)

@Composable
fun BallSortPuzzleContent(
    session: PuzzleSession,
    modifier: Modifier = Modifier,
    interactionEnabled: Boolean = true,
) {
    val uiState by session.state.collectAsState()
    val payload = uiState.payload as? PuzzleUiPayload.BallSort ?: return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = uiState.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        ) {
            payload.tubes.forEachIndexed { index, tube ->
                BallTube(
                    balls = tube,
                    selected = payload.selectedTube == index,
                    interactionEnabled = interactionEnabled,
                    onClick = { session.onUserAction(PuzzleAction.TapIndex(index)) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun BallTube(
    balls: List<Int>,
    selected: Boolean,
    interactionEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    }
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .border(2.dp, borderColor, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .then(if (interactionEnabled) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            repeat(4 - balls.size) { SpacerBall() }
            balls.asReversed().forEach { colorId ->
                Ball(color = BALL_COLORS[colorId % BALL_COLORS.size])
            }
        }
    }
}

@Composable
private fun Ball(color: Color) {
    Box(
        modifier = Modifier
            .padding(vertical = 2.dp)
            .size(28.dp)
            .clip(CircleShape)
            .background(color),
    )
}

@Composable
private fun SpacerBall() {
    Box(modifier = Modifier.size(28.dp).padding(vertical = 2.dp))
}
