package com.skillslot.puzzles.ballsort

import kotlin.random.Random

internal class BallSortGenerator {
    data class Generated(
        val tubes: List<List<Int>>,
        val colorCount: Int,
    )

    fun generate(difficulty: Int, seed: Long): Generated {
        val random = Random(seed)
        val colorCount = (3 + difficulty / 2).coerceIn(4, 7)
        val capacity = 4
        val balls = buildList {
            repeat(colorCount) { color ->
                repeat(capacity) { add(color) }
            }
        }.shuffled(random)
        val emptyTubes = if (difficulty <= 4) 2 else 1
        val tubeCount = colorCount + emptyTubes
        val mutableTubes = List(tubeCount) { mutableListOf<Int>() }
        for (ball in balls) {
            val candidates = (0 until colorCount).filter { mutableTubes[it].size < capacity }
            if (candidates.isEmpty()) break
            mutableTubes[candidates.random(random)].add(ball)
        }
        if (isBallSortSolved(mutableTubes.map { it.toList() })) {
            return generate(difficulty, seed + 1)
        }
        return Generated(mutableTubes.map { it.toList() }, colorCount)
    }
}

internal fun isBallSortSolved(tubes: List<List<Int>>): Boolean =
    tubes.all { tube -> tube.isEmpty() || (tube.distinct().size == 1 && tube.size <= 4) }

internal fun canMoveBall(
    tubes: List<List<Int>>,
    from: Int,
    to: Int,
    capacity: Int = 4,
): Boolean {
    if (from == to) return false
    if (from !in tubes.indices || to !in tubes.indices) return false
    val source = tubes[from]
    val target = tubes[to]
    if (source.isEmpty()) return false
    if (target.size >= capacity) return false
    val ball = source.last()
    if (target.isNotEmpty() && target.last() != ball) return false
    return true
}

internal fun moveBall(tubes: List<List<Int>>, from: Int, to: Int): List<List<Int>> {
    if (!canMoveBall(tubes, from, to)) return tubes
    return tubes.mapIndexed { index, tube ->
        when (index) {
            from -> tube.dropLast(1)
            to -> tube + tubes[from].last()
            else -> tube
        }
    }
}
