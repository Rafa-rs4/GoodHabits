package com.example.goodhabits.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BreakfastDining
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.goodhabits.viewmodel.NutritionViewModel

data class MealOption(val type: String, val color: Color, val icon: ImageVector)

@Composable
fun NutritionScreen(nutritionViewModel: NutritionViewModel) {
    val mealOptions = listOf(
        MealOption("Desayuno", Color(0xFF81C784), Icons.Default.BreakfastDining),
        MealOption("Almuerzo", Color(0xFFFFB74D), Icons.Default.LunchDining),
        MealOption("Cena", Color(0xFFE57373), Icons.Default.DinnerDining)
    )

    val breakfastNote by nutritionViewModel.breakfastNote.collectAsState()
    val lunchNote by nutritionViewModel.lunchNote.collectAsState()
    val dinnerNote by nutritionViewModel.dinnerNote.collectAsState()

    // Estado para gestionar qué diálogo de comida se muestra
    var selectedMeal by remember { mutableStateOf<MealOption?>(null) }
    
    fun getNoteForMealType(mealType: String): String {
        return when (mealType) {
            "Desayuno" -> breakfastNote
            "Almuerzo" -> lunchNote
            "Cena" -> dinnerNote
            else -> ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2))
                )
            )
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Plan de Alimentación 🍏",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00796B)
                )
            )
            Text(
                text = "Registra lo que planeas comer en cada comida",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF00796B)
            )

            mealOptions.forEach { mealOption ->
                val currentDisplayNote = getNoteForMealType(mealOption.type)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .clickable { selectedMeal = mealOption },
                    colors = CardDefaults.cardColors(containerColor = mealOption.color)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color.White.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = mealOption.icon,
                                contentDescription = mealOption.type,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = mealOption.type,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            if (currentDisplayNote.isNotEmpty()) {
                                Text(
                                    text = currentDisplayNote,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }

        // Muestra el nuevo diálogo personalizado cuando se selecciona una comida
        selectedMeal?.let { meal ->
            AddMealDialog(
                mealOption = meal,
                initialNote = getNoteForMealType(meal.type),
                onDismiss = { selectedMeal = null },
                onSave = { note ->
                    nutritionViewModel.addOrUpdateMealPlan(meal.type, note)
                    selectedMeal = null
                }
            )
        }
    }
}

// Nuevo diálogo personalizado para añadir/editar una nota de comida
@Composable
fun AddMealDialog(
    mealOption: MealOption,
    initialNote: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var noteText by remember { mutableStateOf(initialNote) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Encabezado con icono y título
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(mealOption.color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = mealOption.icon,
                        contentDescription = mealOption.type,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Añadir nota para ${mealOption.type}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = mealOption.color
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Campo de texto
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Escribe tu plan de comida...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp), // Más espacio para las notas
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = mealOption.color,
                        cursorColor = mealOption.color
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSave(noteText) },
                        colors = ButtonDefaults.buttonColors(containerColor = mealOption.color)
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}
