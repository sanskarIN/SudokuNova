package com.sanskar.sudokunova.shared

import com.sanskar.sudokunova.engine.Difficulty
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class EncodedSharedGameStoreTest {
    @Test
    fun saveLoadAndClearUseTheCommonCodec() {
        val textStore = FakeTextStore()
        val store = EncodedSharedGameStore(textStore)
        val snapshot = SharedGameSnapshot(
            difficulty = Difficulty.EXPERT,
            seed = 1234L,
            board = "0".repeat(81),
            notes = mapOf(8 to setOf(1, 4, 9)),
            selectedIndex = 8,
            notesMode = true,
        )

        runSuspend { store.save(snapshot) }

        assertEquals(snapshot, runSuspend { store.load() })
        assertEquals(SharedGameSnapshotCodec.encode(snapshot), textStore.value)

        runSuspend { store.clear() }
        assertNull(textStore.value)
        assertNull(runSuspend { store.load() })
    }

    @Test
    fun corruptedStoredPayloadFailsClosed() {
        val textStore = FakeTextStore("not-a-valid-snapshot")
        val store = EncodedSharedGameStore(textStore)

        assertNull(runSuspend { store.load() })
        assertEquals("not-a-valid-snapshot", textStore.value)
    }

    private class FakeTextStore(
        var value: String? = null,
    ) : SharedGameTextStore {
        override suspend fun read(): String? = value

        override suspend fun write(value: String) {
            this.value = value
        }

        override suspend fun clear() {
            value = null
        }
    }

    private fun <T> runSuspend(block: suspend () -> T): T {
        var outcome: Result<T>? = null
        block.startCoroutine(
            object : Continuation<T> {
                override val context = EmptyCoroutineContext

                override fun resumeWith(result: Result<T>) {
                    outcome = result
                }
            },
        )
        return outcome?.getOrThrow() ?: error("Suspend block did not complete synchronously in this unit test.")
    }
}
