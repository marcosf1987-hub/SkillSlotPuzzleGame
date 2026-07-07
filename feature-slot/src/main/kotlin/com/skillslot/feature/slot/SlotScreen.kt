package com.skillslot.feature.slot

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skillslot.core.domain.SlotEngine
import com.skillslot.core.model.GameState
import com.skillslot.core.model.ProgressionConfig
import com.skillslot.core.model.SlotSpinResult
import com.skillslot.core.model.SlotSymbol
import kotlin.random.Random

const val SLOT_SPIN_ANIMATION_MS = 1_200L
private const val ACTIVE_PAYLINES = 8
private val COLUMN_STOP_DELAYS_MS = listOf(400L, 700L, 1_000L)

@Composable
fun SlotScreen(
    gameState: GameState,
    lastSpin: SlotSpinResult?,
    isSpinning: Boolean,
    showUnlockDialog: Boolean,
    returnMessage: String? = null,
    onDismissReturnMessage: () -> Unit = {},
    onSpin: () -> Unit,
    onPlayPuzzle: () -> Unit,
    onDismissUnlock: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val displayGrid = lastSpin?.grid ?: defaultGrid()
    val winningCells = lastSpin?.bestWinningLine?.cells?.toSet().orEmpty()
    var showResultOverlay by remember { mutableStateOf(false) }
    var wasSpinning by remember { mutableStateOf(false) }
    var autoSkillMode by remember { mutableStateOf(false) }

    LaunchedEffect(isSpinning, lastSpin) {
        if (wasSpinning && !isSpinning && lastSpin != null) {
            showResultOverlay = true
            kotlinx.coroutines.delay(2_500)
            showResultOverlay = false
        }
        wasSpinning = isSpinning
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            AnimatedVisibility(
                visible = returnMessage != null,
                enter = fadeIn(tween(300)),
                exit = fadeOut(tween(300)),
            ) {
                returnMessage?.let { message ->
                    LaunchedEffect(message) {
                        kotlinx.coroutines.delay(4_000)
                        onDismissReturnMessage()
                    }
                    BrandedBanner(text = message)
                }
            }

            MegaJackpotHeader(points = gameState.slotPoints)
            SessionStatusRow(gameState = gameState)
            PuzzleProgressStrip(gameState = gameState)

            SlotMachineCabinet(
                modifier = Modifier.weight(1f),
                grid = displayGrid,
                isSpinning = isSpinning,
                winningCells = if (isSpinning) emptySet() else winningCells,
                slotPoints = gameState.slotPoints,
                threshold = gameState.pointsThreshold,
            )

            SpinControls(
                enabled = gameState.isSessionActive && !isSpinning,
                autoSkillMode = autoSkillMode,
                onSpin = onSpin,
                onAutoSkillToggle = { autoSkillMode = !autoSkillMode },
            )

            CompactPaytable()
        }

        SpinResultOverlay(
            visible = showResultOverlay,
            spin = lastSpin,
        )
    }

    if (showUnlockDialog && gameState.puzzleUnlockAvailable) {
        AlertDialog(
            onDismissRequest = onDismissUnlock,
            title = { Text("¡Puzzle desbloqueado!") },
            text = {
                Text(
                    "Tienes ${gameState.slotPoints} pts. ¿Jugar ${gameState.currentPuzzleType?.displayName ?: "puzzle"}?",
                )
            },
            confirmButton = {
                TextButton(onClick = onPlayPuzzle) {
                    Text("Jugar puzzle")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissUnlock) {
                    Text("Seguir en slots")
                }
            },
        )
    }
}

@Composable
private fun BrandedBanner(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                shape = RoundedCornerShape(12.dp),
            )
            .padding(12.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun MegaJackpotHeader(points: Int) {
    val pulse = rememberInfiniteTransition(label = "jackpotHeader")
    val glowAlpha by pulse.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "jackpotGlow",
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .neonPurpleBorder()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(20.dp),
            )
            .padding(vertical = 16.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "MEGA JACKPOT",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary,
            letterSpacing = 3.sp,
        )
        Text(
            text = formatJackpotAmount(points),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.graphicsLayer { alpha = glowAlpha },
        )
    }
}

@Composable
private fun SessionStatusRow(gameState: GameState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        StatusChip(label = "TIER", value = gameState.currentTier.toString())
        StatusChip(
            label = "PUZZLE",
            value = "${gameState.completedPuzzlesInTier + 1}/10",
        )
        StatusChip(
            label = "VIDAS",
            value = "${gameState.lives}/${gameState.maxLives}",
            accent = MaterialTheme.colorScheme.tertiary,
        )
    }
}

