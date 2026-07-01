package com.skillslot.puzzles.sudoku

import kotlin.random.Random

internal class SudokuGenerator {
    data class Generated(
        val puzzle: List<List<Int>>,
        val solution: List<List<Int>>,
    )

    fun generate(difficulty: Int, seed: Long): Generated {
        val random = Random(seed)
        val solved = Array(9) { IntArray(9) }
        fillDiagonalBoxes(solved, random)
        solve(solved)
        val solution = solved.map { row -> row.toList() }
        val puzzle = solution.map { it.toMutableList() }.toMutableList()
        val cellsToRemove = (22 + difficulty.coerceIn(1, 10) * 3).coerceIn(30, 58)
        val positions = (0 until 81).shuffled(random).take(cellsToRemove)
        for (pos in positions) {
            puzzle[pos / 9][pos % 9] = 0
        }
        return Generated(puzzle, solution)
    }

    private fun fillDiagonalBoxes(grid: Array<IntArray>, random: Random) {
        for (box in 0 until 9 step 3) {
            val nums = (1..9).shuffled(random)
            var index = 0
            for (r in box until box + 3) {
                for (c in box until box + 3) {
                    grid[r][c] = nums[index++]
                }
            }
        }
    }

    private fun solve(grid: Array<IntArray>): Boolean {
        for (row in 0 until 9) {
            for (col in 0 until 9) {
                if (grid[row][col] == 0) {
                    for (digit in 1..9) {
                        if (isValid(grid, row, col, digit)) {
                            grid[row][col] = digit
                            if (solve(grid)) return true
                            grid[row][col] = 0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    fun isValid(grid: Array<IntArray>, row: Int, col: Int, digit: Int): Boolean {
        for (c in 0 until 9) if (grid[row][c] == digit) return false
        for (r in 0 until 9) if (grid[r][col] == digit) return false
        val boxRow = row / 3 * 3
        val boxCol = col / 3 * 3
        for (r in boxRow until boxRow + 3) {
            for (c in boxCol until boxCol + 3) {
                if (grid[r][c] == digit) return false
            }
        }
        return true
    }

    fun findConflicts(grid: List<List<Int>>): Set<Pair<Int, Int>> {
        val conflicts = mutableSetOf<Pair<Int, Int>>()
        for (row in 0 until 9) {
            markDuplicates(grid, (0 until 9).map { row to it }, conflicts)
        }
        for (col in 0 until 9) {
            markDuplicates(grid, (0 until 9).map { it to col }, conflicts)
        }
        for (boxRow in 0 until 9 step 3) {
            for (boxCol in 0 until 9 step 3) {
                val cells = buildList {
                    for (r in boxRow until boxRow + 3) {
                        for (c in boxCol until boxCol + 3) add(r to c)
                    }
                }
                markDuplicates(grid, cells, conflicts)
            }
        }
        return conflicts
    }

    private fun markDuplicates(
        grid: List<List<Int>>,
        cells: List<Pair<Int, Int>>,
        conflicts: MutableSet<Pair<Int, Int>>,
    ) {
        val byDigit = cells.groupBy { (r, c) -> grid[r][c] }.filterKeys { it != 0 }
        for ((_, group) in byDigit) {
            if (group.size > 1) conflicts.addAll(group)
        }
    }
}
