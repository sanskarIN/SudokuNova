package com.sanskar.sudokunova.shared

import com.sanskar.sudokunova.engine.Difficulty

data class SharedGameSnapshot(
    val difficulty: Difficulty,
    val seed: Long,
    val board: String,
    val notes: Map<Int, Set<Int>>,
    val selectedIndex: Int?,
    val notesMode: Boolean,
)

interface SharedGameStore {
    suspend fun load(): SharedGameSnapshot?

    suspend fun save(snapshot: SharedGameSnapshot)

    suspend fun clear()
}
