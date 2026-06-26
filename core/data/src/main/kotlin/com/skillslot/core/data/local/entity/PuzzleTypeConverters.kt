package com.skillslot.core.data.local.entity

import androidx.room.TypeConverter
import com.skillslot.core.model.PuzzleType

class PuzzleTypeConverters {
    @TypeConverter
    fun fromPuzzleTypeList(types: List<PuzzleType>): String =
        types.joinToString(SEPARATOR) { it.name }

    @TypeConverter
    fun toPuzzleTypeList(raw: String): List<PuzzleType> {
        if (raw.isBlank()) return PuzzleType.defaultQueue
        return raw.split(SEPARATOR).mapNotNull { name ->
            runCatching { PuzzleType.valueOf(name) }.getOrNull()
        }
    }

    companion object {
        private const val SEPARATOR = ","
    }
}
