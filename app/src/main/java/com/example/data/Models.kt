package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val category: String = "Personal",
    val dueDate: Long? = null,
    val isToday: Boolean = false,
    val isArchived: Boolean = false,
    val repeatType: String = "NONE",
    val tags: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    enum class Priority { LOW, MEDIUM, HIGH }
}

@Serializable
@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String = "",
    val targetDateMillis: Long = System.currentTimeMillis() + 2592000000L,
    val progress: Float = 0f,          // 0.0 – 1.0 (derived from currentValue/targetValue)
    val currentValue: Float = 0f,
    val targetValue: Float = 100f,
    val milestones: String = "[]",     // JSON array of milestone strings
    val status: GoalStatus = GoalStatus.ON_TRACK
) {
    enum class GoalStatus { ON_TRACK, AT_RISK, COMPLETED }
}

@Serializable
@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String = "",
    val timeMillis: Long,
    val endTimeMillis: Long? = null,
    val category: String = "Personal",
    val location: String? = null,
    val color: String = "#6366F1",
    val priority: String = "MEDIUM",
    val repeatType: String = "NONE",
    val reminderMinutes: Int = 0
)

@Serializable
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val dateMillis: Long,
    val amount: Double,
    val category: String = "Other"
)

@Serializable
@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String = "",
    val frequency: String = "DAILY",   // DAILY | WEEKLY | MONTHLY
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val completedDates: String = "[]", // JSON array of "yyyy-MM-dd" strings
    val color: String = "#6366F1",
    val icon: String = "star",
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val tags: String = "",
    val color: String = "#1A1F2D",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Rohan",
    val gender: String = "",
    val dateOfBirth: Long? = null,
    val occupation: String = "",
    val email: String = "",
    val phone: String = "",
    val weightKg: Float? = null,
    val heightCm: Float? = null,
    val bio: String = "",
    val goalsText: String = "",
    val avatarColor: String = "#6366F1"
)
