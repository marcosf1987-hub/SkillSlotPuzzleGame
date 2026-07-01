package com.skillslot.app.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.skillslot.app.ui.theme.GlassBorderGold
import com.skillslot.app.ui.theme.Primary
import com.skillslot.app.ui.theme.PrimaryContainer
import com.skillslot.app.ui.theme.Secondary
import com.skillslot.app.ui.theme.SecondaryContainer
import com.skillslot.app.ui.theme.SkillSlotRadius
import com.skillslot.app.ui.theme.SkillSlotSpacing
import com.skillslot.app.ui.theme.SkillSlotTextStyles
import com.skillslot.app.ui.theme.SurfaceContainerHigh
import com.skillslot.app.ui.theme.SurfaceContainerHighest
import com.skillslot.app.ui.theme.SurfaceContainerLow
import com.skillslot.app.ui.theme.SurfaceContainerLowest
import com.skillslot.app.ui.theme.Tertiary
import com.skillslot.app.ui.theme.TertiaryContainer
import com.skillslot.app.ui.theme.glassPanel
import com.skillslot.app.ui.theme.neonGlowGold

@Composable
fun SkillSlotButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    filled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        enabled = enabled,
        shape = RoundedCornerShape(SkillSlotRadius.full),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (filled) Primary else Color.Transparent,
            contentColor = if (filled) MaterialTheme.colorScheme.onPrimary else Primary,
            disabledContainerColor = Primary.copy(alpha = 0.4f),
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
    ) {
        Text(text.uppercase(), style = SkillSlotTextStyles.labelCaps)
    }
}

@Composable
fun SkillSlotProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = Tertiary,
) {
    LinearProgressIndicator(
        progress = { progress.coerceIn(0f, 1f) },
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(SkillSlotRadius.full)),
        color = color,
        trackColor = SurfaceContainerHighest,
    )
}

@Composable
fun LifeIndicator(
    lives: Int,
    maxLives: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "♥".repeat(lives.coerceAtLeast(0)) +
            "♡".repeat((maxLives - lives).coerceAtLeast(0)),
        modifier = modifier,
        color = Primary,
        style = SkillSlotTextStyles.gameTile,
    )
}

@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    borderColor: Color = GlassBorderGold,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.glassPanel(borderColor = borderColor),
        color = Color.Transparent,
        shape = RoundedCornerShape(SkillSlotRadius.xl),
    ) {
        content()
    }
}

@Composable
fun CreditsChip(
    credits: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(SkillSlotRadius.full))
            .background(SurfaceContainerHigh)
            .border(1.dp, Primary.copy(alpha = 0.3f), RoundedCornerShape(SkillSlotRadius.full))
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "%,d CR".format(credits),
            style = SkillSlotTextStyles.credits,
            color = Primary,
        )
    }
}

