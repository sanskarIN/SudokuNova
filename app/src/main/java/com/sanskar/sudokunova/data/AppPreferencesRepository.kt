package com.sanskar.sudokunova.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sanskar.sudokunova.ui.theme.AppTheme
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.sudokuNovaDataStore by preferencesDataStore(name = "sudokunova_preferences")

class AppPreferencesRepository(
    private val context: Context,
) {
    val settings: Flow<UserSettings> = data.map { preferences ->
        UserSettings(
            theme = preferences[Keys.THEME]
                ?.let { value -> runCatching { AppTheme.valueOf(value) }.getOrNull() }
                ?: AppTheme.SYSTEM,
            dynamicColor = preferences[Keys.DYNAMIC_COLOR] ?: true,
            highlightPeers = preferences[Keys.HIGHLIGHT_PEERS] ?: true,
            highlightSameNumbers = preferences[Keys.HIGHLIGHT_SAME] ?: true,
            autoCheckMistakes = preferences[Keys.AUTO_CHECK] ?: true,
            autoRemoveNotes = preferences[Keys.AUTO_REMOVE_NOTES] ?: true,
            showTimer = preferences[Keys.SHOW_TIMER] ?: true,
            haptics = preferences[Keys.HAPTICS] ?: true,
            sounds = preferences[Keys.SOUNDS] ?: false,
            reducedMotion = preferences[Keys.REDUCED_MOTION] ?: false,
            highContrast = preferences[Keys.HIGH_CONTRAST] ?: false,
            mistakeLimit = preferences[Keys.MISTAKE_LIMIT] ?: 3,
        )
    }

    val statistics: Flow<PlayerStatistics> = data.map { preferences ->
        PlayerStatistics(
            gamesStarted = preferences[Keys.GAMES_STARTED] ?: 0,
            gamesCompleted = preferences[Keys.GAMES_COMPLETED] ?: 0,
            gamesAbandoned = preferences[Keys.GAMES_ABANDONED] ?: 0,
            totalPlaySeconds = preferences[Keys.TOTAL_PLAY_SECONDS] ?: 0L,
            bestTimeSeconds = preferences[Keys.BEST_TIME_SECONDS],
            totalMistakes = preferences[Keys.TOTAL_MISTAKES] ?: 0,
            totalHints = preferences[Keys.TOTAL_HINTS] ?: 0,
            perfectGames = preferences[Keys.PERFECT_GAMES] ?: 0,
            noHintGames = preferences[Keys.NO_HINT_GAMES] ?: 0,
            currentStreak = preferences[Keys.CURRENT_STREAK] ?: 0,
            longestStreak = preferences[Keys.LONGEST_STREAK] ?: 0,
        )
    }

    val activeGame: Flow<String?> = data.map { it[Keys.ACTIVE_GAME] }

    suspend fun setTheme(value: AppTheme) = update(Keys.THEME, value.name)
    suspend fun setDynamicColor(value: Boolean) = update(Keys.DYNAMIC_COLOR, value)
    suspend fun setHighlightPeers(value: Boolean) = update(Keys.HIGHLIGHT_PEERS, value)
    suspend fun setHighlightSameNumbers(value: Boolean) = update(Keys.HIGHLIGHT_SAME, value)
    suspend fun setAutoCheck(value: Boolean) = update(Keys.AUTO_CHECK, value)
    suspend fun setAutoRemoveNotes(value: Boolean) = update(Keys.AUTO_REMOVE_NOTES, value)
    suspend fun setShowTimer(value: Boolean) = update(Keys.SHOW_TIMER, value)
    suspend fun setHaptics(value: Boolean) = update(Keys.HAPTICS, value)
    suspend fun setSounds(value: Boolean) = update(Keys.SOUNDS, value)
    suspend fun setReducedMotion(value: Boolean) = update(Keys.REDUCED_MOTION, value)
    suspend fun setHighContrast(value: Boolean) = update(Keys.HIGH_CONTRAST, value)
    suspend fun setMistakeLimit(value: Int) = update(Keys.MISTAKE_LIMIT, value.coerceAtLeast(0))

    suspend fun saveActiveGame(encodedState: String) {
        context.sudokuNovaDataStore.edit { it[Keys.ACTIVE_GAME] = encodedState }
    }

    suspend fun clearActiveGame() {
        context.sudokuNovaDataStore.edit { it.remove(Keys.ACTIVE_GAME) }
    }

    suspend fun recordGameStarted() {
        context.sudokuNovaDataStore.edit { preferences ->
            preferences[Keys.GAMES_STARTED] = (preferences[Keys.GAMES_STARTED] ?: 0) + 1
        }
    }

    suspend fun recordGameAbandoned() {
        context.sudokuNovaDataStore.edit { preferences ->
            preferences[Keys.GAMES_ABANDONED] = (preferences[Keys.GAMES_ABANDONED] ?: 0) + 1
        }
    }

    suspend fun recordGameCompleted(
        elapsedSeconds: Long,
        mistakes: Int,
        hintsUsed: Int,
        completedEpochDay: Long,
    ) {
        context.sudokuNovaDataStore.edit { preferences ->
            val previousBest = preferences[Keys.BEST_TIME_SECONDS]
            val currentStreak = preferences[Keys.CURRENT_STREAK] ?: 0
            val previousDay = preferences[Keys.LAST_COMPLETED_EPOCH_DAY]
            val newStreak = when {
                previousDay == null -> 1
                completedEpochDay == previousDay -> currentStreak.coerceAtLeast(1)
                completedEpochDay == previousDay + 1L -> currentStreak + 1
                else -> 1
            }

            preferences[Keys.GAMES_COMPLETED] = (preferences[Keys.GAMES_COMPLETED] ?: 0) + 1
            preferences[Keys.TOTAL_PLAY_SECONDS] =
                (preferences[Keys.TOTAL_PLAY_SECONDS] ?: 0L) + elapsedSeconds
            preferences[Keys.TOTAL_MISTAKES] = (preferences[Keys.TOTAL_MISTAKES] ?: 0) + mistakes
            preferences[Keys.TOTAL_HINTS] = (preferences[Keys.TOTAL_HINTS] ?: 0) + hintsUsed
            preferences[Keys.CURRENT_STREAK] = newStreak
            preferences[Keys.LONGEST_STREAK] = maxOf(
                preferences[Keys.LONGEST_STREAK] ?: 0,
                newStreak,
            )
            preferences[Keys.LAST_COMPLETED_EPOCH_DAY] = completedEpochDay

            if (previousBest == null || elapsedSeconds < previousBest) {
                preferences[Keys.BEST_TIME_SECONDS] = elapsedSeconds
            }
            if (mistakes == 0 && hintsUsed == 0) {
                preferences[Keys.PERFECT_GAMES] = (preferences[Keys.PERFECT_GAMES] ?: 0) + 1
            }
            if (hintsUsed == 0) {
                preferences[Keys.NO_HINT_GAMES] = (preferences[Keys.NO_HINT_GAMES] ?: 0) + 1
            }
        }
    }

    suspend fun resetStatistics() {
        context.sudokuNovaDataStore.edit { preferences ->
            preferences.remove(Keys.GAMES_STARTED)
            preferences.remove(Keys.GAMES_COMPLETED)
            preferences.remove(Keys.GAMES_ABANDONED)
            preferences.remove(Keys.TOTAL_PLAY_SECONDS)
            preferences.remove(Keys.BEST_TIME_SECONDS)
            preferences.remove(Keys.TOTAL_MISTAKES)
            preferences.remove(Keys.TOTAL_HINTS)
            preferences.remove(Keys.PERFECT_GAMES)
            preferences.remove(Keys.NO_HINT_GAMES)
            preferences.remove(Keys.CURRENT_STREAK)
            preferences.remove(Keys.LONGEST_STREAK)
            preferences.remove(Keys.LAST_COMPLETED_EPOCH_DAY)
        }
    }

    private val data: Flow<Preferences>
        get() = context.sudokuNovaDataStore.data.catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }

    private suspend fun <T> update(key: Preferences.Key<T>, value: T) {
        context.sudokuNovaDataStore.edit { it[key] = value }
    }

    private object Keys {
        val THEME = stringPreferencesKey("theme")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val HIGHLIGHT_PEERS = booleanPreferencesKey("highlight_peers")
        val HIGHLIGHT_SAME = booleanPreferencesKey("highlight_same_numbers")
        val AUTO_CHECK = booleanPreferencesKey("auto_check_mistakes")
        val AUTO_REMOVE_NOTES = booleanPreferencesKey("auto_remove_notes")
        val SHOW_TIMER = booleanPreferencesKey("show_timer")
        val HAPTICS = booleanPreferencesKey("haptics")
        val SOUNDS = booleanPreferencesKey("sounds")
        val REDUCED_MOTION = booleanPreferencesKey("reduced_motion")
        val HIGH_CONTRAST = booleanPreferencesKey("high_contrast")
        val MISTAKE_LIMIT = intPreferencesKey("mistake_limit")
        val ACTIVE_GAME = stringPreferencesKey("active_game")

        val GAMES_STARTED = intPreferencesKey("games_started")
        val GAMES_COMPLETED = intPreferencesKey("games_completed")
        val GAMES_ABANDONED = intPreferencesKey("games_abandoned")
        val TOTAL_PLAY_SECONDS = longPreferencesKey("total_play_seconds")
        val BEST_TIME_SECONDS = longPreferencesKey("best_time_seconds")
        val TOTAL_MISTAKES = intPreferencesKey("total_mistakes")
        val TOTAL_HINTS = intPreferencesKey("total_hints")
        val PERFECT_GAMES = intPreferencesKey("perfect_games")
        val NO_HINT_GAMES = intPreferencesKey("no_hint_games")
        val CURRENT_STREAK = intPreferencesKey("current_streak")
        val LONGEST_STREAK = intPreferencesKey("longest_streak")
        val LAST_COMPLETED_EPOCH_DAY = longPreferencesKey("last_completed_epoch_day")
    }
}
