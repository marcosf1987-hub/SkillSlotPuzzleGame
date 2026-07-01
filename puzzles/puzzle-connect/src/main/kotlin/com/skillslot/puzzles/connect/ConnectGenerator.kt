package com.skillslot.puzzles.connect

import kotlin.random.Random

internal class ConnectGenerator {
    data class Generated(
        val size: Int,
        val endpoints: Map<Int, Pair<Pair<Int, Int>, Pair<Int, Int>>>,
    )

    fun generate(difficulty: Int, seed: Long): Generated {
        val random = Random(seed)
        val size = when {
            difficulty <= 3 -> 5
            difficulty <= 6 -> 6
            else -> 7
        }
        val pairCount = (2 + difficulty / 3).coerceIn(2, 4)
        val used = mutableSetOf<Pair<Int, Int>>()
        val endpoints = mutableMapOf<Int, Pair<Pair<Int, Int>, Pair<Int, Int>>>()

        for (pair in 1..pairCount) {
            val a = randomFreeCell(size, used, random)
            val b = randomFreeCell(size, used, random)
            used += a
            used += b
            endpoints[pair] = a to b
        }
        return Generated(size, endpoints)
    }

    private fun randomFreeCell(
        size: Int,
        used: Set<Pair<Int, Int>>,
        random: Random,
    ): Pair<Int, Int> {
        repeat(100) {
            val cell = random.nextInt(size) to random.nextInt(size)
            if (cell !in used) return cell
        }
        for (row in 0 until size) {
            for (col in 0 until size) {
                val cell = row to col
                if (cell !in used) return cell
            }
        }
        return 0 to 0
    }
}

internal fun isAdjacentConnect(a: Pair<Int, Int>, b: Pair<Int, Int>): Boolean {
    val (r1, c1) = a
    val (r2, c2) = b
    return kotlin.math.abs(r1 - r2) + kotlin.math.abs(c1 - c2) == 1
}

internal fun cellNumber(
    row: Int,
    col: Int,
    endpoints: Map<Int, Pair<Pair<Int, Int>, Pair<Int, Int>>>,
): Int? {
    for ((number, pair) in endpoints) {
        if (pair.first == row to col || pair.second == row to col) return number
    }
    return null
}
