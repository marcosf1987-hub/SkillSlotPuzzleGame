package com.skillslot.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skillslot.app.ui.components.SkillSlotButton

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenPremium: () -> Unit,
    onRestorePurchases: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Ajustes", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "Sonido, vibración y cuenta.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        SkillSlotButton(text = "SkillSlot Premium", onClick = onOpenPremium)
        SkillSlotButton(text = "Restaurar compras", onClick = onRestorePurchases)
        SkillSlotButton(text = "Volver", onClick = onBack)
    }
}
