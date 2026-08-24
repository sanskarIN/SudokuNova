package com.sanskar.sudokunova.shared

import com.sanskar.sudokunova.engine.Difficulty

const val SHARED_ACTIVE_GAME_STORAGE_KEY = "sudokunova.shared.active-game.v1"

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

/**
 * Minimal platform boundary for storing one encoded active-game payload.
 *
 * Desktop, Web, and Apple hosts can implement this interface with their native
 * local-storage mechanism while common code retains one versioned encoding and
 * validation contract.
 */
interface SharedGameTextStore {
    suspend fun read(): String?

    suspend fun write(value: String)

    suspend fun clear()
}

class EncodedSharedGameStore(
    private val textStore: SharedGameTextStore,
) : SharedGameStore {
    override suspend fun load(): SharedGameSnapshot? =
        textStore.read()?.let(SharedGameSnapshotCodec::decode)

    override suspend fun save(snapshot: SharedGameSnapshot) {
        textStore.write(SharedGameSnapshotCodec.encode(snapshot))
    }

    override suspend fun clear() {
        textStore.clear()
    }
}
