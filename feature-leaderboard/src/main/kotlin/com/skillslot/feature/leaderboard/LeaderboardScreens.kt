package com.skillslot.feature.leaderboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skillslot.core.model.GameState

@Composable
fun LeaderboardScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Ranking global", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Fase 3b: Firebase Firestore top 100",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun GameOverScreen(
    gameState: GameState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Game Over", style = MaterialTheme.typography.headlineMedium)
        Text("Puntuación de sesión: ${gameState.sessionScore}")
        Text("Tier alcanzado: ${gameState.currentTier}")
        Text("Puzzles completados: ${gameState.totalPuzzlesEverCompleted}")
    }
}
