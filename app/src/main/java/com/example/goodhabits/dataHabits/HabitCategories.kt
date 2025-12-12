package com.example.goodhabits.dataHabits

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class HabitCategory(val name: String, val color: Color, val icon: ImageVector)

val habitCategories = listOf(
    HabitCategory("Físico", Color(0xFF4CAF50), Icons.Default.FitnessCenter),
    HabitCategory("Mental", Color(0xFF2196F3), Icons.Default.Lightbulb),
    HabitCategory("Social", Color(0xFFE91E63), Icons.Default.People),
    HabitCategory("Afectivo", Color(0xFFFF5722), Icons.Default.Favorite),
    HabitCategory("Intelectual", Color(0xFF9C27B0), Icons.Default.School),
    HabitCategory("Recreativo", Color(0xFFFFC107), Icons.Default.SportsEsports)
)
