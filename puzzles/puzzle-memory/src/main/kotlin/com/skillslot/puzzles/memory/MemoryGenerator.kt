package com.skillslot.puzzles.memory

import kotlin.random.Random

internal class MemoryGenerator {
    data class Generated(val cardSymbols: List<Int>)

    fun generate(difficulty: Int, seed: Long): Generated {
        val random = Random(seed)
        val pairCount = (4 + difficulty / 2).coerceIn(4, 8)
        val symbols = buildList {
            repeat(pairCount) { symbol ->
                add(symbol)
                add(symbol)
            }
        }.shuffled(random)
        return Generated(symbols)
    }
}
