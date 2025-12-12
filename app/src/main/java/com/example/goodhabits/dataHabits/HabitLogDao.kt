package com.example.goodhabits.dataHabits

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: HabitLog)

    // Obtiene todos los logs, útil para el cálculo de progreso
    @Query("SELECT * FROM habit_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<HabitLog>>

    // Elimina un log para un hábito específico en un día concreto
    @Query("DELETE FROM habit_logs WHERE habitId = :habitId AND timestamp >= :dayStart AND timestamp < :dayEnd")
    suspend fun deleteLogForDay(habitId: Int, dayStart: Long, dayEnd: Long)
}
