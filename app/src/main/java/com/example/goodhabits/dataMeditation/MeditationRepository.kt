package com.example.goodhabits.dataMeditation

import kotlinx.coroutines.flow.Flow

class MeditationRepository(private val meditationDao: MeditationDao) {
    fun getAllActivities(): Flow<List<MeditationActivity>> = meditationDao.getAllActivities()

    suspend fun insertActivity(activity: MeditationActivity) {
        meditationDao.insertActivity(activity)
    }

    suspend fun deleteActivity(activity: MeditationActivity) {
        meditationDao.deleteActivity(activity)
    }
}
