package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Task::class, Goal::class, Event::class, Expense::class], version = 1, exportSchema = false)
abstract class MomentoDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun goalDao(): GoalDao
    abstract fun eventDao(): EventDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: MomentoDatabase? = null

        fun getDatabase(context: Context): MomentoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MomentoDatabase::class.java,
                    "momento_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