@Composable
private fun StatusChip(
    label: String,
    value: String,
    accent: Color = MaterialTheme.colorScheme.primary,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = accent,
        )
    }
}

@Composable
private fun PuzzleProgressStrip(gameState: GameState) {
    val progress = (gameState.slotPoints.toFloat() / gameState.pointsThreshold.coerceAtLeast(1))
        .coerceIn(0f, 1f)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "HACIA EL PUZZLE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp,
            )
            Text(
                text = "${gameState.slotPoints} / ${gameState.pointsThreshold}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
    }
}

@Composable
private fun SlotMachineCabinet(
    grid: List<List<SlotSymbol>>,
    isSpinning: Boolean,
    winningCells: Set<Pair<Int, Int>>,
    slotPoints: Int,
    threshold: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                shape = RoundedCornerShape(20.dp),
            )
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CabinetTopBar()
        SlotReelsWithPayline(
            grid = grid,
            isSpinning = isSpinning,
            winningCells = winningCells,
        )
        BetInfoRow(
            slotPoints = slotPoints,
            threshold = threshold,
        )
    }
}

@Composable
private fun CabinetTopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)),
                )
            }
        }
        Text(
            text = "LINES: $ACTIVE_PAYLINES ACTIVE",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
private fun BetInfoRow(
    slotPoints: Int,
    threshold: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        BetPanel(
            modifier = Modifier.weight(1f),
            label = "PTS SESIÓN",
            value = slotPoints.toString(),
            valueColor = MaterialTheme.colorScheme.primary,
        )
        BetPanel(
            modifier = Modifier.weight(1f),
            label = "UMBRAL",
            value = threshold.toString(),
            valueColor = MaterialTheme.colorScheme.secondary,
        )
    }
}

@Composable
private fun BetPanel(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp),
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.5.sp,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = valueColor,
        )
    }
}

@Composable
private fun SlotReelsWithPayline(
    grid: List<List<SlotSymbol>>,
    isSpinning: Boolean,
    winningCells: Set<Pair<Int, Int>>,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                shape = RoundedCornerShape(14.dp),
            )
            .padding(10.dp),
    ) {
        SlotReels(
            grid = grid,
            isSpinning = isSpinning,
            winningCells = winningCells,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(2.dp)
                .align(Alignment.Center)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )
    }
}

@Composable
private fun SlotReels(
    grid: List<List<SlotSymbol>>,
    isSpinning: Boolean,
    winningCells: Set<Pair<Int, Int>>,
) {
    var stoppedColumns by remember { mutableStateOf(setOf(0, 1, 2)) }
    var shuffleTick by remember { mutableIntStateOf(0) }

    LaunchedEffect(isSpinning) {
        if (isSpinning) {
            stoppedColumns = emptySet()
            shuffleTick = 0
            COLUMN_STOP_DELAYS_MS.forEachIndexed { col, delayMs ->
                kotlinx.coroutines.delay(
                    if (col == 0) delayMs else delayMs - COLUMN_STOP_DELAYS_MS[col - 1],
                )
                stoppedColumns = stoppedColumns + col
            }
        } else {
            stoppedColumns = setOf(0, 1, 2)
        }
    }

    LaunchedEffect(isSpinning) {
        if (!isSpinning) return@LaunchedEffect
        while (true) {
            kotlinx.coroutines.delay(70)
            shuffleTick++
        }
    }

    val random = remember(shuffleTick) { Random(shuffleTick.toLong()) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        for (col in 0 until SlotEngine.GRID_SIZE) {
            ReelColumn(
                modifier = Modifier.weight(1f),
                columnIndex = col,
                grid = grid,
                isSpinning = isSpinning,
                stopped = col in stoppedColumns,
                winningCells = winningCells,
                random = random,
            )
        }
    }
}

@Composable
private fun ReelColumn(
    columnIndex: Int,
    grid: List<List<SlotSymbol>>,
    isSpinning: Boolean,
    stopped: Boolean,
    winningCells: Set<Pair<Int, Int>>,
    random: Random,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.85f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp),
            )
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        for (row in 0 until SlotEngine.GRID_SIZE) {
            val cell = row to columnIndex
            val symbol = if (isSpinning && !stopped) {
                SlotSymbol.entries.random(random)
            } else {
                grid[row][columnIndex]
            }
            ReelCell(
                symbol = symbol,
                isWinner = cell in winningCells,
                isSpinning = isSpinning && !stopped,
                isCenterRow = row == 1,
            )
        }
    }
}

