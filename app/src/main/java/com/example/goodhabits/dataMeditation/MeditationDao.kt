package com.example.goodhabits.dataMeditation

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MeditationDao {
    @Query("SELECT * FROM meditation_activities ORDER BY id ASC")
    fun getAllActivities(): Flow<List<MeditationActivity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertActivity(activity: MeditationActivity)

    @Delete
    suspend fun deleteActivity(activity: MeditationActivity)
}
