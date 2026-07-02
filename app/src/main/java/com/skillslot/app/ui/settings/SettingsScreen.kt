package com.skillslot.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skillslot.app.ui.components.SkillSlotButton

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenPremium: () -> Unit,
    onRestorePurchases: () -> Unit,
    onOpenRanking: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val prefs by viewModel.preferences.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Ajustes", style = MaterialTheme.typography.headlineMedium)
        SettingToggle(
            label = "Sonido",
            checked = prefs.soundEnabled,
            onCheckedChange = viewModel::setSoundEnabled,
        )
        SettingToggle(
            label = "Vibración",
            checked = prefs.vibrationEnabled,
            onCheckedChange = viewModel::setVibrationEnabled,
        )
        Text(
            text = "Estadísticas locales: ${prefs.totalSpins} tiradas · ${prefs.totalPuzzlesCompleted} puzzles",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        SkillSlotButton(text = "Ver ranking", onClick = onOpenRanking)
        SkillSlotButton(text = "SkillSlot Premium", onClick = onOpenPremium)
        SkillSlotButton(text = "Restaurar compras", onClick = onRestorePurchases)
        SkillSlotButton(text = "Volver", onClick = onBack)
    }
}

@Composable
private fun SettingToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
