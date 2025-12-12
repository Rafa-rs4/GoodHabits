package com.example.goodhabits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodhabits.dataHabits.Habit
import com.example.goodhabits.dataHabits.HabitRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

// Modelo de datos para las estadísticas de progreso
data class ProgressStats(
    val totalCompletions: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val completionPerDay: Map<Long, Int> = emptyMap() // Timestamp del inicio del día -> Count
)

class ProgressViewModel(private val repository: HabitRepository) : ViewModel() {

    // El StateFlow que calcula las estadísticas
    val progressStats: StateFlow<ProgressStats> = repository.getAllLogs()
        .map { logs ->
            if (logs.isEmpty()) return@map ProgressStats()

            val calendar = Calendar.getInstance()

            // Normaliza las fechas de los logs al inicio del día
            val dailyCompletions = logs.groupBy {
                calendar.timeInMillis = it.timestamp
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.timeInMillis
            }.mapValues { it.value.size }

            // Calcular rachas
            val sortedDates = dailyCompletions.keys.sortedDescending()
            var currentStreak = 0
            var longestStreak = 0
            var streak = 0

            if (sortedDates.isNotEmpty()) {
                val today = Calendar.getInstance()
                today.set(Calendar.HOUR_OF_DAY, 0)
                today.set(Calendar.MINUTE, 0)
                today.set(Calendar.SECOND, 0)
                today.set(Calendar.MILLISECOND, 0)

                // Comprobar si la racha incluye hoy o ayer
                val firstDate = Calendar.getInstance().apply { timeInMillis = sortedDates.first() }
                if (firstDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    (firstDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) ||
                     firstDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) - 1)) {
                    streak = 1
                    currentStreak = 1
                    longestStreak = 1
                }
            }
            
            for (i in 0 until sortedDates.size - 1) {
                val current = Calendar.getInstance().apply { timeInMillis = sortedDates[i] }
                val next = Calendar.getInstance().apply { timeInMillis = sortedDates[i+1] }

                current.add(Calendar.DAY_OF_YEAR, -1) // Día anterior

                if (current.get(Calendar.YEAR) == next.get(Calendar.YEAR) &&
                    current.get(Calendar.DAY_OF_YEAR) == next.get(Calendar.DAY_OF_YEAR)) {
                    streak++
                } else {
                    if (streak > longestStreak) {
                        longestStreak = streak
                    }
                    streak = 1 // Reiniciar la racha
                }
            }

            if (streak > longestStreak) {
                longestStreak = streak
            }
            
            // Asignar racha actual si la racha más reciente es la que se estaba contando
            if (sortedDates.size > 1) {
                 val current = Calendar.getInstance().apply { timeInMillis = sortedDates[0] }
                 val next = Calendar.getInstance().apply { timeInMillis = sortedDates[1] }
                 current.add(Calendar.DAY_OF_YEAR, -1)
                if (current.get(Calendar.YEAR) == next.get(Calendar.YEAR) &&
                    current.get(Calendar.DAY_OF_YEAR) == next.get(Calendar.DAY_OF_YEAR)){
                       currentStreak = streak
                    }
            }

            ProgressStats(
                totalCompletions = logs.size,
                currentStreak = currentStreak,
                longestStreak = longestStreak,
                completionPerDay = dailyCompletions
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = ProgressStats()
        )
}
