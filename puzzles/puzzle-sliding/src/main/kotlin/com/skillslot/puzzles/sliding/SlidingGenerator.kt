package com.skillslot.puzzles.sliding

import kotlin.random.Random

internal class SlidingGenerator {
    data class Generated(val size: Int, val tiles: List<List<Int>>)

    fun generate(difficulty: Int, seed: Long): Generated {
        val random = Random(seed)
        val size = if (difficulty <= 5) 3 else 4
        val total = size * size
        var tiles = List(size) { row ->
            List(size) { col -> row * size + col + 1 }.map { if (it == total) 0 else it }
        }
        repeat(80 + difficulty * 10) {
            tiles = applyRandomMove(tiles, random) ?: tiles
        }
        return Generated(size, tiles)
    }

    private fun applyRandomMove(tiles: List<List<Int>>, random: Random): List<List<Int>>? {
        val size = tiles.size
        val empty = findEmpty(tiles) ?: return null
        val (er, ec) = empty
        val neighbors = listOf(er - 1 to ec, er + 1 to ec, er to ec - 1, er to ec + 1)
            .filter { (r, c) -> r in 0 until size && c in 0 until size }
        val (nr, nc) = neighbors.random(random)
        return swapTiles(tiles, empty, nr to nc)
    }
}

internal fun findEmpty(tiles: List<List<Int>>): Pair<Int, Int>? {
    tiles.forEachIndexed { row, line ->
        line.forEachIndexed { col, value ->
            if (value == 0) return row to col
        }
    }
    return null
}

internal fun swapTiles(
    tiles: List<List<Int>>,
    a: Pair<Int, Int>,
    b: Pair<Int, Int>,
): List<List<Int>> {
    val (ar, ac) = a
    val (br, bc) = b
    return tiles.mapIndexed { row, line ->
        line.mapIndexed { col, value ->
            when (row to col) {
                a -> tiles[br][bc]
                b -> tiles[ar][ac]
                else -> value
            }
        }
    }
}

internal fun isSlidingSolved(tiles: List<List<Int>>): Boolean {
    val size = tiles.size
    val total = size * size
    var expected = 1
    for (row in tiles) {
        for (value in row) {
            if (expected == total) return value == 0
            if (value != expected) return false
            expected++
        }
    }
    return true
}

internal fun tryMove(tiles: List<List<Int>>, row: Int, col: Int): List<List<Int>>? {
    val empty = findEmpty(tiles) ?: return null
    val (er, ec) = empty
    val adjacent = (kotlin.math.abs(er - row) + kotlin.math.abs(ec - col)) == 1
    return if (adjacent) swapTiles(tiles, empty, row to col) else null
}
