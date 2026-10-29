package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*

class MomentoViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = application.getSharedPreferences("momento_preferences", android.content.Context.MODE_PRIVATE)

    private val database = MomentoDatabase.getDatabase(application)
    private val repository = MomentoRepository(
        database.taskDao(),
        database.goalDao(),
        database.eventDao(),
        database.expenseDao(),
        database.habitDao(),
        database.noteDao(),
        database.userProfileDao()
    )

    // â”€â”€ StateFlows â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    val tasks: StateFlow<List<Task>> = repository.allTasks.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val goals: StateFlow<List<Goal>> = repository.allGoals.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val events: StateFlow<List<Event>> = repository.allEvents.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val expenses: StateFlow<List<Expense>> = repository.allExpenses.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val habits: StateFlow<List<Habit>> = repository.allHabits.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val notes: StateFlow<List<Note>> = repository.allNotes.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val userProfile: StateFlow<UserProfile?> = repository.userProfile.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    // â”€â”€ Greeting â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    fun getGreeting(name: String): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour in 5..11  -> "Good Morning, $name â˜€ï¸"
            hour in 12..16 -> "Good Afternoon, $name â˜€ï¸"
            hour in 17..20 -> "Good Evening, $name ðŸŒ‡"
            else           -> "Good Night, $name ðŸŒ™"
        }
    }

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    // â”€â”€ Task Operations â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    fun addTask(
        title: String,
        priority: Task.Priority,
        description: String = "",
        category: String = "Personal",
        dueDate: Long? = null
    ) = viewModelScope.launch {
        repository.insertTask(
            Task(
                title = title,
                description = description,
                priority = priority,
                category = category,
                dueDate = dueDate,
                isToday = true
            )
        )
    }

    fun toggleTaskCompletion(task: Task) = viewModelScope.launch {
        repository.updateTask(task.copy(isCompleted = !task.isCompleted))
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.deleteTask(task.id)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        repository.updateTask(task)
    }

    fun archiveTask(task: Task) = viewModelScope.launch {
        repository.updateTask(task.copy(isArchived = true))
    }

    // â”€â”€ Goal Operations â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    fun addGoal(
        title: String,
        description: String = "",
        targetValue: Float = 100f,
        targetDateMillis: Long = System.currentTimeMillis() + 2592000000L
    ) = viewModelScope.launch {
        repository.insertGoal(
            Goal(
                title = title,
                description = description,
                targetValue = targetValue,
                targetDateMillis = targetDateMillis
            )
        )
    }

    fun updateGoalProgress(goal: Goal, newValue: Float) = viewModelScope.launch {
        val clamped = newValue.coerceIn(0f, goal.targetValue)
        val newProgress = clamped / goal.targetValue
        val newStatus = when {
            newProgress >= 1f -> Goal.GoalStatus.COMPLETED
            newProgress < 0.3f && isDeadlineSoon(goal.targetDateMillis) -> Goal.GoalStatus.AT_RISK
            else -> Goal.GoalStatus.ON_TRACK
        }
        repository.updateGoal(
            goal.copy(
                currentValue = clamped,
                progress = newProgress,
                status = newStatus
            )
        )
    }

    fun deleteGoal(goal: Goal) = viewModelScope.launch { repository.deleteGoal(goal.id) }

    private fun isDeadlineSoon(targetMillis: Long): Boolean {
        val daysLeft = (targetMillis - System.currentTimeMillis()) / 86400000L
        return daysLeft < 7
    }

    // â”€â”€ Event Operations â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    fun addEvent(
        title: String,
        timeMillis: Long,
        category: String,
        description: String = "",
        location: String = "",
        color: String = "#6366F1"
    ) = viewModelScope.launch {
        repository.insertEvent(
            Event(
                title = title,
                timeMillis = timeMillis,
                category = category,
                description = description,
                location = location.ifBlank { null },
                color = color
            )
        )
    }

    fun deleteEvent(event: Event) = viewModelScope.launch { repository.deleteEvent(event.id) }

    // â”€â”€ Expense Operations â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    fun addExpense(title: String, amount: Double, category: String) = viewModelScope.launch {
        repository.insertExpense(
            Expense(
                title = title,
                amount = amount,
                dateMillis = System.currentTimeMillis(),
                category = category
            )
        )
    }

    fun deleteExpense(expense: Expense) = viewModelScope.launch {
        repository.deleteExpense(expense.id)
    }

    // â”€â”€ Habit Operations â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    fun addHabit(
        name: String,
        description: String = "",
        frequency: String = "DAILY",
        color: String = "#6366F1",
        icon: String = "star"
    ) = viewModelScope.launch {
        repository.insertHabit(
            Habit(
                name = name,
                description = description,
                frequency = frequency,
                color = color,
                icon = icon
            )
        )
    }

    fun toggleHabitForToday(habit: Habit) = viewModelScope.launch {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val dates = try {
            Json.decodeFromString<List<String>>(habit.completedDates).toMutableList()
        } catch (e: Exception) {
            mutableListOf()
        }
        if (dates.contains(today)) dates.remove(today) else dates.add(today)
        val sorted = dates.sorted()
        val current = calculateCurrentStreak(sorted)
        val longest = maxOf(habit.longestStreak, current)
        repository.updateHabit(
            habit.copy(
                completedDates = Json.encodeToString(sorted),
                currentStreak = current,
                longestStreak = longest
            )
        )
    }

    fun deleteHabit(habit: Habit) = viewModelScope.launch { repository.deleteHabit(habit.id) }

    fun isHabitCompletedToday(habit: Habit): Boolean {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return try {
            Json.decodeFromString<List<String>>(habit.completedDates).contains(today)
        } catch (e: Exception) { false }
    }

    fun getHabitLast7Days(habit: Habit): List<Boolean> {
        val dates = try {
            Json.decodeFromString<List<String>>(habit.completedDates)
        } catch (e: Exception) { emptyList() }
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        return (6 downTo 0).map { daysAgo ->
            cal.time = Date()
            cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
            dates.contains(sdf.format(cal.time))
        }
    }

    private fun calculateCurrentStreak(sortedDates: List<String>): Int {
        if (sortedDates.isEmpty()) return 0
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = sdf.format(Date())
        if (!sortedDates.contains(today)) return 0
        val cal = Calendar.getInstance()
        var streak = 0
        while (true) {
            val dateStr = sdf.format(cal.time)
            if (sortedDates.contains(dateStr)) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else break
        }
        return streak
    }

    // â”€â”€ Note Operations â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    fun addNote(title: String, content: String, tags: String = "", color: String = "#1A1F2D") =
        viewModelScope.launch {
            repository.insertNote(Note(title = title, content = content, tags = tags, color = color))
        }

    fun toggleNotePin(note: Note) = viewModelScope.launch {
        repository.updateNote(note.copy(isPinned = !note.isPinned, updatedAt = System.currentTimeMillis()))
    }

    fun toggleNoteFavorite(note: Note) = viewModelScope.launch {
        repository.updateNote(note.copy(isFavorite = !note.isFavorite, updatedAt = System.currentTimeMillis()))
    }

    fun deleteNote(note: Note) = viewModelScope.launch { repository.deleteNote(note.id) }

    fun updateNote(note: Note, title: String, content: String) = viewModelScope.launch {
        repository.updateNote(note.copy(title = title, content = content, updatedAt = System.currentTimeMillis()))
    }

    // â”€â”€ Profile Operations â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    fun saveProfile(profile: UserProfile) = viewModelScope.launch {
        repository.saveProfile(profile)
    }

    fun clearAllData() = viewModelScope.launch { repository.clearAllData() }

    fun getInitials(name: String): String {
        return name.trim().split(" ")
            .filter { it.isNotEmpty() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
            .ifEmpty { "?" }
    }

    // â”€â”€ Today's Stats â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    fun getTodayEventCount(): Int {
        val cal = Calendar.getInstance()
        val startOfDay = cal.apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
        }.timeInMillis
        val endOfDay = startOfDay + 86400000L
        return events.value.count { it.timeMillis in startOfDay..endOfDay }
    }

    fun getHabitsCompletedToday(): Int = habits.value.count { isHabitCompletedToday(it) }

    // â”€â”€ Motivational Quotes â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    val motivationalQuotes = listOf(
        "The secret of getting ahead is getting started." to "Mark Twain",
        "It always seems impossible until it's done." to "Nelson Mandela",
        "Don't watch the clock; do what it does. Keep going." to "Sam Levenson",
        "The future depends on what you do today." to "Mahatma Gandhi",
        "Success is not final, failure is not fatal." to "Winston Churchill",
        "Believe you can and you're halfway there." to "Theodore Roosevelt",
        "Your limitationâ€”it's only your imagination." to "Unknown",
        "Push yourself, because no one else is going to do it for you." to "Unknown",
        "Great things never come from comfort zones." to "Unknown",
        "Dream it. Wish it. Do it." to "Unknown",
        "Success doesn't just find you. You have to go out and get it." to "Unknown",
        "The harder you work for something, the greater you'll feel when you achieve it." to "Unknown"
    )

    fun getTodayQuote(): Pair<String, String> {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        return motivationalQuotes[dayOfYear % motivationalQuotes.size]
    }

    // â”€â”€ Seed Data â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    init {
        viewModelScope.launch {
            repository.userProfile.collect { profile ->
                if (profile == null) {
                    repository.saveProfile(UserProfile(name = "You"))
                }
            }
        }
        // Keep the app populated with the original, clearly editable starter content.
        // These records are created only for empty collections and remain ordinary local data.
        viewModelScope.launch {
            repository.allTasks.collect { list ->
                if (list.isEmpty() && !preferences.getBoolean("sample_tasks_seeded", false)) {
                    preferences.edit().putBoolean("sample_tasks_seeded", true).apply()
                    repository.insertTask(Task(title = "Finalize Q4 Marketing Deck", priority = Task.Priority.HIGH, isToday = true, category = "Work"))
                    repository.insertTask(Task(title = "Review Design System Updates", priority = Task.Priority.MEDIUM, isToday = true, category = "Work"))
                    repository.insertTask(Task(title = "Schedule Team 1:1s", priority = Task.Priority.LOW, isToday = true, category = "Work"))
                } else if (list.isNotEmpty()) {
                    preferences.edit().putBoolean("sample_tasks_seeded", true).apply()
                }
            }
        }
        viewModelScope.launch {
            repository.allGoals.collect { list ->
                if (list.isEmpty() && !preferences.getBoolean("sample_goals_seeded", false)) {
                    preferences.edit().putBoolean("sample_goals_seeded", true).apply()
                    repository.insertGoal(Goal(title = "Launch MVP", progress = 0.75f, currentValue = 75f, status = Goal.GoalStatus.ON_TRACK))
                    repository.insertGoal(Goal(title = "Learn Jetpack Compose", progress = 0.40f, currentValue = 40f, status = Goal.GoalStatus.ON_TRACK))
                    repository.insertGoal(Goal(title = "Run 5k Daily", progress = 0.20f, currentValue = 20f, status = Goal.GoalStatus.AT_RISK))
                    repository.insertGoal(Goal(title = "Read 12 Books", progress = 1.0f, currentValue = 100f, status = Goal.GoalStatus.COMPLETED))
                } else if (list.isNotEmpty()) {
                    preferences.edit().putBoolean("sample_goals_seeded", true).apply()
                }
            }
        }
        viewModelScope.launch {
            repository.allEvents.collect { list ->
                if (list.isEmpty() && !preferences.getBoolean("sample_events_seeded", false)) {
                    preferences.edit().putBoolean("sample_events_seeded", true).apply()
                    val now = System.currentTimeMillis()
                    repository.insertEvent(Event(title = "Design Sync", timeMillis = now + 3600000, category = "Work", color = "#6857C8"))
                    repository.insertEvent(Event(title = "Gym Session", timeMillis = now + 86400000, category = "Health", color = "#26836C"))
                    repository.insertEvent(Event(title = "Dinner with Sarah", timeMillis = now + 172800000, category = "Personal", color = "#776F9F"))
                    repository.insertEvent(Event(title = "Dentist Appointment", timeMillis = now + 259200000, category = "Health", color = "#B54748"))
                } else if (list.isNotEmpty()) {
                    preferences.edit().putBoolean("sample_events_seeded", true).apply()
                }
            }
        }
        viewModelScope.launch {
            repository.allExpenses.collect { list ->
                if (list.isEmpty() && !preferences.getBoolean("sample_expenses_seeded", false)) {
                    preferences.edit().putBoolean("sample_expenses_seeded", true).apply()
                    repository.insertExpense(Expense(title = "Dinner at Vue", amount = 124.50, dateMillis = System.currentTimeMillis(), category = "Food"))
                    repository.insertExpense(Expense(title = "Monthly Rent", amount = 2100.00, dateMillis = System.currentTimeMillis(), category = "Housing"))
                    repository.insertExpense(Expense(title = "Shell Station", amount = 45.00, dateMillis = System.currentTimeMillis(), category = "Transport"))
                } else if (list.isNotEmpty()) {
                    preferences.edit().putBoolean("sample_expenses_seeded", true).apply()
                }
            }
        }
        viewModelScope.launch {
            repository.allHabits.collect { list ->
                if (list.isEmpty() && !preferences.getBoolean("sample_habits_seeded", false)) {
                    preferences.edit().putBoolean("sample_habits_seeded", true).apply()
                    repository.insertHabit(Habit(name = "Morning Meditation", frequency = "DAILY", color = "#776F9F", icon = "self_improvement", currentStreak = 5, longestStreak = 12))
                    repository.insertHabit(Habit(name = "Read 30 Minutes", frequency = "DAILY", color = "#26836C", icon = "menu_book", currentStreak = 3, longestStreak = 7))
                    repository.insertHabit(Habit(name = "Drink 8 Glasses Water", frequency = "DAILY", color = "#6857C8", icon = "water_drop", currentStreak = 8, longestStreak = 8))
                    repository.insertHabit(Habit(name = "Evening Walk", frequency = "DAILY", color = "#B58A37", icon = "directions_walk", currentStreak = 2, longestStreak = 14))
                } else if (list.isNotEmpty()) {
                    preferences.edit().putBoolean("sample_habits_seeded", true).apply()
                }
            }
        }
        viewModelScope.launch {
            repository.allNotes.collect { list ->
                if (list.isEmpty() && !preferences.getBoolean("sample_notes_seeded", false)) {
                    preferences.edit().putBoolean("sample_notes_seeded", true).apply()
                    repository.insertNote(Note(title = "Project Ideas", content = "Build a habit tracker app with streak animations and daily reminders.", isPinned = true, tags = "work,ideas"))
                    repository.insertNote(Note(title = "Book Recommendations", content = "1. Atomic Habits\n2. Deep Work\n3. The Psychology of Money", tags = "books,reading"))
                } else if (list.isNotEmpty()) {
                    preferences.edit().putBoolean("sample_notes_seeded", true).apply()
                }
            }
        }
    }
}

