package com.sanskar.sudokunova.data.history

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sanskar.sudokunova.data.challenge.ChallengeResultDao
import com.sanskar.sudokunova.data.challenge.ChallengeResultEntity

@Database(
    entities = [
        GameHistoryEntity::class,
        SavedPuzzleEntity::class,
        ChallengeResultEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
abstract class SudokuNovaDatabase : RoomDatabase() {
    abstract fun gameHistoryDao(): GameHistoryDao
    abstract fun savedPuzzleDao(): SavedPuzzleDao
    abstract fun challengeResultDao(): ChallengeResultDao

    companion object {
        @Volatile
        private var instance: SudokuNovaDatabase? = null

        fun get(context: Context): SudokuNovaDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                SudokuNovaDatabase::class.java,
                DATABASE_NAME,
            )
                .addMigrations(MIGRATION_1_2)
                .build()
                .also { instance = it }
        }

        internal const val DATABASE_NAME = "sudokunova.db"
    }
}
