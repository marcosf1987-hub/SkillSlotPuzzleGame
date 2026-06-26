package com.skillslot.app.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Estilos tipográficos del manual de marca (además de Material Typography).
 */
object SkillSlotTextStyles {
    val displayMobile = TextStyle(
        fontFamily = AnybodyFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 36.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.02).sp,
    )
    val displayLg = TextStyle(
        fontFamily = AnybodyFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 48.sp,
        lineHeight = 52.sp,
        letterSpacing = (-0.02).sp,
    )
    val headlineMd = TextStyle(
        fontFamily = AnybodyFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    )
    val headlineMdItalic = TextStyle(
        fontFamily = AnybodyFamily,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    )
    val bodyLg = TextStyle(
        fontFamily = HankenGroteskFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 28.sp,
    )
    val bodySm = TextStyle(
        fontFamily = HankenGroteskFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    )
    val labelCaps = TextStyle(
        fontFamily = SpaceGroteskFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.08.sp,
    )
    val gameTile = TextStyle(
        fontFamily = SpaceGroteskFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 24.sp,
    )
    val credits = TextStyle(
        fontFamily = AnybodyFamily,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        fontSize = 18.sp,
    )
    val brandTitle = TextStyle(
        fontFamily = AnybodyFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 24.sp,
        letterSpacing = (-0.02).sp,
    )
}
