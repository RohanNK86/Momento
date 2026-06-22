package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MomentoViewModel(application: Application) : AndroidViewModel(application) {
    private val database = MomentoDatabase.getDatabase(application)
    private val repository = MomentoRepository(
        database.taskDao(),
        database.goalDao(),
        database.eventDao(),
        database.expenseDao()
    )

    val tasks: StateFlow<List<Task>> = repository.allTasks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    val goals: StateFlow<List<Goal>> = repository.allGoals.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    val events: StateFlow<List<Event>> = repository.allEvents.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    val expenses: StateFlow<List<Expense>> = repository.allExpenses.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Handlers
    fun toggleTaskCompletion(task: Task) = viewModelScope.launch {
        repository.updateTask(task.copy(isCompleted = !task.isCompleted))
    }
    
    fun addTask(title: String, priority: Task.Priority) = viewModelScope.launch {
        repository.insertTask(Task(title = title, priority = priority, isToday = true))
    }

    fun addGoal(title: String, progress: Float) = viewModelScope.launch {
        repository.insertGoal(Goal(title = title, targetDateMillis = System.currentTimeMillis() + 864000000L, progress = progress))
    }
    
    fun addEvent(title: String, timeMillis: Long, category: String) = viewModelScope.launch {
        repository.insertEvent(Event(title = title, timeMillis = timeMillis, category = category))
    }
    
    fun addExpense(title: String, amount: Double, category: String) = viewModelScope.launch {
         repository.insertExpense(Expense(title = title, amount = amount, dateMillis = System.currentTimeMillis(), category = category))
    }

    init {
        // Pre-populate with mock data if empty
        viewModelScope.launch {
            repository.allTasks.collect { currentTasks ->
                if (currentTasks.isEmpty()) {
                    addTask("Finalize Q4 Marketing Deck", Task.Priority.HIGH)
                    addTask("Review Design System Updates", Task.Priority.MEDIUM)
                    addTask("Schedule Team 1:1s", Task.Priority.LOW)
                }
            }
        }
        viewModelScope.launch {
            repository.allGoals.collect { currentGoals ->
                if (currentGoals.isEmpty()) {
                    repository.insertGoal(Goal(title = "Launch MVP", targetDateMillis = System.currentTimeMillis(), progress = 0.75f, status = Goal.GoalStatus.ON_TRACK))
                    repository.insertGoal(Goal(title = "Learn WebGL", targetDateMillis = System.currentTimeMillis(), progress = 0.40f, status = Goal.GoalStatus.ON_TRACK))
                    repository.insertGoal(Goal(title = "Run 5k Daily", targetDateMillis = System.currentTimeMillis(), progress = 0.20f, status = Goal.GoalStatus.AT_RISK))
                    repository.insertGoal(Goal(title = "Read 12 Books", targetDateMillis = System.currentTimeMillis(), progress = 1.0f, status = Goal.GoalStatus.COMPLETED))
                    repository.insertGoal(Goal(title = "Emergency Fund", targetDateMillis = System.currentTimeMillis(), progress = 1.0f, status = Goal.GoalStatus.COMPLETED))
                }
            }
        }
        viewModelScope.launch {
            repository.allExpenses.collect { currentExpenses ->
                if (currentExpenses.isEmpty()) {
                    addExpense("Dinner at Vue", 124.50, "Food")
                    addExpense("Monthly Rent", 2100.00, "Housing")
                    addExpense("Shell Station", 45.00, "Transport")
                }
            }
        }
        viewModelScope.launch {
            repository.allEvents.collect { currentEvents ->
                if (currentEvents.isEmpty()) {
                    addEvent("Design Sync", System.currentTimeMillis(), "Work")
                    addEvent("Gym Session", System.currentTimeMillis() + 86400000, "Personal")
                    addEvent("Dinner with Sarah", System.currentTimeMillis() + 172800000, "Personal")
                    addEvent("Dentist Appointment", System.currentTimeMillis() + 259200000, "Health")
                }
            }
        }
    }
}
