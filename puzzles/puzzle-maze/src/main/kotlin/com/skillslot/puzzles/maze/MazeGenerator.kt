package com.skillslot.puzzles.maze

import kotlin.random.Random

internal class MazeGenerator {
    data class Generated(
        val walls: List<List<Boolean>>,
        val start: Pair<Int, Int>,
        val goal: Pair<Int, Int>,
    )

    fun generate(difficulty: Int, seed: Long): Generated {
        val random = Random(seed)
        val size = when {
            difficulty <= 3 -> 9
            difficulty <= 6 -> 11
            else -> 13
        }
        val walls = Array(size) { BooleanArray(size) { true } }
        carve(1, 1, walls, random)
        walls[0][1] = false
        walls[size - 1][size - 2] = false
        return Generated(
            walls = walls.map { row -> row.toList() },
            start = 0 to 1,
            goal = size - 1 to (size - 2),
        )
    }

    private fun carve(row: Int, col: Int, walls: Array<BooleanArray>, random: Random) {
        walls[row][col] = false
        val directions = listOf(-2 to 0, 2 to 0, 0 to -2, 0 to 2).shuffled(random)
        for ((dr, dc) in directions) {
            val nr = row + dr
            val nc = col + dc
            if (nr in walls.indices && nc in walls.indices && walls[nr][nc]) {
                walls[row + dr / 2][col + dc / 2] = false
                carve(nr, nc, walls, random)
            }
        }
    }
}

internal fun isAdjacent(a: Pair<Int, Int>, b: Pair<Int, Int>): Boolean {
    val (r1, c1) = a
    val (r2, c2) = b
    return kotlin.math.abs(r1 - r2) + kotlin.math.abs(c1 - c2) == 1
}
