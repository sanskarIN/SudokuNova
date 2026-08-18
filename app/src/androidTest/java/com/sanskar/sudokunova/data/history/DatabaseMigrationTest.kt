package com.sanskar.sudokunova.data.history

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseMigrationTest {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.deleteDatabase(TEST_DATABASE)
    }

    @After
    fun tearDown() {
        context.deleteDatabase(TEST_DATABASE)
    }

    @Test
    fun migrationOneToTwoPreservesVersionOneRowsAndCreatesChallengeSchema() {
        createVersionOneDatabase().use { helper ->
            helper.writableDatabase.apply {
                execSQL(
                    """
                    INSERT INTO game_history (
                        puzzle, solution, difficulty, completed, elapsedSeconds, mistakes, hintsUsed,
                        startedAtEpochMillis, completedAtEpochMillis, isDailyChallenge, isPerfect,
                        isFavorite, replayOfHistoryId
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """.trimIndent(),
                    arrayOf(
                        PUZZLE,
                        SOLUTION,
                        "HARD",
                        1,
                        222L,
                        1,
                        0,
                        1_700_000_000_000L,
                        1_700_000_222_000L,
                        0,
                        0,
                        1,
                        null,
                    ),
                )
                execSQL(
                    """
                    INSERT INTO saved_puzzles (
                        puzzle, solution, title, difficulty, source, createdAtEpochMillis, isFavorite
                    ) VALUES (?, ?, ?, ?, ?, ?, ?)
                    """.trimIndent(),
                    arrayOf(
                        PUZZLE,
                        SOLUTION,
                        "Migration fixture",
                        "HARD",
                        "CUSTOM",
                        1_700_000_000_000L,
                        1,
                    ),
                )
            }
        }

        createVersionTwoDatabase().use { helper ->
            val database = helper.writableDatabase

            database.query(
                "SELECT puzzle, elapsedSeconds, isFavorite FROM game_history",
            ).use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals(PUZZLE, cursor.getString(0))
                assertEquals(222L, cursor.getLong(1))
                assertEquals(1, cursor.getInt(2))
                assertTrue(cursor.isLast)
            }

            database.query(
                "SELECT title, source, isFavorite FROM saved_puzzles",
            ).use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals("Migration fixture", cursor.getString(0))
                assertEquals("CUSTOM", cursor.getString(1))
                assertEquals(1, cursor.getInt(2))
                assertTrue(cursor.isLast)
            }

            assertEquals(
                EXPECTED_CHALLENGE_COLUMNS,
                tableColumns(database, "challenge_results"),
            )
            val indexes = tableIndexes(database, "challenge_results")
            assertTrue("index_challenge_results_challengeType_challengeKey" in indexes)
            assertTrue("index_challenge_results_completedAtEpochMillis" in indexes)

            database.execSQL(
                """
                INSERT INTO challenge_results (
                    challengeType, challengeKey, difficulty, puzzle, elapsedSeconds,
                    mistakes, hintsUsed, completedAtEpochMillis, perfect
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent(),
                arrayOf(
                    "DAILY",
                    20_260_818L,
                    "HARD",
                    PUZZLE,
                    180L,
                    0,
                    1,
                    1_700_000_300_000L,
                    0,
                ),
            )
            database.query(
                "SELECT challengeType, challengeKey FROM challenge_results",
            ).use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals("DAILY", cursor.getString(0))
                assertEquals(20_260_818L, cursor.getLong(1))
            }
        }
    }

    private fun createVersionOneDatabase(): SupportSQLiteOpenHelper =
        FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(TEST_DATABASE)
                .callback(
                    object : SupportSQLiteOpenHelper.Callback(1) {
                        override fun onCreate(database: SupportSQLiteDatabase) {
                            createVersionOneSchema(database)
                        }

                        override fun onUpgrade(
                            database: SupportSQLiteDatabase,
                            oldVersion: Int,
                            newVersion: Int,
                        ) = Unit
                    },
                )
                .build(),
        )

    private fun createVersionTwoDatabase(): SupportSQLiteOpenHelper =
        FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(TEST_DATABASE)
                .callback(
                    object : SupportSQLiteOpenHelper.Callback(2) {
                        override fun onCreate(database: SupportSQLiteDatabase) {
                            error("Migration fixture must already exist at version 1")
                        }

                        override fun onUpgrade(
                            database: SupportSQLiteDatabase,
                            oldVersion: Int,
                            newVersion: Int,
                        ) {
                            require(oldVersion == 1 && newVersion == 2)
                            MIGRATION_1_2.migrate(database)
                        }
                    },
                )
                .build(),
        )

    private fun createVersionOneSchema(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS game_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                puzzle TEXT NOT NULL,
                solution TEXT NOT NULL,
                difficulty TEXT NOT NULL,
                completed INTEGER NOT NULL,
                elapsedSeconds INTEGER NOT NULL,
                mistakes INTEGER NOT NULL,
                hintsUsed INTEGER NOT NULL,
                startedAtEpochMillis INTEGER NOT NULL,
                completedAtEpochMillis INTEGER,
                isDailyChallenge INTEGER NOT NULL,
                isPerfect INTEGER NOT NULL,
                isFavorite INTEGER NOT NULL,
                replayOfHistoryId INTEGER
            )
            """.trimIndent(),
        )
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_game_history_difficulty ON game_history (difficulty)",
        )
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_game_history_completedAtEpochMillis " +
                "ON game_history (completedAtEpochMillis)",
        )
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_game_history_isFavorite ON game_history (isFavorite)",
        )
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS saved_puzzles (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                puzzle TEXT NOT NULL,
                solution TEXT,
                title TEXT,
                difficulty TEXT NOT NULL,
                source TEXT NOT NULL,
                createdAtEpochMillis INTEGER NOT NULL,
                isFavorite INTEGER NOT NULL
            )
            """.trimIndent(),
        )
        database.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS index_saved_puzzles_puzzle ON saved_puzzles (puzzle)",
        )
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_saved_puzzles_difficulty ON saved_puzzles (difficulty)",
        )
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_saved_puzzles_isFavorite ON saved_puzzles (isFavorite)",
        )
    }

    private fun tableColumns(database: SupportSQLiteDatabase, table: String): Set<String> {
        val columns = linkedSetOf<String>()
        database.query("PRAGMA table_info(`$table`)").use { cursor ->
            val nameIndex = cursor.getColumnIndexOrThrow("name")
            while (cursor.moveToNext()) {
                columns += cursor.getString(nameIndex)
            }
        }
        return columns
    }

    private fun tableIndexes(database: SupportSQLiteDatabase, table: String): Set<String> {
        val indexes = linkedSetOf<String>()
        database.query("PRAGMA index_list(`$table`)").use { cursor ->
            val nameIndex = cursor.getColumnIndexOrThrow("name")
            while (cursor.moveToNext()) {
                indexes += cursor.getString(nameIndex)
            }
        }
        return indexes
    }

    private companion object {
        const val TEST_DATABASE = "sudokunova-migration-test.db"
        const val PUZZLE =
            "530070000600195000098000060800060003400803001700020006060000280000419005000080079"
        const val SOLUTION =
            "534678912672195348198342567859761423426853791713924856961537284287419635345286179"
        val EXPECTED_CHALLENGE_COLUMNS = setOf(
            "id",
            "challengeType",
            "challengeKey",
            "difficulty",
            "puzzle",
            "elapsedSeconds",
            "mistakes",
            "hintsUsed",
            "completedAtEpochMillis",
            "perfect",
        )
    }
}
