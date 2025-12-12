package com.example.goodhabits.dataMeditation

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meditation_activities")
data class MeditationActivity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val iconName: String // Guardamos el nombre del ícono para recuperarlo después
)
