package com.example.goodhabits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodhabits.dataTasks.Task
import com.example.goodhabits.dataTasks.TaskLog
import com.example.goodhabits.dataTasks.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    val tasks: StateFlow<List<Task>> = repository.getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun getLogsForTask(taskId: Int): Flow<List<TaskLog>> {
        return repository.getLogsForTask(taskId)
    }

    fun logTaskCompletion(taskId: Int, completionDate: Long) {
        viewModelScope.launch {
            repository.insertLog(TaskLog(taskId = taskId, completionDate = completionDate))
        }
    }

    fun removeTaskLog(taskId: Int, completionDate: Long) {
        viewModelScope.launch {
            repository.deleteLog(taskId, completionDate)
        }
    }
}
