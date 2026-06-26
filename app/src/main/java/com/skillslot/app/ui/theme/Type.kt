package com.skillslot.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val SkillSlotTypography = Typography(
    displayLarge = SkillSlotTextStyles.displayLg,
    displayMedium = SkillSlotTextStyles.displayMobile,
    headlineMedium = SkillSlotTextStyles.headlineMd,
    titleMedium = SkillSlotTextStyles.gameTile,
    bodyLarge = SkillSlotTextStyles.bodyLg,
    bodyMedium = SkillSlotTextStyles.bodySm,
    labelSmall = SkillSlotTextStyles.labelCaps,
    labelMedium = TextStyle(
        fontFamily = SpaceGroteskFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
    ),
)
