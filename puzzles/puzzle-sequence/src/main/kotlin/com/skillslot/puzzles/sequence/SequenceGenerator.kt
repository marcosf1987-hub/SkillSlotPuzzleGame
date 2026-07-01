package com.skillslot.puzzles.sequence

import kotlin.random.Random

internal class SequenceGenerator {
    data class Generated(val sequence: List<Int>, val padCount: Int)

    fun generate(difficulty: Int, seed: Long): Generated {
        val random = Random(seed)
        val padCount = 4
        val length = (3 + difficulty / 2).coerceIn(3, 8)
        val sequence = List(length) { random.nextInt(padCount) }
        return Generated(sequence, padCount)
    }
}
