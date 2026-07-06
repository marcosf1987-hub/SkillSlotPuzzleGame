package com.skillslot.app.ui.theme

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.glassPanel(
    borderColor: Color = GlassBorderGold,
    cornerRadius: Dp = SkillSlotRadius.xl,
): Modifier = composed {
    val brand = SkillSlotTheme.brandColors
    this
        .drawBehind {
            drawRoundRect(
                color = brand.glassFill,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx()),
            )
        }
        .border(
            width = 1.dp,
            color = borderColor,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(cornerRadius),
        )
}

fun Modifier.neonGlowGold(
    cornerRadius: Dp = SkillSlotRadius.xxl,
): Modifier = composed {
    drawBehind {
        val radius = cornerRadius.toPx()
        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(NeonGoldGlow, Color.Transparent),
                radius = size.maxDimension * 0.6f,
            ),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius),
        )
        drawRoundRect(
            color = Primary.copy(alpha = 0.35f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius),
            style = Stroke(width = 2.dp.toPx()),
        )
    }
}

fun Modifier.neonGlowPurple(
    cornerRadius: Dp = SkillSlotRadius.xxl,
): Modifier = composed {
    drawBehind {
        val radius = cornerRadius.toPx()
        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(NeonPurpleGlow, Color.Transparent),
                radius = size.maxDimension * 0.55f,
            ),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius),
        )
        drawRoundRect(
            color = SecondaryContainer.copy(alpha = 0.35f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius),
            style = Stroke(width = 1.5.dp.toPx()),
        )
    }
}

@Composable
fun Modifier.jackpotPulse(): Modifier {
    val transition = rememberInfiniteTransition(label = "jackpotPulse")
    val alpha by transition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "jackpotAlpha",
    )
    return this.graphicsLayer { this.alpha = alpha }
}
