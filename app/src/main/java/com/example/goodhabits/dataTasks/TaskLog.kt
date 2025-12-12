package com.example.goodhabits.dataTasks

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_logs")
data class TaskLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val taskId: Int,
    val completionDate: Long
)
