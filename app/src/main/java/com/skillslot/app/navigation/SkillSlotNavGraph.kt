package com.skillslot.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.skillslot.app.bootstrap.BootstrapViewModel
import com.skillslot.app.session.GameSessionEvent
import com.skillslot.app.session.GameSessionViewModel
import com.skillslot.app.ui.components.LobbyTab
import com.skillslot.app.ui.components.SkillSlotBottomBar
import com.skillslot.app.ui.components.SkillSlotButton
import com.skillslot.app.ui.lobby.LobbyScreen
import com.skillslot.app.ui.theme.Background
import com.skillslot.app.ui.theme.Primary
import com.skillslot.app.ui.theme.Secondary
import com.skillslot.app.ui.theme.SkillSlotTextStyles
import com.skillslot.feature.leaderboard.GameOverScreen
import com.skillslot.feature.leaderboard.LeaderboardScreen
import com.skillslot.feature.progression.ProgressionMapScreen
import com.skillslot.feature.puzzle.PuzzleShellScreen
import com.skillslot.feature.slot.SlotScreen

@Composable
fun SkillSlotNavHost(
    navController: NavHostController = rememberNavController(),
) {
    val sessionViewModel: GameSessionViewModel = hiltViewModel()

    LaunchedEffect(sessionViewModel) {
        sessionViewModel.events.collect { event ->
            when (event) {
                GameSessionEvent.NavigateToPuzzle ->
                    navController.navigate(SkillSlotRoutes.PUZZLE) {
                        popUpTo(SkillSlotRoutes.LOBBY) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                GameSessionEvent.NavigateToSlots ->
                    navController.navigate(SkillSlotRoutes.SLOT) {
                        popUpTo(SkillSlotRoutes.LOBBY) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                GameSessionEvent.NavigateToGameOver ->
                    navController.navigate(SkillSlotRoutes.GAME_OVER) {
                        launchSingleTop = true
                    }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = SkillSlotRoutes.SPLASH,
    ) {
        composable(SkillSlotRoutes.SPLASH) {
            SplashRoute(onReady = {
                navController.navigate(SkillSlotRoutes.LOBBY) {
                    popUpTo(SkillSlotRoutes.SPLASH) { inclusive = true }
                }
            })
        }
        composable(SkillSlotRoutes.LOBBY) {
            val gameState by sessionViewModel.gameState.collectAsState()
            LobbyScreen(
                gameState = gameState,
                selectedTab = LobbyTab.LOBBY,
                onTabSelected = { navigateToTab(navController, it) },
                onSpinClick = { navController.navigate(SkillSlotRoutes.SLOT) },
                onWordSlotsClick = { navController.navigate(SkillSlotRoutes.SLOT) },
                onSudokuClick = {
                    sessionViewModel.tryOpenPuzzleSession()
                    navController.navigate(SkillSlotRoutes.PUZZLE)
                },
                onVaultClick = { navController.navigate(SkillSlotRoutes.PROGRESSION) },
                onLobbyTablesClick = { },
            )
        }
        composable(SkillSlotRoutes.SLOT) {
            val gameState by sessionViewModel.gameState.collectAsState()
            val lastSpin by sessionViewModel.lastSpin.collectAsState()
            val isSpinning by sessionViewModel.isSpinning.collectAsState()
            val showUnlock by sessionViewModel.showUnlockDialog.collectAsState()
            val returnMessage by sessionViewModel.slotReturnMessage.collectAsState()
            TabbedShell(
                selectedTab = LobbyTab.WORD_SLOTS,
                onTabSelected = { navigateToTab(navController, it) },
            ) {
                SlotScreen(
                    gameState = gameState,
                    lastSpin = lastSpin,
                    isSpinning = isSpinning,
                    showUnlockDialog = showUnlock,
                    returnMessage = returnMessage,
                    onDismissReturnMessage = sessionViewModel::clearSlotReturnMessage,
                    onSpin = sessionViewModel::spin,
                    onPlayPuzzle = sessionViewModel::acceptPuzzle,
                    onDismissUnlock = sessionViewModel::dismissUnlockDialog,
                )
            }
        }
        composable(SkillSlotRoutes.PUZZLE) {
            val gameState by sessionViewModel.gameState.collectAsState()
            val puzzleSession by sessionViewModel.puzzleSession.collectAsState()
            LaunchedEffect(Unit) {
                sessionViewModel.tryOpenPuzzleSession()
            }
            TabbedShell(
                selectedTab = LobbyTab.SUDOKU,
                onTabSelected = { navigateToTab(navController, it) },
            ) {
                PuzzleShellScreen(
                    gameState = gameState,
                    puzzleSession = puzzleSession,
                    puzzleType = gameState.currentPuzzleType,
                    onPuzzleCompleted = sessionViewModel::onPuzzleCompleted,
                    onPuzzleFailed = sessionViewModel::onPuzzleFailed,
                )
            }
        }
        composable(SkillSlotRoutes.PROGRESSION) {
            val gameState by sessionViewModel.gameState.collectAsState()
            TabbedShell(
                selectedTab = LobbyTab.VAULT,
                onTabSelected = { navigateToTab(navController, it) },
            ) {
                ProgressionMapScreen(gameState = gameState)
            }
        }
        composable(SkillSlotRoutes.LEADERBOARD) {
            ShellRoute(
                title = "Ranking",
                onBack = { navController.popBackStack() },
            ) {
                LeaderboardScreen()
            }
        }
        composable(SkillSlotRoutes.GAME_OVER) {
            val gameState by sessionViewModel.gameState.collectAsState()
            ShellRoute(
                title = "Game Over",
                onBack = { navController.popBackStack() },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    GameOverScreen(gameState = gameState)
                    SkillSlotButton(
                        text = "Nueva partida",
                        onClick = {
                            sessionViewModel.startNewSession()
                            navController.navigate(SkillSlotRoutes.LOBBY) {
                                popUpTo(SkillSlotRoutes.LOBBY) { inclusive = true }
                            }
                        },
                    )
                }
            }
        }
        composable(SkillSlotRoutes.SETTINGS) {
            ShellRoute(
                title = "Ajustes",
                onBack = { navController.popBackStack() },
            ) {
                PlaceholderScreen(
                    title = "Ajustes",
                    subtitle = "Sonido, alias, restaurar compras (Fase 3)",
                )
            }
        }
        composable(SkillSlotRoutes.PREMIUM) {
            ShellRoute(
                title = "Premium",
                onBack = { navController.popBackStack() },
            ) {
                PlaceholderScreen(
                    title = "SkillSlot Premium",
                    subtitle = "Sin anuncios + guardar progreso (Fase 3)",
                )
            }
        }
    }
}

private fun navigateToTab(navController: NavHostController, tab: LobbyTab) {
    val route = when (tab) {
        LobbyTab.LOBBY -> SkillSlotRoutes.LOBBY
        LobbyTab.WORD_SLOTS -> SkillSlotRoutes.SLOT
        LobbyTab.SUDOKU -> SkillSlotRoutes.PUZZLE
        LobbyTab.VAULT -> SkillSlotRoutes.PROGRESSION
    }
    navController.navigate(route) {
        popUpTo(SkillSlotRoutes.LOBBY) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun TabbedShell(
    selectedTab: LobbyTab,
    onTabSelected: (LobbyTab) -> Unit,
    content: @Composable () -> Unit,
) {
    Scaffold(
        containerColor = Background,
        bottomBar = {
            SkillSlotBottomBar(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected,
            )
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            content()
        }
    }
}

@Composable
private fun SplashRoute(onReady: () -> Unit) {
    val viewModel: BootstrapViewModel = hiltViewModel()
    val ready by viewModel.ready.collectAsState()
    if (ready) {
        onReady()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "JACKPOT PUZZLES",
                style = SkillSlotTextStyles.brandTitle,
                color = Primary,
            )
            CircularProgressIndicator(color = Secondary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShellRoute(
    title: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit,
) {
    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text(title, style = SkillSlotTextStyles.headlineMd) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            content()
        }
    }
}

@Composable
private fun PlaceholderScreen(
    title: String,
    subtitle: String,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(title, style = SkillSlotTextStyles.headlineMd)
        Text(
            subtitle,
            style = SkillSlotTextStyles.bodySm,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
