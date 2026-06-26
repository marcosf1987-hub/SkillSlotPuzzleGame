package com.skillslot.core.model

enum class PuzzleType(
    val displayName: String,
    val order: Int,
) {
    WORD_SEARCH("Sopa de letras", 0),
    SUDOKU("Sudoku", 1),
    BALL_SORT("Tubos de colores", 2),
    MAZE("Laberinto", 3),
    BOGGLE("Búsqueda en lienzo", 4),
    MEMORY("Memory", 5),
    NONOGRAM("Nonogram", 6),
    SLIDING("Sliding puzzle", 7),
    CONNECT("Conectar puntos", 8),
    SEQUENCE("Secuencia luminosa", 9),
    ;

    companion object {
        val defaultQueue: List<PuzzleType> = entries.sortedBy { it.order }
    }
}
