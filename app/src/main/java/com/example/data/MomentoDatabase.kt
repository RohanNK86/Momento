package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Task::class,
        Goal::class,
        Event::class,
        Expense::class,
        Habit::class,
        Note::class,
        UserProfile::class
    ],
    version = 2,
    exportSchema = false
)
abstract class MomentoDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun goalDao(): GoalDao
    abstract fun eventDao(): EventDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun habitDao(): HabitDao
    abstract fun noteDao(): NoteDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: MomentoDatabase? = null

        fun getDatabase(context: Context): MomentoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MomentoDatabase::class.java,
                    "momento_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
