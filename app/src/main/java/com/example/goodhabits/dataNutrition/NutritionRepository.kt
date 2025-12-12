package com.example.goodhabits.dataNutrition

import kotlinx.coroutines.flow.Flow

class NutritionRepository(private val nutritionDao: NutritionDao) {

    fun getAllMealPlans(): Flow<List<MealPlan>> {
        return nutritionDao.getAllMealPlans()
    }

    fun getMealPlansByType(mealType: String): Flow<List<MealPlan>> {
        return nutritionDao.getMealPlansByType(mealType)
    }

    suspend fun insertMealPlan(mealPlan: MealPlan) {
        nutritionDao.insertMealPlan(mealPlan)
    }

    suspend fun updateMealPlan(mealPlan: MealPlan) {
        nutritionDao.updateMealPlan(mealPlan)
    }

    suspend fun deleteMealPlan(mealPlan: MealPlan) {
        nutritionDao.deleteMealPlan(mealPlan)
    }

    suspend fun getMealPlanById(id: Int): MealPlan? {
        return nutritionDao.getMealPlanById(id)
    }
}
