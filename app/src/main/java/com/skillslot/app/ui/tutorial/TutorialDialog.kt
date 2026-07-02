package com.skillslot.app.ui.tutorial

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun TutorialDialog(
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¡Bienvenido a SkillSlot!") },
        text = {
            Text(
                "1. Gira la tragamonedas para acumular puntos.\n" +
                    "2. Alcanza el umbral para desbloquear un puzzle.\n" +
                    "3. Completa 10 puzzles para subir de tier.\n" +
                    "4. Premium guarda tu progreso y quita anuncios.",
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("¡A jugar!")
            }
        },
    )
}
