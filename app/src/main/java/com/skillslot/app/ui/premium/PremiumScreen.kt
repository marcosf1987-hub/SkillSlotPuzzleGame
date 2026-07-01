package com.skillslot.app.ui.premium

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skillslot.app.ui.components.SkillSlotButton

@Composable
fun PremiumScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PremiumViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "SkillSlot Premium",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "Juega sin interrupciones y continúa donde lo dejaste.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("✓ Sin anuncios (banner e intersticiales)", style = MaterialTheme.typography.bodyMedium)
            Text("✓ Guardar y reanudar progreso", style = MaterialTheme.typography.bodyMedium)
            Text("✓ Ranking global disponible", style = MaterialTheme.typography.bodyMedium)
        }

        if (uiState.isPremium) {
            Text(
                text = "Ya tienes Premium activo.",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary,
            )
        } else {
            SkillSlotButton(
                text = if (uiState.isLoading) "Procesando…" else "Comprar Premium",
                onClick = viewModel::purchase,
                enabled = !uiState.isLoading,
            )
        }

        SkillSlotButton(
            text = "Restaurar compras",
            onClick = viewModel::restore,
            enabled = !uiState.isLoading,
        )

        uiState.message?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        SkillSlotButton(text = "Volver", onClick = onBack)
    }
}
