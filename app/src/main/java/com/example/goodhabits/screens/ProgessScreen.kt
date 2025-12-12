package com.example.goodhabits.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goodhabits.dataTasks.Task
import com.example.goodhabits.viewmodel.ProgressViewModel
import com.example.goodhabits.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProgressScreen(
    progressViewModel: ProgressViewModel,
    taskViewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val stats by progressViewModel.progressStats.collectAsState()
    val tasks by taskViewModel.tasks.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF2196F3), Color(0xFF4CAF50)),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 2000f)
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    text = "Tu Progreso",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = Color.White, fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            item {
                StatsRow(stats = stats)
            }

            item {
                Text(
                    text = "Mapa de Contribución de Hábitos",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White, fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            item {
                ContributionGraph(contributionData = stats.completionPerDay)
            }

            item {
                Text(
                    text = "Seguimiento de Tareas",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White, fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                )
            }

            items(tasks) { task ->
                TaskProgressCard(task = task, taskViewModel = taskViewModel)
            }
        }
    }
}

@Composable
fun TaskProgressCard(task: Task, taskViewModel: TaskViewModel) {
    val taskLogs by taskViewModel.getLogsForTask(task.id).collectAsState(initial = emptyList())
    val completionDates = taskLogs.map {
        val cal = Calendar.getInstance()
        cal.timeInMillis = it.completionDate
        cal.get(Calendar.DAY_OF_YEAR)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = task.name,
                style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Fecha límite: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(task.deadline))}",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val weekDays = listOf("L", "M", "X", "J", "V", "S", "D")
                val calendar = Calendar.getInstance()
                val today = calendar.get(Calendar.DAY_OF_YEAR)
                for (i in 0..6) {
                    val day = today - (6 - i)
                    val cal = Calendar.getInstance()
                    cal.set(Calendar.DAY_OF_YEAR, day)
                    val isCompleted = completionDates.contains(cal.get(Calendar.DAY_OF_YEAR))
                    val dayOfWeekIndex = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = weekDays[dayOfWeekIndex], color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(if (isCompleted) Color(0xFF8BC34A) else Color.Gray.copy(alpha = 0.3f))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatsRow(stats: com.example.goodhabits.viewmodel.ProgressStats) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        StatCard(icon = Icons.Default.CheckCircle, label = "Total", value = stats.totalCompletions.toString())
        StatCard(icon = Icons.Default.LocalFireDepartment, label = "Racha Actual", value = stats.currentStreak.toString())
        StatCard(icon = Icons.Default.Star, label = "Mejor Racha", value = stats.longestStreak.toString())
    }
}

@Composable
fun StatCard(icon: ImageVector, label: String, value: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)),
        modifier = Modifier.padding(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
            Text(text = label, style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.8f)))
        }
    }
}

@Composable
fun ContributionGraph(contributionData: Map<Long, Int>) {
    val calendar = Calendar.getInstance()
    val today = calendar.time
    calendar.add(Calendar.DAY_OF_YEAR, -180) // Muestra los últimos ~6 meses
    val startDate = calendar.time

    val dayMillis = 1000 * 60 * 60 * 24L
    val days = mutableListOf<Long>()
    var currentDay = startDate.time
    while (currentDay <= today.time) {
        days.add(currentDay)
        currentDay += dayMillis
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                listOf("D", "L", "M", "M", "J", "V", "S").forEach { day ->
                    Text(text = day, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            LazyHorizontalGrid(
                rows = GridCells.Fixed(7),
                contentPadding = PaddingValues(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.height(150.dp) // Altura fija para el gráfico
            ) {
                items(days.size) { index ->
                    val dayTimestamp = days[index]
                    calendar.timeInMillis = dayTimestamp
                    val dayStart = calendar.clone() as Calendar
                    dayStart.set(Calendar.HOUR_OF_DAY, 0)
                    dayStart.set(Calendar.MINUTE, 0)
                    dayStart.set(Calendar.SECOND, 0)
                    dayStart.set(Calendar.MILLISECOND, 0)
                    
                    val completions = contributionData[dayStart.timeInMillis] ?: 0
                    val color = when {
                        completions >= 5 -> Color(0xFF4CAF50) // Verde oscuro
                        completions >= 3 -> Color(0xFF8BC34A)
                        completions >= 1 -> Color(0xFFCDDC39)
                        else -> Color.Gray.copy(alpha = 0.3f)
                    }
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                    )
                }
            }
        }
    }
}
