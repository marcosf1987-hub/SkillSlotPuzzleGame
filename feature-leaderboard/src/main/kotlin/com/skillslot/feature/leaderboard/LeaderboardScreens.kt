package com.skillslot.feature.leaderboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skillslot.core.model.GameState
import com.skillslot.core.model.LeaderboardEntry

@Composable
fun LeaderboardScreen(
    entries: List<LeaderboardEntry>,
    localPlayerId: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Ranking global", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "Top ${entries.size} · almacenado en el dispositivo",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (entries.isEmpty()) {
            Text(
                text = "Aún no hay puntuaciones. ¡Sé el primero en publicar!",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 24.dp),
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                itemsIndexed(entries) { index, entry ->
                    LeaderboardRow(
                        rank = index + 1,
                        entry = entry,
                        highlighted = entry.playerId == localPlayerId,
                    )
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRow(
    rank: Int,
    entry: LeaderboardEntry,
    highlighted: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "#$rank",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (highlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            )
            Column {
                Text(
                    text = entry.alias,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (highlighted) FontWeight.Bold else FontWeight.Normal,
                )
                Text(
                    text = "Tier ${entry.tierReached} · ${entry.puzzlesCompleted} puzzles",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Text(
            text = entry.sessionScore.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary,
        )
    }
    HorizontalDivider()
}

@Composable
fun GameOverScreen(
    gameState: GameState,
    alias: String,
    onAliasChange: (String) -> Unit,
    submitMessage: String?,
    isSubmitting: Boolean,
    onSubmitScore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Resumen de partida", style = MaterialTheme.typography.headlineSmall)
        Text("Puntuación: ${gameState.sessionScore}", style = MaterialTheme.typography.titleLarge)
        Text("Tier alcanzado: ${gameState.currentTier}")
        Text("Puzzles completados: ${gameState.totalPuzzlesEverCompleted}")
        Text("Tiers superados: ${gameState.tiersCompleted}")
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        Text("Publicar en ranking", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = alias,
            onValueChange = onAliasChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Alias (3–16 caracteres)") },
            singleLine = true,
            enabled = !isSubmitting,
        )
        Button(
            onClick = onSubmitScore,
            enabled = !isSubmitting && alias.trim().length >= 3,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (isSubmitting) "Publicando…" else "Publicar puntuación")
        }
        submitMessage?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
    }
}