@Composable
private fun ReelCell(
    symbol: SlotSymbol,
    isWinner: Boolean,
    isSpinning: Boolean,
    isCenterRow: Boolean,
) {
    val pulse = rememberInfiniteTransition(label = "winPulse")
    val glowAlpha by pulse.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glowAlpha",
    )
    val backgroundColor = when {
        isWinner -> MaterialTheme.colorScheme.primary.copy(alpha = glowAlpha)
        isCenterRow -> MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f)
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .then(
                if (isWinner) {
                    Modifier.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp),
                    )
                } else {
                    Modifier
                },
            )
            .padding(vertical = 14.dp)
            .graphicsLayer {
                if (isSpinning) {
                    alpha = 0.85f + (symbol.ordinal % 3) * 0.05f
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = symbol.display,
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal,
        )
    }
}

@Composable
private fun SpinControls(
    enabled: Boolean,
    autoSkillMode: Boolean,
    onSpin: () -> Unit,
    onAutoSkillToggle: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CircularSpinButton(enabled = enabled, onClick = onSpin)
        AutoSkillModePill(
            active = autoSkillMode,
            onClick = onAutoSkillToggle,
        )
    }
}

@Composable
private fun CircularSpinButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val pulse = rememberInfiniteTransition(label = "spinGlow")
    val scale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = if (enabled) 1.04f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "spinScale",
    )

    Box(
        modifier = Modifier
            .size(96.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .neonGoldGlow()
            .clip(CircleShape)
            .background(
                if (enabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                },
            )
            .border(
                width = 3.dp,
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape,
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (enabled) "SPIN" else "…",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onPrimary,
            letterSpacing = 2.sp,
        )
    }
}

@Composable
private fun AutoSkillModePill(
    active: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(999.dp)
    Box(
        modifier = Modifier
            .clip(shape)
            .background(
                if (active) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                } else {
                    Color.Transparent
                },
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = if (active) 0.8f else 0.45f),
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        Text(
            text = "AUTO-SKILL MODE",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
private fun CompactPaytable() {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp),
            )
            .clickable { expanded = !expanded }
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "TABLA DE PAGOS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                letterSpacing = 1.sp,
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
        }
        AnimatedVisibility(visible = expanded) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                SlotSymbol.entries.forEach { symbol ->
                    Text(
                        text = "${symbol.display} ×3 = ${symbol.triplePayout} pts",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = "Par en línea = ${ProgressionConfig.settings.pairPayout} pts",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun SpinResultOverlay(
    visible: Boolean,
    spin: SlotSpinResult?,
) {
    AnimatedVisibility(
        visible = visible && spin != null,
        enter = fadeIn(tween(250)) + scaleIn(initialScale = 0.85f, animationSpec = tween(300)),
        exit = fadeOut(tween(200)),
        modifier = Modifier.fillMaxSize(),
    ) {
        if (spin == null) return@AnimatedVisibility
        val isJackpot = spin.pointsAwarded >= 350
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.55f)),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .border(
                        width = 2.dp,
                        color = if (isJackpot) {
                            MaterialTheme.colorScheme.tertiary
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        shape = RoundedCornerShape(24.dp),
                    )
                    .padding(horizontal = 28.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = if (isJackpot) "¡JACKPOT!" else "¡GANASTE!",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 2.sp,
                )
                Text(
                    text = "+${spin.pointsAwarded}",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "PUNTOS",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                spin.winLabel?.let { label ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

private fun Modifier.neonGoldGlow(): Modifier = drawBehind {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0x66EEC065),
                Color.Transparent,
            ),
            radius = size.maxDimension * 0.75f,
        ),
    )
}

private fun Modifier.neonPurpleBorder(): Modifier = drawBehind {
    drawRoundRect(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0x339D05FF),
                Color.Transparent,
            ),
            radius = size.maxDimension * 0.9f,
        ),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(20.dp.toPx()),
    )
}

private fun formatJackpotAmount(points: Int): String {
    val grouped = points.toString()
        .reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
    return "$grouped.00"
}

private fun defaultGrid(): List<List<SlotSymbol>> =
    List(SlotEngine.GRID_SIZE) {
        List(SlotEngine.GRID_SIZE) { SlotSymbol.CHERRY }
    }
