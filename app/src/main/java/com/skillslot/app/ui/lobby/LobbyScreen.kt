package com.skillslot.app.ui.lobby

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.skillslot.app.ui.components.CreditsChip
import com.skillslot.app.ui.components.DailyJackpotBanner
import com.skillslot.app.ui.components.FeaturedGameCard
import com.skillslot.app.ui.components.LobbyTab
import com.skillslot.app.ui.components.OtherTableTile
import com.skillslot.app.ui.components.ProfileAvatar
import com.skillslot.app.ui.components.SkillSlotBottomBar
import com.skillslot.app.ui.components.VipStatusCard
import com.skillslot.app.ui.theme.Background
import com.skillslot.app.ui.theme.OnPrimary
import com.skillslot.app.ui.theme.OnSecondary
import com.skillslot.app.ui.theme.Primary
import com.skillslot.app.ui.theme.Secondary
import com.skillslot.app.ui.theme.SkillSlotSpacing
import com.skillslot.app.ui.theme.SkillSlotTextStyles
import com.skillslot.app.ui.theme.neonGlowGold
import com.skillslot.app.ui.theme.neonGlowPurple
import com.skillslot.core.model.ProgressionConfig
import com.skillslot.core.model.GameState

private const val WORD_SLOTS_IMAGE =
    "https://lh3.googleusercontent.com/aida-public/AB6AXuAAeVSGDEKFG6MeEgXQUxMYzFRoGDILSbz0Jq_gY9Jyomj3dSas_oHBgrbQV5DXvBkYVWTqXgykW9_XBiveVabJHiezo4gIZqumSdtwsyK3DjgIxCEENazsxCiQ0IGRvop1W0jwRng0eStaKs5au7BWBMoJtkVw-wf7dIl_kdbSkWGKWrwTXO7RjQkRcLrqY8Ko7N3-B_FZY7YVL0l2uYvodaQG6zFVWNDB30SsnVnS4VWioqPVLeVBL9HvjZsDN_PQaoQ2BPGdtzGq"

private const val SUDOKU_SPIN_IMAGE =
    "https://lh3.googleusercontent.com/aida-public/AB6AXuBRTtgDdBszIelVxtQqXeso2ZfX4q4-ZOdPeVBWA1xhNiBWVWNYKa4OAulYn9wqx-lEV0m0UBOjit14SDLkXO7lKshHMmDHoD5_eaU4iqURd1brmRXBv5llcF0uv4mjH8hAooNP-9j8PmBLM88pFUksKyLIfJLn6Q7b5HWVLWd9oDryvBzJO3jDE5ZMNdelY9TIG6LBCv0ktooxVU2NDw9IZQooPECAwdHpCcg2AcrU0dYIsoNv0SOGmsCEOG8bR_1RhMB8lzJuA7OB"

@Composable
fun LobbyScreen(
    gameState: GameState,
    selectedTab: LobbyTab,
    onTabSelected: (LobbyTab) -> Unit,
    onSpinClick: () -> Unit,
    onWordSlotsClick: () -> Unit,
    onSudokuClick: () -> Unit,
    onVaultClick: () -> Unit,
    onLobbyTablesClick: () -> Unit,
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Background,
        topBar = {
            LobbyTopBar(
                credits = gameState.slotPoints,
                onSettingsClick = onSettingsClick,
            )
        },
        bottomBar = {
            SkillSlotBottomBar(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = SkillSlotSpacing.containerPadding),
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            VipStatusCard(
                rankLabel = tierRankLabel(gameState.currentTier),
                progress = gameState.completedPuzzlesInTier / ProgressionConfig.PUZZLES_PER_TIER.toFloat(),
            )
            Spacer(modifier = Modifier.height(SkillSlotSpacing.stackGap * 2))
            DailyJackpotBanner(
                amount = "1,000,000",
                onSpinClick = onSpinClick,
            )
            Spacer(modifier = Modifier.height(24.dp))
            FeaturedFloorSection(
                onWordSlotsClick = onWordSlotsClick,
                onSudokuClick = onSudokuClick,
                onLobbyTablesClick = onLobbyTablesClick,
                onVaultClick = onVaultClick,
            )
            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}

@Composable
private fun LobbyTopBar(
    credits: Int,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Background.copy(alpha = 0.9f))
            .border(
                width = 1.dp,
                color = Primary.copy(alpha = 0.2f),
            )
            .statusBarsPadding()
            .height(64.dp)
            .padding(horizontal = SkillSlotSpacing.containerPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = Icons.Default.AccountBalanceWallet,
                contentDescription = null,
                tint = Primary,
            )
            Text(
                text = "JACKPOT PUZZLES",
                style = SkillSlotTextStyles.brandTitle,
                color = Primary,
                textAlign = TextAlign.Start,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CreditsChip(credits = credits)
            ProfileAvatar(onClick = onSettingsClick) {
                Icon(Icons.Default.Person, contentDescription = "Ajustes", tint = com.skillslot.app.ui.theme.Tertiary)
            }
        }
    }
}

@Composable
private fun FeaturedFloorSection(
    onWordSlotsClick: () -> Unit,
    onSudokuClick: () -> Unit,
    onLobbyTablesClick: () -> Unit,
    onVaultClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(SkillSlotSpacing.gridGutter * 2)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Primary)
            Text(
                text = "Featured Floor",
                style = SkillSlotTextStyles.headlineMd,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        FeaturedGameCard(
            title = "Word Slots",
            subtitle = "Word search meets tragamonedas.",
            badge = "New",
            badgeContainerColor = Secondary,
            badgeContentColor = OnSecondary,
            imageUrl = WORD_SLOTS_IMAGE,
            glowModifier = Modifier.neonGlowPurple(),
            onClick = onWordSlotsClick,
        )
        FeaturedGameCard(
            title = "Sudoku Spin",
            subtitle = "Fast-paced number crunching win.",
            badge = "Popular",
            badgeContainerColor = Primary,
            badgeContentColor = OnPrimary,
            imageUrl = SUDOKU_SPIN_IMAGE,
            glowModifier = Modifier.neonGlowGold(),
            onClick = onSudokuClick,
        )
        OtherTablesSection(
            onLobbyTablesClick = onLobbyTablesClick,
            onVaultClick = onVaultClick,
        )
    }
}

@Composable
private fun OtherTablesSection(
    onLobbyTablesClick: () -> Unit,
    onVaultClick: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "OTHER TABLES",
                style = SkillSlotTextStyles.labelCaps,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "View All",
                style = SkillSlotTextStyles.bodySm,
                color = com.skillslot.app.ui.theme.Tertiary,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            OtherTableTile(
                label = "Lobby",
                tint = Secondary,
                onClick = onLobbyTablesClick,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Casino,
                        contentDescription = null,
                        tint = Secondary,
                        modifier = Modifier.height(32.dp),
                    )
                },
            )
            OtherTableTile(
                label = "The Vault",
                tint = Primary,
                onClick = onVaultClick,
                icon = {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.height(32.dp),
                    )
                },
            )
            OtherTableTile(
                label = "Blackjack",
                tint = Color.White.copy(alpha = 0.05f),
                enabled = false,
                onClick = {},
                icon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.height(32.dp),
                    )
                },
            )
        }
    }
}

private fun tierRankLabel(tier: Int): String = when {
    tier >= 9 -> "DIAMOND VIP"
    tier >= 6 -> "GOLD VIP"
    tier >= 3 -> "SILVER VIP"
    else -> "BRONZE PLAYER"
}
