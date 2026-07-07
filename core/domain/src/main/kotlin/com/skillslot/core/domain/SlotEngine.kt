package com.skillslot.core.domain

import com.skillslot.core.model.ProgressionConfig
import com.skillslot.core.model.SlotSpinResult
import com.skillslot.core.model.SlotSymbol
import com.skillslot.core.model.SlotWinningLine
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
        val bestWin = lineWins.maxByOrNull { it.points }
        return SlotSpinResult(
            grid = grid,
            pointsAwarded = points,
            winLabel = bestWin?.label,
            bestWinningLine = bestWin?.toWinningLine(),
        )
    }

    private data class LineWin(
        val points: Int,
        val label: String,
        val cells: List<Pair<Int, Int>>,
    ) {
        fun toWinningLine() = SlotWinningLine(cells = cells, label = label, points = points)
    }

    private fun evaluatePaylines(grid: List<List<SlotSymbol>>): List<LineWin> {
        val paylines = buildList {
            for (row in 0 until GRID_SIZE) {
                val cells = (0 until GRID_SIZE).map { row to it }
                add((0 until GRID_SIZE).map { grid[row][it] } to cells)
            }
            for (col in 0 until GRID_SIZE) {
                val cells = (0 until GRID_SIZE).map { it to col }
                add((0 until GRID_SIZE).map { grid[it][col] } to cells)
            }
            add(
                (0 until GRID_SIZE).map { grid[it][it] } to
                    (0 until GRID_SIZE).map { it to it },
            )
            add(
                (0 until GRID_SIZE).map { grid[it][GRID_SIZE - 1 - it] } to
                    (0 until GRID_SIZE).map { it to GRID_SIZE - 1 - it },
            )
        }
        return paylines.mapNotNull { (symbols, cells) -> evaluateLine(symbols, cells) }
    }

    private fun evaluateLine(
        symbols: List<SlotSymbol>,
        cells: List<Pair<Int, Int>>,
    ): LineWin? {
        val first = symbols.first()
        if (symbols.all { it == first }) {
            return LineWin(first.triplePayout, "¡Trío ${first.display}!", cells)
        }
        val counts = symbols.groupingBy { it }.eachCount()
        val bestPair = counts.filter { it.value >= 2 }.maxByOrNull { it.key.triplePayout / 3 }
        if (bestPair != null) {
            return LineWin(
                ProgressionConfig.settings.pairPayout,
                "Par ${bestPair.key.display}",
                cells,
            )
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
