package com.example.goodhabits.screens

import com.example.goodhabits.viewmodel.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class BottomNavItem(val label: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    habitViewModel: HabitViewModel,
    nutritionViewModel: NutritionViewModel,
    progressViewModel: ProgressViewModel,
    meditationViewModel: MeditationViewModel,
    taskViewModel: TaskViewModel,
    exerciseViewModel: ExerciseViewModel,
    userName: String,
    onNavigateToLogin: () -> Unit,
    onNavigateToSettings: () -> Unit // Added
) {
    var selectedIndex by remember { mutableStateOf(0) }
    val items = listOf(
        BottomNavItem("Hábitos", Icons.Default.FitnessCenter),
        BottomNavItem("Checklist", Icons.Default.Check),
        BottomNavItem("Progreso", Icons.Default.Home),
        BottomNavItem("Nutrición", Icons.Default.Restaurant),
        BottomNavItem("Meditación", Icons.Default.Timer),
        BottomNavItem("Ejercicio", Icons.Default.DirectionsRun)
    )

    var menuExpanded by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    if (showAboutDialog) {
        AboutDialog(onDismiss = { showAboutDialog = false })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = items[selectedIndex].label) },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menú de opciones")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Configuración") },
                            onClick = { 
                                menuExpanded = false
                                onNavigateToSettings() // Changed
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Acerca de") },
                            onClick = { 
                                showAboutDialog = true
                                menuExpanded = false 
                            }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text("Cerrar Sesión") },
                            onClick = { 
                                menuExpanded = false
                                onNavigateToLogin()
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFFFF9800), Color(0xFFFFC107))
                        )
                    )
                    .shadow(8.dp, RoundedCornerShape(32.dp))
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp
                ) {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedIndex == index,
                            onClick = { selectedIndex = index },
                            icon = {
                                Icon(
                                    item.icon,
                                    contentDescription = item.label,
                                    tint = if (selectedIndex == index) Color.White else Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier.size(if (selectedIndex == index) 30.dp else 24.dp)
                                )
                            },
                            label = {
                                Text(
                                    item.label,
                                    color = if (selectedIndex == index) Color.White else Color.White.copy(alpha = 0.5f),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            alwaysShowLabel = true
                        )
                    }
                }
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedIndex) {
                0 -> HabitsScreen(habitViewModel = habitViewModel, taskViewModel = taskViewModel, userName = userName)
                1 -> ChecklistScreen(habitViewModel = habitViewModel)
                2 -> ProgressScreen(progressViewModel = progressViewModel, taskViewModel = taskViewModel)
                3 -> NutritionScreen(nutritionViewModel = nutritionViewModel)
                4 -> MeditationScreen(meditationViewModel = meditationViewModel)
                5 -> ExerciseScreen(viewModel = exerciseViewModel)
            }
        }
    }
}

@Composable
private fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Acerca de GoodHabits") },
        text = { 
            Text(
                "GoodHabits es tu compañero personal para construir un estilo de vida más saludable y productivo. " +
                "Registra tus hábitos, sigue tu progreso, planifica tu nutrición y mantente activo. " +
                "¡Convierte pequeñas acciones en grandes resultados!",
                textAlign = TextAlign.Justify
            )
         },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}