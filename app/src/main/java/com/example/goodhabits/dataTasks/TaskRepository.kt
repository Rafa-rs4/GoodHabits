package com.example.goodhabits.dataTasks

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao, private val taskLogDao: TaskLogDao) {

    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    fun getLogsForTask(taskId: Int): Flow<List<TaskLog>> = taskLogDao.getLogsForTask(taskId)

    suspend fun insertLog(log: TaskLog) {
        taskLogDao.insertLog(log)
    }

    suspend fun deleteLog(taskId: Int, completionDate: Long) {
        taskLogDao.deleteLog(taskId, completionDate)
    }
}
