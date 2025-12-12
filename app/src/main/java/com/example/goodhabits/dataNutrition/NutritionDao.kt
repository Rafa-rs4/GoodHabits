package com.example.goodhabits.dataNutrition

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NutritionDao {
    @Query("SELECT * FROM meal_plans ORDER BY date DESC")
    fun getAllMealPlans(): Flow<List<MealPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealPlan(mealPlan: MealPlan)

    @Update
    suspend fun updateMealPlan(mealPlan: MealPlan)

    @Delete
    suspend fun deleteMealPlan(mealPlan: MealPlan)

    @Query("SELECT * FROM meal_plans WHERE mealType = :mealType ORDER BY date DESC")
    fun getMealPlansByType(mealType: String): Flow<List<MealPlan>>

    @Query("SELECT * FROM meal_plans WHERE id = :id")
    suspend fun getMealPlanById(id: Int): MealPlan?
}
