package com.skillslot.feature.puzzle

internal sealed interface PuzzleShellPhase {
    data object Playing : PuzzleShellPhase
    data object Paused : PuzzleShellPhase
    data object Victory : PuzzleShellPhase
    data class Defeat(val reason: String) : PuzzleShellPhase
}

internal fun formatTimer(seconds: Int): String {
    val safe = seconds.coerceAtLeast(0)
    val minutes = safe / 60
    val secs = safe % 60
    return "%d:%02d".format(minutes, secs)
}
