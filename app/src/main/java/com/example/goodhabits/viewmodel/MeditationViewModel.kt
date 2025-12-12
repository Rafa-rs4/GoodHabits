package com.example.goodhabits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodhabits.dataMeditation.MeditationActivity
import com.example.goodhabits.dataMeditation.MeditationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MeditationViewModel(private val repository: MeditationRepository) : ViewModel() {
    val activities: StateFlow<List<MeditationActivity>> = repository.getAllActivities()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    fun addActivity(activity: MeditationActivity) {
        viewModelScope.launch {
            repository.insertActivity(activity)
        }
    }

    fun deleteActivity(activity: MeditationActivity) {
        viewModelScope.launch {
            repository.deleteActivity(activity)
        }
    }
}
