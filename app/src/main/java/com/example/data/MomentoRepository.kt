package com.example.data

import kotlinx.coroutines.flow.Flow

class MomentoRepository(
    private val taskDao: TaskDao,
    private val goalDao: GoalDao,
    private val eventDao: EventDao,
    private val expenseDao: ExpenseDao,
    private val habitDao: HabitDao,
    private val noteDao: NoteDao,
    private val userProfileDao: UserProfileDao
) {
    // Streams
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()
    val allGoals: Flow<List<Goal>> = goalDao.getAllGoals()
    val allEvents: Flow<List<Event>> = eventDao.getAllEvents()
    val allExpenses: Flow<List<Expense>> = expenseDao.getAllExpenses()
    val allHabits: Flow<List<Habit>> = habitDao.getAllHabits()
    val allNotes: Flow<List<Note>> = noteDao.getAllNotes()
    val userProfile: Flow<UserProfile?> = userProfileDao.getProfile()

    // Tasks
    suspend fun insertTask(task: Task) = taskDao.insertTask(task)
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    suspend fun deleteTask(id: Int) = taskDao.deleteTaskById(id)

    // Goals
    suspend fun insertGoal(goal: Goal) = goalDao.insertGoal(goal)
    suspend fun updateGoal(goal: Goal) = goalDao.updateGoal(goal)
    suspend fun deleteGoal(id: Int) = goalDao.deleteGoalById(id)

    // Events
    suspend fun insertEvent(event: Event) = eventDao.insertEvent(event)
    suspend fun updateEvent(event: Event) = eventDao.updateEvent(event)
    suspend fun deleteEvent(id: Int) = eventDao.deleteEventById(id)

    // Expenses
    suspend fun insertExpense(expense: Expense) = expenseDao.insertExpense(expense)
    suspend fun deleteExpense(id: Int) = expenseDao.deleteExpenseById(id)

    // Habits
    suspend fun insertHabit(habit: Habit) = habitDao.insertHabit(habit)
    suspend fun updateHabit(habit: Habit) = habitDao.updateHabit(habit)
    suspend fun deleteHabit(id: Int) = habitDao.deleteHabitById(id)

    // Notes
    suspend fun insertNote(note: Note) = noteDao.insertNote(note)
    suspend fun updateNote(note: Note) = noteDao.updateNote(note)
    suspend fun deleteNote(id: Int) = noteDao.deleteNoteById(id)

    // Profile
    suspend fun saveProfile(profile: UserProfile) = userProfileDao.insertOrUpdateProfile(profile)
}
