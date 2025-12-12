package com.example.goodhabits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodhabits.dataNutrition.MealPlan
import com.example.goodhabits.dataNutrition.NutritionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NutritionViewModel(private val repository: NutritionRepository) : ViewModel() {

    // StateFlow para todos los planes de comida
    val allMealPlans: StateFlow<List<MealPlan>> = repository.getAllMealPlans()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    // Para manejar el plan de comida actualmente seleccionado o editándose
    private val _selectedMealPlan = MutableStateFlow<MealPlan?>(null)
    val selectedMealPlan: StateFlow<MealPlan?> = _selectedMealPlan.asStateFlow()

    // Para obtener planes de comida por tipo (Desayuno, Almuerzo, Cena)
    // Esto podría ser útil si quieres mostrar notas separadas por tipo de comida
    // directamente desde el ViewModel.
    fun getMealPlansByType(mealType: String): StateFlow<List<MealPlan>> {
        return repository.getMealPlansByType(mealType)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = emptyList()
            )
    }

    // Combina los planes de comida con las notas guardadas para cada tipo de comida principal
    // Asumiendo que quieres mostrar la última nota para cada tipo de comida en la vista principal
    val breakfastNote: StateFlow<String> = repository.getMealPlansByType("Desayuno")
        .combine(selectedMealPlan) { plans, selected ->
            selected?.takeIf { it.mealType == "Desayuno" }?.note ?: plans.firstOrNull()?.note ?: ""
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    val lunchNote: StateFlow<String> = repository.getMealPlansByType("Almuerzo")
        .combine(selectedMealPlan) { plans, selected ->
            selected?.takeIf { it.mealType == "Almuerzo" }?.note ?: plans.firstOrNull()?.note ?: ""
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    val dinnerNote: StateFlow<String> = repository.getMealPlansByType("Cena")
        .combine(selectedMealPlan) { plans, selected ->
            selected?.takeIf { it.mealType == "Cena" }?.note ?: plans.firstOrNull()?.note ?: ""
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, "")


    fun setSelectedMealPlanById(id: Int) {
        viewModelScope.launch {
            _selectedMealPlan.value = repository.getMealPlanById(id)
        }
    }

    fun clearSelectedMealPlan() {
        _selectedMealPlan.value = null
    }

    fun addOrUpdateMealPlan(mealType: String, note: String) {
        viewModelScope.launch {
            // Intenta encontrar un plan existente para este tipo de comida (la lógica puede variar aquí)
            // Por simplicidad, aquí se asume que actualizamos el más reciente o creamos uno nuevo.
            // Para una UI más compleja, podrías querer seleccionar un plan específico para actualizar.
            val existingPlan = allMealPlans.value.firstOrNull { it.mealType == mealType }

            if (existingPlan != null && note.isNotEmpty()) {
                val updatedPlan = existingPlan.copy(note = note, date = System.currentTimeMillis())
                repository.updateMealPlan(updatedPlan)
                _selectedMealPlan.value = updatedPlan
            } else if (note.isNotEmpty()) {
                val newPlan = MealPlan(mealType = mealType, note = note)
                repository.insertMealPlan(newPlan)
                // Opcional: podrías querer cargar el plan recién insertado en _selectedMealPlan si tiene un ID generado
            } else if (existingPlan != null && note.isEmpty()){
                 repository.deleteMealPlan(existingPlan) // Borra si la nota se vacía
                 _selectedMealPlan.value = null
            }
        }
    }

    fun deleteMealPlan(mealPlan: MealPlan) {
        viewModelScope.launch {
            repository.deleteMealPlan(mealPlan)
            if (_selectedMealPlan.value?.id == mealPlan.id) {
                _selectedMealPlan.value = null
            }
        }
    }
}
