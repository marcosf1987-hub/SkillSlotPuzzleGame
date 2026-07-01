package com.skillslot.puzzles.boggle

import kotlin.random.Random

internal class BoggleGenerator {
    data class Generated(
        val grid: List<List<Char>>,
        val words: List<String>,
    )

    fun generate(difficulty: Int, seed: Long): Generated {
        val random = Random(seed)
        val size = when {
            difficulty <= 3 -> 4
            difficulty <= 6 -> 4
            else -> 5
        }
        val wordCount = (2 + difficulty / 2).coerceIn(3, 6)
        repeat(40) { attempt ->
            val grid = List(size) {
                List(size) { LETTERS.random(random) }
            }
            val found = findAllWords(grid, MIN_WORD_LENGTH)
                .filter { it.length >= MIN_WORD_LENGTH }
                .distinct()
                .sortedByDescending { it.length }
            val targets = WORD_POOL
                .filter { word -> found.any { it.equals(word, ignoreCase = true) } }
                .shuffled(random)
                .take(wordCount)
            if (targets.size >= (wordCount - 1).coerceAtLeast(2)) {
                return Generated(grid, targets.map { it.uppercase() })
            }
            if (found.size >= wordCount) {
                return Generated(grid, found.take(wordCount).map { it.uppercase() })
            }
        }
        val fallbackGrid = listOf(
            listOf('S', 'P', 'I', 'N'),
            listOf('L', 'O', 'T', 'G'),
            listOf('W', 'I', 'N', 'O'),
            listOf('G', 'O', 'L', 'D'),
        )
        return Generated(fallbackGrid, listOf("SPIN", "SLOT", "GOLD", "WIN").take(wordCount))
    }

    companion object {
        private const val MIN_WORD_LENGTH = 3
        private val LETTERS = ('A'..'Z').toList()
        private val WORD_POOL = listOf(
            "SLOT", "GOLD", "WIN", "LUCK", "SPIN", "COIN",
            "PRIZE", "BONUS", "MEGA", "ORO", "REY", "AS",
            "LOG", "GIN", "LOT", "PIN", "GOT", "SON",
        )
    }
}

internal fun findAllWords(grid: List<List<Char>>, minLength: Int): List<String> {
    val size = grid.size
    val results = mutableSetOf<String>()
    val visited = Array(size) { BooleanArray(size) }

    fun dfs(row: Int, col: Int, path: String) {
        if (row !in 0 until size || col !in 0 until size || visited[row][col]) return
        val next = path + grid[row][col]
        if (next.length >= minLength) results += next
        visited[row][col] = true
        for (dr in -1..1) {
            for (dc in -1..1) {
                if (dr == 0 && dc == 0) continue
                dfs(row + dr, col + dc, next)
            }
        }
        visited[row][col] = false
    }

    for (row in 0 until size) {
        for (col in 0 until size) {
            dfs(row, col, "")
        }
    }
    return results.toList()
}

internal fun isValidBogglePath(grid: List<List<Char>>, cells: List<Pair<Int, Int>>): String? {
    if (cells.isEmpty()) return null
    val size = grid.size
    val used = mutableSetOf<Pair<Int, Int>>()
    val word = buildString {
        var prev: Pair<Int, Int>? = null
        for (cell in cells) {
            val (row, col) = cell
            if (row !in 0 until size || col !in 0 until size) return null
            if (cell in used) return null
            prev?.let { (pr, pc) ->
                if (kotlin.math.abs(pr - row) > 1 || kotlin.math.abs(pc - col) > 1) return null
            }
            used += cell
            append(grid[row][col])
        }
    }
    return word
}

internal fun buildAdjacentSelection(
    grid: List<List<Char>>,
    cells: List<Pair<Int, Int>>,
    end: Pair<Int, Int>,
): List<Pair<Int, Int>> {
    if (cells.isEmpty()) return listOf(end)
    val last = cells.last()
    if (kotlin.math.abs(last.first - end.first) > 1 ||
        kotlin.math.abs(last.second - end.second) > 1
    ) {
        return cells + end
    }
    if (end in cells) return cells
    return cells + end
}
