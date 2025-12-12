package com.example.goodhabits.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.goodhabits.dataMeditation.MeditationActivity
import com.example.goodhabits.viewmodel.MeditationViewModel
import kotlinx.coroutines.delay

// Mapa para convertir el nombre del ícono guardado en un ImageVector
val iconMap = mapOf(
    "Timer" to Icons.Default.Timer,
    "FitnessCenter" to Icons.Default.FitnessCenter,
    "SelfImprovement" to Icons.Default.SelfImprovement,
    "Book" to Icons.Default.Book,
    "Work" to Icons.Default.Work
)

@Composable
fun MeditationScreen(meditationViewModel: MeditationViewModel, modifier: Modifier = Modifier) {
    val activities by meditationViewModel.activities.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    // Añade una actividad por defecto si la lista está vacía
    LaunchedEffect(activities) {
        if (activities.isEmpty()) {
            meditationViewModel.addActivity(MeditationActivity(name = "Meditación", iconName = "SelfImprovement"))
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                shape = CircleShape,
                containerColor = Color(0xFFE91E63)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Actividad", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF673AB7), Color(0xFF3F51B5)),
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 2000f)
                    )
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Temporizadores",
                    style = MaterialTheme.typography.headlineLarge.copy(color = Color.White, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(activities) { activity ->
                        TimerCard(
                            activity = activity,
                            onDelete = { meditationViewModel.deleteActivity(activity) }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddActivityDialog(
                onDismiss = { showAddDialog = false },
                onAddActivity = { name, iconName ->
                    meditationViewModel.addActivity(MeditationActivity(name = name, iconName = iconName))
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun TimerCard(activity: MeditationActivity, onDelete: () -> Unit) {
    var seconds by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000)
            seconds++
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = iconMap[activity.iconName] ?: Icons.Default.Timer,
                    contentDescription = activity.name,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = activity.name,
                    style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.White.copy(alpha = 0.7f))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "%02d:%02d:%02d".format(seconds / 3600, (seconds % 3600) / 60, seconds % 60),
                style = MaterialTheme.typography.displayMedium.copy(color = Color.White, fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { isRunning = !isRunning },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) Color(0xFFF44336) else Color(0xFF4CAF50)
                    )
                ) {
                    Icon(if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = "Iniciar/Pausar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isRunning) "Pausar" else "Iniciar")
                }
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedButton(
                    onClick = {
                        isRunning = false
                        seconds = 0
                    },
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.7f))
                ) {
                    Text("Reiniciar", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun AddActivityDialog(
    onDismiss: () -> Unit,
    onAddActivity: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedIconName by remember { mutableStateOf(iconMap.keys.first()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Nueva Actividad", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de la actividad") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Seleccionar Ícono", style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    iconMap.forEach { (iconName, icon) ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (selectedIconName == iconName) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent)
                                .clickable { selectedIconName = iconName },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, contentDescription = iconName, tint = if (selectedIconName == iconName) MaterialTheme.colorScheme.primary else Color.Gray)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.align(Alignment.End)) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        if (name.isNotBlank()) {
                            onAddActivity(name, selectedIconName)
                        }
                    }) {
                        Text("Añadir")
                    }
                }
            }
        }
    }
}
