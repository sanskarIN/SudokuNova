package com.sanskar.sudokunova.shared

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class EncodedSharedSettingsStoreTest {
    @Test
    fun saveLoadAndClearUseTheSharedCodec() {
        val textStore = MemorySettingsTextStore()
        val store = EncodedSharedSettingsStore(textStore)
        val settings = SharedUserSettings(
            theme = SharedTheme.DARK,
            inputMode = SharedInputMode.NUMBER_FIRST,
            sounds = true,
            reducedMotion = true,
            mistakeLimit = 0,
        )

        runSuspend { store.save(settings) }
        assertEquals(SharedSettingsCodec.encode(settings), textStore.value)
        assertEquals(settings, runSuspend { store.load() })

        runSuspend { store.clear() }
        assertNull(runSuspend { store.load() })
    }

    @Test
    fun corruptStoredSettingsFailClosed() {
        val textStore = MemorySettingsTextStore("SNS2|unsupported=1")
        val store = EncodedSharedSettingsStore(textStore)

        assertFailsWith<IllegalArgumentException> { runSuspend { store.load() } }
    }

    private class MemorySettingsTextStore(
        var value: String? = null,
    ) : SharedSettingsTextStore {
        override suspend fun read(): String? = value

        override suspend fun write(value: String) {
            this.value = value
        }

        override suspend fun clear() {
            value = null
        }
    }

    private fun <T> runSuspend(block: suspend () -> T): T {
        var completed = false
        var result: Result<T>? = null
        block.startCoroutine(object : Continuation<T> {
            override val context = EmptyCoroutineContext

            override fun resumeWith(resumeResult: Result<T>) {
                completed = true
                result = resumeResult
            }
        })
        check(completed) { "Test coroutine did not complete synchronously." }
        return result!!.getOrThrow()
    }
}
