package com.example.goodhabits.dataNutrition

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_plans")
data class MealPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mealType: String, // "Desayuno", "Almuerzo", "Cena"
    val note: String,
    val date: Long = System.currentTimeMillis() // Para registrar cuándo se creó o para qué día es
)
