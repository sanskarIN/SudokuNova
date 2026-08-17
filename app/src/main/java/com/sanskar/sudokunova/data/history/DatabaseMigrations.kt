package com.sanskar.sudokunova.data.history

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `challenge_results` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `challengeType` TEXT NOT NULL,
                `challengeKey` INTEGER NOT NULL,
                `difficulty` TEXT NOT NULL,
                `puzzle` TEXT NOT NULL,
                `elapsedSeconds` INTEGER NOT NULL,
                `mistakes` INTEGER NOT NULL,
                `hintsUsed` INTEGER NOT NULL,
                `completedAtEpochMillis` INTEGER NOT NULL,
                `perfect` INTEGER NOT NULL
            )
            """.trimIndent(),
        )
        database.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_challenge_results_challengeType_challengeKey` " +
                "ON `challenge_results` (`challengeType`, `challengeKey`)",
        )
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_challenge_results_completedAtEpochMillis` " +
                "ON `challenge_results` (`completedAtEpochMillis`)",
        )
    }
}
