package com.sanskar.sudokunova.shared

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SharedSettingsStateTest {
    @Test
    fun updateReplaceRestoreSaveAndClearRemainDeterministic() {
        val state = SharedSettingsState()
        val store = MemorySettingsStore()

        state.update { it.copy(theme = SharedTheme.DARK, sounds = true, mistakeLimit = 5) }
        assertEquals(SharedTheme.DARK, state.settings.theme)
        assertTrue(state.settings.sounds)
        assertEquals(5, state.settings.mistakeLimit)

        runSuspend { state.saveTo(store) }
        assertEquals(state.settings, store.value)

        state.replace(SharedUserSettings(theme = SharedTheme.LIGHT))
        assertTrue(runSuspend { state.restoreFrom(store) })
        assertEquals(SharedTheme.DARK, state.settings.theme)
        assertTrue(state.settings.sounds)

        runSuspend { state.clearStoredSettings(store) }
        assertNull(store.value)
        assertFalse(runSuspend { SharedSettingsState().restoreFrom(store) })
    }

    private class MemorySettingsStore(
        var value: SharedUserSettings? = null,
    ) : SharedSettingsStore {
        override suspend fun load(): SharedUserSettings? = value

        override suspend fun save(settings: SharedUserSettings) {
            value = settings
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
