package com.example.data

import kotlinx.coroutines.flow.Flow

class MomentoRepository(
    private val taskDao: TaskDao,
    private val goalDao: GoalDao,
    private val eventDao: EventDao,
    private val expenseDao: ExpenseDao
) {
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()
    val allGoals: Flow<List<Goal>> = goalDao.getAllGoals()
    val allEvents: Flow<List<Event>> = eventDao.getAllEvents()
    val allExpenses: Flow<List<Expense>> = expenseDao.getAllExpenses()

    suspend fun insertTask(task: Task) = taskDao.insertTask(task)
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    suspend fun deleteTask(id: Int) = taskDao.deleteTaskById(id)

    suspend fun insertGoal(goal: Goal) = goalDao.insertGoal(goal)
    suspend fun updateGoal(goal: Goal) = goalDao.updateGoal(goal)
    suspend fun deleteGoal(id: Int) = goalDao.deleteGoalById(id)

    suspend fun insertEvent(event: Event) = eventDao.insertEvent(event)
    suspend fun deleteEvent(id: Int) = eventDao.deleteEventById(id)

    suspend fun insertExpense(expense: Expense) = expenseDao.insertExpense(expense)
}
