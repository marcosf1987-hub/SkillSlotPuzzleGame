package com.skillslot.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.skillslot.app.R

private val fontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

private val anybodyFont = GoogleFont("Anybody")
private val hankenGroteskFont = GoogleFont("Hanken Grotesk")
private val spaceGroteskFont = GoogleFont("Space Grotesk")

val AnybodyFamily: FontFamily = FontFamily(
    Font(googleFont = anybodyFont, fontProvider = fontProvider, weight = FontWeight.Normal),
    Font(googleFont = anybodyFont, fontProvider = fontProvider, weight = FontWeight.Bold),
    Font(googleFont = anybodyFont, fontProvider = fontProvider, weight = FontWeight.ExtraBold),
)

val HankenGroteskFamily: FontFamily = FontFamily(
    Font(googleFont = hankenGroteskFont, fontProvider = fontProvider, weight = FontWeight.Normal),
    Font(googleFont = hankenGroteskFont, fontProvider = fontProvider, weight = FontWeight.Bold),
)

val SpaceGroteskFamily: FontFamily = FontFamily(
    Font(googleFont = spaceGroteskFont, fontProvider = fontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = spaceGroteskFont, fontProvider = fontProvider, weight = FontWeight.Bold),
)

@Composable
fun rememberSkillSlotFontFamilies(): SkillSlotFontFamilies = SkillSlotFontFamilies(
    anybody = AnybodyFamily,
    hankenGrotesk = HankenGroteskFamily,
    spaceGrotesk = SpaceGroteskFamily,
)

data class SkillSlotFontFamilies(
    val anybody: FontFamily,
    val hankenGrotesk: FontFamily,
    val spaceGrotesk: FontFamily,
)
