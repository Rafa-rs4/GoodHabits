package com.example.goodhabits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodhabits.dataHabits.Habit
import com.example.goodhabits.dataHabits.HabitRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date

class HabitViewModel(private val repository: HabitRepository) : ViewModel() {


    val habits: StateFlow<List<Habit>> = repository.getAllHabits()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    fun addHabit(habit: Habit) {
        viewModelScope.launch {
            repository.insertHabit(habit)
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            repository.updateHabit(habit)
        }
    }



    fun onHabitChecked(habit: Habit, isChecked: Boolean) {
        viewModelScope.launch {

            val updatedHabit = habit.copy(isCompleted = isChecked)
            repository.updateHabit(updatedHabit)

            if (isChecked) {
                repository.logHabitCompletion(habit.id, Date().time)
            } else {
               repository.removeHabitLogForToday(habit.id)
            }
        }
    }
}
