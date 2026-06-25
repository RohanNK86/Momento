package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class MomentoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            listOf(
                NotificationChannel("TASK_REMINDERS",  "Task Reminders",  NotificationManager.IMPORTANCE_HIGH).apply { description = "Task deadline and reminder notifications" },
                NotificationChannel("HABIT_REMINDERS", "Habit Reminders", NotificationManager.IMPORTANCE_DEFAULT).apply { description = "Daily habit check-in prompts" },
                NotificationChannel("EVENT_ALERTS",    "Event Alerts",    NotificationManager.IMPORTANCE_HIGH).apply { description = "Upcoming event notifications" },
                NotificationChannel("GOAL_UPDATES",    "Goal Updates",    NotificationManager.IMPORTANCE_LOW).apply { description = "Goal milestone and progress updates" }
            ).forEach { manager.createNotificationChannel(it) }
        }
    }
}
