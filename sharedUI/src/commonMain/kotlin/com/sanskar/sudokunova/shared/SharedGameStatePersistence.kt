package com.sanskar.sudokunova.shared

/** Save the current portable game state through the supplied platform store. */
suspend fun SharedGameState.saveTo(store: SharedGameStore) {
    store.save(snapshot())
}

/**
 * Restore a previously saved portable game when one exists and passes all
 * snapshot plus generated-puzzle validation.
 */
suspend fun SharedGameState.restoreFrom(store: SharedGameStore): Boolean {
    val snapshot = store.load() ?: return false
    return restore(snapshot)
}

/** Clear the platform's persisted active-game entry without changing this in-memory game. */
suspend fun SharedGameState.clearStoredGame(store: SharedGameStore) {
    store.clear()
}
