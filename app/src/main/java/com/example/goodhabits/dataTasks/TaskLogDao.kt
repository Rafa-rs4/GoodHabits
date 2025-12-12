package com.example.goodhabits.dataTasks

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskLogDao {
    @Query("SELECT * FROM task_logs WHERE taskId = :taskId")
    fun getLogsForTask(taskId: Int): Flow<List<TaskLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: TaskLog)

    @Query("DELETE FROM task_logs WHERE taskId = :taskId AND completionDate = :completionDate")
    suspend fun deleteLog(taskId: Int, completionDate: Long)
}
