package com.skillslot.puzzles.wordsearch

import kotlin.random.Random

internal data class WordPlacement(
    val word: String,
    val row: Int,
    val col: Int,
    val dr: Int,
    val dc: Int,
)

internal class WordSearchGenerator {
    data class Generated(
        val grid: List<List<Char>>,
        val words: List<String>,
        val placements: List<WordPlacement>,
    )

    fun generate(difficulty: Int, seed: Long): Generated {
        val random = Random(seed)
        val size = when {
            difficulty <= 3 -> 8
            difficulty <= 6 -> 10
            else -> 12
        }
        val wordCount = (3 + difficulty / 2).coerceAtMost(8)
        val pool = WORD_POOL.shuffled(random).take(wordCount).sortedByDescending { it.length }
        val grid = Array(size) { CharArray(size) { PLACEHOLDER } }
        val placements = mutableListOf<WordPlacement>()

        for (word in pool) {
            val placed = placeWord(word, grid, random)
            if (placed != null) placements += placed
        }

        for (row in grid.indices) {
            for (col in grid[row].indices) {
                if (grid[row][col] == PLACEHOLDER) {
                    grid[row][col] = LETTERS.random(random)
                }
            }
        }

        return Generated(
            grid = grid.map { it.toList() },
            words = placements.map { it.word },
            placements = placements,
        )
    }

    private fun placeWord(word: String, grid: Array<CharArray>, random: Random): WordPlacement? {
        val size = grid.size
        val directions = listOf(
            0 to 1,
            1 to 0,
            1 to 1,
            1 to -1,
        )
        repeat(80) {
            val (dr, dc) = directions.random(random)
            val row = random.nextInt(size)
            val col = random.nextInt(size)
            if (canPlace(word, grid, row, col, dr, dc)) {
                word.forEachIndexed { index, char ->
                    grid[row + dr * index][col + dc * index] = char
                }
                return WordPlacement(word, row, col, dr, dc)
            }
        }
        return null
    }

    private fun canPlace(
        word: String,
        grid: Array<CharArray>,
        row: Int,
        col: Int,
        dr: Int,
        dc: Int,
    ): Boolean {
        val size = grid.size
        val endRow = row + dr * (word.lastIndex)
        val endCol = col + dc * (word.lastIndex)
        if (endRow !in 0 until size || endCol !in 0 until size) return false
        word.forEachIndexed { index, char ->
            val r = row + dr * index
            val c = col + dc * index
            val existing = grid[r][c]
            if (existing != PLACEHOLDER && existing != char) return false
        }
        return true
    }

    companion object {
        private const val PLACEHOLDER = '\u0000'
        private val LETTERS = ('A'..'Z').toList()
        private val WORD_POOL = listOf(
            "SLOT", "GOLD", "WIN", "LUCK", "SPIN", "JACKPOT",
            "PUZZLE", "VEGAS", "COIN", "PRIZE", "BONUS", "MEGA",
            "ORO", "DIAMANTE", "REY", "AS", "REINA", "JOKER",
        )
    }
}
