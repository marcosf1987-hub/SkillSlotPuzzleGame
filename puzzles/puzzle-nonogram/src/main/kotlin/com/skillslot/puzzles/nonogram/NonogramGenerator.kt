package com.skillslot.puzzles.nonogram

import kotlin.random.Random

internal class NonogramGenerator {
    data class Generated(
        val solution: List<List<Boolean>>,
        val rowClues: List<List<Int>>,
        val colClues: List<List<Int>>,
    )

    fun generate(difficulty: Int, seed: Long): Generated {
        val random = Random(seed)
        val size = when {
            difficulty <= 3 -> 5
            difficulty <= 6 -> 7
            else -> 8
        }
        val solution = Array(size) { row ->
            BooleanArray(size) { col ->
                random.nextFloat() < (0.35f + difficulty * 0.02f)
            }
        }
        for (row in 0 until size) {
            if (solution[row].none { it }) solution[row][random.nextInt(size)] = true
        }
        for (col in 0 until size) {
            if ((0 until size).none { solution[it][col] }) {
                solution[random.nextInt(size)][col] = true
            }
        }
        val grid = solution.map { it.toList() }
        return Generated(
            solution = grid,
            rowClues = grid.map { rowCluesFor(it) },
            colClues = colCluesFor(grid),
        )
    }

    private fun rowCluesFor(row: List<Boolean>): List<Int> {
        val clues = mutableListOf<Int>()
        var run = 0
        for (filled in row) {
            if (filled) {
                run++
            } else if (run > 0) {
                clues += run
                run = 0
            }
        }
        if (run > 0) clues += run
        return clues.ifEmpty { listOf(0) }
    }

    private fun colCluesFor(grid: List<List<Boolean>>): List<List<Int>> {
        val size = grid.size
        return List(size) { col ->
            rowCluesFor(List(size) { row -> grid[row][col] })
        }
    }
}

internal fun isNonogramSolved(solution: List<List<Boolean>>, filled: List<List<Boolean>>): Boolean {
    for (row in solution.indices) {
        for (col in solution[row].indices) {
            if (solution[row][col] != filled[row][col]) return false
        }
    }
    return true
}
