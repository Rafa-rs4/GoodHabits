package com.example.goodhabits.dataHabits

import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class HabitRepository(
    private val habitDao: HabitDao,
    private val habitLogDao: HabitLogDao
) {

    fun getAllHabits(): Flow<List<Habit>> {
        return habitDao.getAllHabits()
    }

    suspend fun insertHabit(habit: Habit) {
        habitDao.insertHabit(habit)
    }

    suspend fun updateHabit(habit: Habit) {
        habitDao.updateHabit(habit)
    }

    suspend fun deleteHabit(habit: Habit) {
        habitDao.deleteHabit(habit)
    }


    fun getAllLogs(): Flow<List<HabitLog>> {
        return habitLogDao.getAllLogs()
    }


    suspend fun logHabitCompletion(habitId: Int, timestamp: Long) {
        val log = HabitLog(habitId = habitId, timestamp = timestamp)
        habitLogDao.insert(log)
    }


    suspend fun removeHabitLogForToday(habitId: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val dayStart = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val dayEnd = calendar.timeInMillis
        
        habitLogDao.deleteLogForDay(habitId, dayStart, dayEnd)
    }
}
