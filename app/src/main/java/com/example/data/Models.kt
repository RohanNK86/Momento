package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val dueDate: Long? = null,
    val isToday: Boolean = false
) {
    enum class Priority { LOW, MEDIUM, HIGH }
}

@Serializable
@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val targetDateMillis: Long,
    val progress: Float = 0f, // 0.0 to 1.0
    val status: GoalStatus = GoalStatus.ON_TRACK
) {
    enum class GoalStatus { ON_TRACK, AT_RISK, COMPLETED }
}

@Serializable
@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val timeMillis: Long,
    val category: String = "Personal",
    val location: String? = null
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
