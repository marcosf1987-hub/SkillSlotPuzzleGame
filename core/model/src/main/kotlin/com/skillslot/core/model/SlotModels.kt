package com.skillslot.core.model

enum class SlotSymbol(val display: String, val weight: Int, val triplePayout: Int) {
    CHERRY("🍒", weight = 30, triplePayout = 150),
    LEMON("🍋", weight = 28, triplePayout = 150),
    BELL("🔔", weight = 20, triplePayout = 200),
    DIAMOND("💎", weight = 12, triplePayout = 350),
    BAR("BAR", weight = 8, triplePayout = 400),
    SEVEN("7", weight = 2, triplePayout = 1000),
}

data class SlotSpinResult(
    val grid: List<List<SlotSymbol>>,
    val pointsAwarded: Int,
    val winLabel: String?,
)
