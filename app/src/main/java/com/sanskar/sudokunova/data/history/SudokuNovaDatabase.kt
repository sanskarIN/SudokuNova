package com.sanskar.sudokunova.data.history

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        GameHistoryEntity::class,
        SavedPuzzleEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class SudokuNovaDatabase : RoomDatabase() {
    abstract fun gameHistoryDao(): GameHistoryDao
    abstract fun savedPuzzleDao(): SavedPuzzleDao

    companion object {
        private const val DATABASE_NAME = "sudokunova.db"

        @Volatile
        private var instance: SudokuNovaDatabase? = null

        fun get(context: Context): SudokuNovaDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                SudokuNovaDatabase::class.java,
                DATABASE_NAME,
            ).build().also { instance = it }
        }
    }
}