@Composable
fun DailyJackpotBanner(
    amount: String,
    onSpinClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "jackpot")
    val glowAlpha by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(192.dp)
            .neonGlowGold()
            .clip(RoundedCornerShape(SkillSlotRadius.xxl))
            .background(
                Brush.linearGradient(
                    colors = listOf(PrimaryContainer, SurfaceContainerLowest),
                ),
            )
            .border(2.dp, Primary.copy(alpha = 0.5f), RoundedCornerShape(SkillSlotRadius.xxl))
            .clickable(onClick = onSpinClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SkillSlotSpacing.stackGap),
        ) {
            Text(
                text = "DAILY MEGA JACKPOT",
                style = SkillSlotTextStyles.labelCaps,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = amount,
                style = SkillSlotTextStyles.displayMobile,
                color = Primary,
                modifier = Modifier.graphicsLayer {
                    alpha = glowAlpha
                },
            )
            Text(
                text = "COINS",
                style = SkillSlotTextStyles.headlineMdItalic,
                color = Primary.copy(alpha = 0.9f),
            )
            SkillSlotButton(
                text = "Spin to win",
                onClick = onSpinClick,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
fun FeaturedGameCard(
    title: String,
    subtitle: String,
    badge: String,
    badgeContainerColor: Color,
    badgeContentColor: Color,
    imageUrl: String?,
    glowModifier: Modifier,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .then(glowModifier)
            .clip(RoundedCornerShape(SkillSlotRadius.xxl))
            .border(1.dp, badgeContainerColor.copy(alpha = 0.35f), RoundedCornerShape(SkillSlotRadius.xxl))
            .background(SurfaceContainerLow)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer { alpha = 0.6f },
                contentScale = ContentScale.Crop,
            )
        } else {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.linearGradient(
                            listOf(SecondaryContainer.copy(0.35f), Color.Transparent),
                        ),
                    ),
            )
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
                    ),
                ),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Surface(
                color = badgeContainerColor,
                shape = RoundedCornerShape(SkillSlotRadius.sm),
            ) {
                Text(
                    text = badge.uppercase(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = SkillSlotTextStyles.labelCaps,
                    color = badgeContentColor,
                )
            }
            Text(text = title, style = SkillSlotTextStyles.headlineMd, color = MaterialTheme.colorScheme.onSurface)
            Text(
                text = subtitle,
                style = SkillSlotTextStyles.bodySm,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun OtherTableTile(
    label: String,
    icon: @Composable () -> Unit,
    tint: Color,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val alpha = if (enabled) 1f else 0.5f
    Column(
        modifier = modifier
            .size(width = 140.dp, height = 186.dp)
            .graphicsLayer { this.alpha = alpha }
            .glassPanel(borderColor = tint.copy(alpha = 0.2f))
            .clip(RoundedCornerShape(SkillSlotRadius.xl))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(modifier = Modifier.padding(bottom = 8.dp)) {
            icon()
        }
        Text(
            text = label.uppercase(),
            style = SkillSlotTextStyles.labelCaps,
            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun VipStatusCard(
    rankLabel: String,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    GlassPanel(
        modifier = modifier.fillMaxWidth(),
        borderColor = Tertiary.copy(alpha = 0.2f),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(72.dp)
                    .background(Tertiary),
            )
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "CURRENT RANK",
                        style = SkillSlotTextStyles.labelCaps,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = rankLabel,
                        style = SkillSlotTextStyles.headlineMdItalic,
                        color = Tertiary,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "NEXT LEVEL",
                        style = SkillSlotTextStyles.labelCaps,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    SkillSlotProgressBar(
                        progress = progress,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .fillMaxWidth(0.45f),
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileAvatar(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    icon: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                },
            )
            .clip(CircleShape)
            .background(TertiaryContainer.copy(alpha = 0.2f))
            .border(2.dp, Tertiary, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        icon()
    }
}

@Composable
fun BottomNavItem(
    label: String,
    selected: Boolean,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = if (selected) Primary else MaterialTheme.colorScheme.onSurfaceVariant
    val scale = if (selected) 1.1f else 1f
    Column(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            icon()
        }
        Text(
            text = label.uppercase(),
            style = SkillSlotTextStyles.labelCaps,
            color = color,
        )
    }
}

@Composable
fun SkillSlotBottomBar(
    selectedTab: LobbyTab,
    onTabSelected: (LobbyTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                spotColor = SecondaryContainer.copy(alpha = 0.2f),
            ),
        color = SurfaceContainerLowest.copy(alpha = 0.95f),
        shape = RoundedCornerShape(topStart = SkillSlotRadius.xl, topEnd = SkillSlotRadius.xl),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Secondary.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(topStart = SkillSlotRadius.xl, topEnd = SkillSlotRadius.xl),
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            LobbyTab.entries.forEach { tab ->
                BottomNavItem(
                    label = tab.label,
                    selected = tab == selectedTab,
                    onClick = { onTabSelected(tab) },
                    icon = { tab.Icon(tint = if (tab == selectedTab) Primary else MaterialTheme.colorScheme.onSurfaceVariant) },
                )
            }
        }
    }
}

enum class LobbyTab(val label: String) {
    LOBBY("Lobby"),
    WORD_SLOTS("Word Slots"),
    SUDOKU("Sudoku"),
    VAULT("Vault"),
    ;

    @Composable
    fun Icon(tint: Color) {
        val imageVector = when (this) {
            LOBBY -> Icons.Default.Casino
            WORD_SLOTS -> Icons.Default.Abc
            SUDOKU -> Icons.Default.GridOn
            VAULT -> Icons.Default.MilitaryTech
        }
        Icon(imageVector = imageVector, contentDescription = label, tint = tint)
    }
}
