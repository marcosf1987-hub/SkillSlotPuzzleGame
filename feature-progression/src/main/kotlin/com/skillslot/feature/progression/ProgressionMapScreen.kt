package com.skillslot.feature.progression

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
import com.skillslot.core.model.PuzzleType

@Composable
fun ProgressionMapScreen(
    gameState: GameState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Mapa de progresión", style = MaterialTheme.typography.headlineMedium)
        Text("Tier ${gameState.currentTier}")
        PuzzleType.defaultQueue.forEachIndexed { index, type ->
            val status = when {
                index < gameState.completedPuzzlesInTier -> "✓"
                index == gameState.completedPuzzlesInTier -> "→"
                else -> "·"
            }
            Text("$status ${type.displayName}")
        }
    }
}
