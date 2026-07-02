package com.skillslot.core.domain

import com.skillslot.core.model.ProgressionConfig
import com.skillslot.core.model.SlotSpinResult
import com.skillslot.core.model.SlotSymbol
import kotlin.random.Random

class SlotEngine(
    private val random: Random = Random.Default,
) {
    fun spin(): SlotSpinResult {
        val grid = List(GRID_SIZE) {
            List(GRID_SIZE) { randomWeightedSymbol() }
        }
        val lineWins = evaluatePaylines(grid)
        val settings = ProgressionConfig.settings
        val points = when {
            lineWins.isNotEmpty() -> lineWins.maxOf { it.points }
            else -> random.nextInt(settings.consolationMin, settings.consolationMax + 1)
        }
        val label = lineWins.maxByOrNull { it.points }?.label
        return SlotSpinResult(grid = grid, pointsAwarded = points, winLabel = label)
    }

    private data class LineWin(val points: Int, val label: String)

    private fun evaluatePaylines(grid: List<List<SlotSymbol>>): List<LineWin> {
        val lines = buildList {
            for (row in 0 until GRID_SIZE) add((0 until GRID_SIZE).map { grid[row][it] })
            for (col in 0 until GRID_SIZE) add((0 until GRID_SIZE).map { grid[it][col] })
            add((0 until GRID_SIZE).map { grid[it][it] })
            add((0 until GRID_SIZE).map { grid[it][GRID_SIZE - 1 - it] })
        }
        return lines.mapNotNull { evaluateLine(it) }
    }

    private fun evaluateLine(symbols: List<SlotSymbol>): LineWin? {
        val first = symbols.first()
        if (symbols.all { it == first }) {
            return LineWin(first.triplePayout, "¡Trío ${first.display}!")
        }
        val counts = symbols.groupingBy { it }.eachCount()
        val bestPair = counts.filter { it.value >= 2 }.maxByOrNull { it.key.triplePayout / 3 }
        if (bestPair != null) {
            return LineWin(ProgressionConfig.settings.pairPayout, "Par ${bestPair.key.display}")
        }
        return null
    }

    private fun randomWeightedSymbol(): SlotSymbol {
        val total = SlotSymbol.entries.sumOf { it.weight }
        var roll = random.nextInt(total)
        for (symbol in SlotSymbol.entries) {
            roll -= symbol.weight
            if (roll < 0) return symbol
        }
        return SlotSymbol.CHERRY
    }

    companion object {
        const val GRID_SIZE = 3
    }
}
